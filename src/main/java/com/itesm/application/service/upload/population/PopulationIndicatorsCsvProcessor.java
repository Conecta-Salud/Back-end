package com.itesm.application.service.upload.population;

import com.itesm.application.service.upload.CsvStorageService;
import com.itesm.domain.models.catalog.AvailabilityStatus;
import com.itesm.domain.models.catalog.TerritoryLevel;
import com.itesm.domain.models.upload.CsvFileRole;
import com.itesm.domain.models.upload.DataAvailabilityWriteDraft;
import com.itesm.domain.models.upload.TerritoryIndicatorValueWriteDraft;
import com.itesm.domain.models.upload.UploadErrorDraft;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadErrorRepository;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.domain.repository.UploadBatchRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import com.itesm.infrastructure.persistence.repository.DataAvailabilityWriter;
import com.itesm.infrastructure.persistence.repository.TerritoryIndicatorValueWriter;
import com.itesm.infrastructure.persistence.repository.TerritoryIndicatorValueWriter.IndicatorMetadata;
import com.itesm.infrastructure.persistence.repository.TerritoryIndicatorValueWriter.MunicipalityMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class PopulationIndicatorsCsvProcessor {

    private static final int CHUNK_SIZE = 500;
    private static final short POPULATION_SOURCE_YEAR = 2020;
    private static final BigDecimal THOUSANDS_MULTIPLIER = new BigDecimal("1000");
    private static final String BASE_POPULATION_NOTE = "Dato poblacional base 2020 utilizado como referencia para el año de análisis.";
    private static final String MUNICIPAL_UNAVAILABLE_NOTE = "Indicador no disponible a nivel municipal en la fuente oficial.";
    private static final List<Short> TARGET_ANALYSIS_YEARS = List.of((short) 2018, (short) 2020, (short) 2022, (short) 2024);
    private static final Set<String> INDICATOR_CODES = Set.of(
            "total_population",
            "percentage_over_60",
            "healthcare_access_deficiency",
            "total_poverty_population"
    );
    private static final Set<CsvFileRole> SUPPORTED_ROLES = EnumSet.of(CsvFileRole.population_indicators);

    private final CsvStorageService csvStorageService;
    private final DataUploadRepository dataUploadRepository;
    private final DataUploadErrorRepository dataUploadErrorRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final PopulationIndicatorsCsvAdapter csvAdapter;
    private final TerritoryIndicatorValueWriter territoryIndicatorValueWriter;
    private final DataAvailabilityWriter dataAvailabilityWriter;

    public PopulationIndicatorsCsvProcessor(
            CsvStorageService csvStorageService,
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            UploadBatchRepository uploadBatchRepository,
            PopulationIndicatorsCsvAdapter csvAdapter,
            TerritoryIndicatorValueWriter territoryIndicatorValueWriter,
            DataAvailabilityWriter dataAvailabilityWriter
    ) {
        this.csvStorageService = csvStorageService;
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.csvAdapter = csvAdapter;
        this.territoryIndicatorValueWriter = territoryIndicatorValueWriter;
        this.dataAvailabilityWriter = dataAvailabilityWriter;
    }

    public PopulationProcessingResult process(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        List<DataUploadEntity> populationUploads = uploads.stream()
                .filter(upload -> SUPPORTED_ROLES.contains(upload.getFileRole()))
                .toList();

        if (populationUploads.isEmpty()) {
            throw new BadRequestException("INVALID_FILE_ROLE: population processing requires fileRole=population_indicators");
        }

        ProcessingCatalog catalog = loadCatalog(batch);
        boolean writeFinalData = mode != UploadProcessingMode.validate_only;

        if (writeFinalData && (mode == UploadProcessingMode.replace || replaceExistingForYear)) {
            territoryIndicatorValueWriter.deleteExistingPopulationValues(
                    batch.getDataSource().getId(),
                    catalog.indicatorIds(),
                    TARGET_ANALYSIS_YEARS
            );
        }

        PopulationProcessingResult result = new PopulationProcessingResult(0, 0, 0);

        for (DataUploadEntity upload : populationUploads) {
            result = result.add(processUpload(batch, upload, catalog, writeFinalData));
        }

        if (writeFinalData && result.records() > 0) {
            dataAvailabilityWriter.upsert(buildAvailability(catalog.indicators()));
        }

        uploadBatchRepository.recalculateCounters(batch.getId());
        return result;
    }

    private PopulationProcessingResult processUpload(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            ProcessingCatalog catalog,
            boolean writeFinalData
    ) {
        dataUploadErrorRepository.deleteByUploadId(upload.getId());

        Path path = csvStorageService.resolveStoredPath(upload.getStoredFileName());
        ChunkBuffer chunk = new ChunkBuffer();
        int totalRecords = 0;
        int validRecords = 0;
        int errorRecords = 0;
        int valuesUpserted = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_16LE)) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                UploadErrorDraft error = error(1, null, null, "EMPTY_FILE", "CSV file is empty or has no header row");
                dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
                updateUpload(upload.getId(), UploadStatus.warning, 0, 0, 1, "CSV processing found 1 error(s)");
                return new PopulationProcessingResult(0, 0, 1);
            }

            PopulationIndicatorsCsvAdapter.PopulationIndicatorsColumns columns =
                    csvAdapter.detectColumns(csvAdapter.parseCsvLine(headerLine));
            List<String> missingHeaders = csvAdapter.missingHeaders(columns);

            if (!missingHeaders.isEmpty()) {
                List<UploadErrorDraft> errors = missingHeaders.stream()
                        .map(header -> error(1, header, null, "MISSING_REQUIRED_HEADER", "Required CSV header is missing: " + header))
                        .toList();
                dataUploadErrorRepository.appendErrors(upload.getId(), errors);
                updateUpload(upload.getId(), UploadStatus.warning, 0, 0, errors.size(), "CSV processing found " + errors.size() + " error(s)");
                return new PopulationProcessingResult(0, 0, errors.size());
            }

            String line;
            int csvRowNumber = 1;
            while ((line = reader.readLine()) != null) {
                csvRowNumber++;

                if (line.isBlank()) {
                    continue;
                }

                totalRecords++;
                PopulationIndicatorsCsvRow row = csvAdapter.toRow(
                        csvRowNumber,
                        csvAdapter.parseCsvLine(line),
                        columns
                );

                RowProcessingResult rowResult = processRow(batch, upload, row, catalog);
                chunk.values().addAll(rowResult.values());
                chunk.errors().addAll(rowResult.errors());
                valuesUpserted += rowResult.values().size();
                errorRecords += rowResult.errors().size();

                if (rowResult.validRecord()) {
                    validRecords++;
                }

                if (chunk.shouldFlush()) {
                    flushChunk(upload.getId(), chunk, writeFinalData);
                    chunk.clear();
                }
            }

            flushChunk(upload.getId(), chunk, writeFinalData);

            UploadStatus status = errorRecords == 0 ? UploadStatus.completed : UploadStatus.warning;
            updateUpload(
                    upload.getId(),
                    status,
                    totalRecords,
                    validRecords,
                    errorRecords,
                    errorRecords == 0 ? null : "CSV processing found " + errorRecords + " error(s)"
            );

            return new PopulationProcessingResult(totalRecords, writeFinalData ? valuesUpserted : 0, errorRecords);
        } catch (IOException e) {
            UploadErrorDraft error = error(null, null, null, "UPLOAD_STORAGE_ERROR", "Could not read stored CSV file");
            dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
            updateUpload(upload.getId(), UploadStatus.error, totalRecords, validRecords, errorRecords + 1, "Could not read stored CSV file");
            return new PopulationProcessingResult(totalRecords, writeFinalData ? valuesUpserted : 0, errorRecords + 1);
        }
    }

    private RowProcessingResult processRow(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            PopulationIndicatorsCsvRow row,
            ProcessingCatalog catalog
    ) {
        List<UploadErrorDraft> errors = new ArrayList<>();
        List<TerritoryIndicatorValueWriteDraft> values = new ArrayList<>();

        Short period = parsePeriod(row, errors);
        TerritoryReference territory = parseTerritory(row, catalog, errors);

        if (period == null || territory == null) {
            return new RowProcessingResult(values, errors, false);
        }

        addPopulationBaseValues(batch, upload, row, territory, period, catalog, values, errors);
        addThousandsIndicatorValue(
                batch,
                upload,
                row,
                territory,
                period,
                catalog,
                "healthcare_access_deficiency",
                "Carencia por acceso a los servicios de salud",
                row.getHealthcareAccessDeficiencyRaw(),
                values,
                errors
        );
        addThousandsIndicatorValue(
                batch,
                upload,
                row,
                territory,
                period,
                catalog,
                "total_poverty_population",
                "Población en situación de pobreza",
                row.getTotalPovertyPopulationRaw(),
                values,
                errors
        );

        return new RowProcessingResult(values, errors, true);
    }

    private void addPopulationBaseValues(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            PopulationIndicatorsCsvRow row,
            TerritoryReference territory,
            Short period,
            ProcessingCatalog catalog,
            List<TerritoryIndicatorValueWriteDraft> values,
            List<UploadErrorDraft> errors
    ) {
        if (period.shortValue() != POPULATION_SOURCE_YEAR) {
            validateOptionalDecimal(row, "Población total", row.getTotalPopulationRaw(), errors);
            validateOptionalDecimal(row, "Porcentaje de población de 60 y más años", row.getPercentageOver60Raw(), errors);
            return;
        }

        BigDecimal totalPopulation = parseRequiredDecimal(
                row,
                "Población total",
                row.getTotalPopulationRaw(),
                errors
        );
        BigDecimal percentageOver60 = parseRequiredDecimal(
                row,
                "Porcentaje de población de 60 y más años",
                row.getPercentageOver60Raw(),
                errors
        );

        if (totalPopulation != null && !isWholeNumber(totalPopulation)) {
            errors.add(error(
                    row.getCsvRowNumber(),
                    "Población total",
                    row.getTotalPopulationRaw(),
                    "INVALID_NUMERIC_VALUE",
                    "Población total must be an integer value"
            ));
            totalPopulation = null;
        }

        for (Short analysisYear : TARGET_ANALYSIS_YEARS) {
            String methodologyNote = analysisYear.shortValue() == POPULATION_SOURCE_YEAR
                    ? null
                    : BASE_POPULATION_NOTE;

            if (totalPopulation != null) {
                values.add(valueDraft(
                        batch,
                        upload,
                        territory,
                        catalog.indicators().get("total_population").id(),
                        totalPopulation.stripTrailingZeros(),
                        analysisYear,
                        POPULATION_SOURCE_YEAR,
                        methodologyNote
                ));
            }

            if (percentageOver60 != null) {
                values.add(valueDraft(
                        batch,
                        upload,
                        territory,
                        catalog.indicators().get("percentage_over_60").id(),
                        percentageOver60,
                        analysisYear,
                        POPULATION_SOURCE_YEAR,
                        methodologyNote
                ));
            }
        }
    }

    private void addThousandsIndicatorValue(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            PopulationIndicatorsCsvRow row,
            TerritoryReference territory,
            Short period,
            ProcessingCatalog catalog,
            String indicatorCode,
            String columnName,
            String rawValue,
            List<TerritoryIndicatorValueWriteDraft> values,
            List<UploadErrorDraft> errors
    ) {
        if (territory.level() == TerritoryLevel.municipality) {
            if (hasText(rawValue)) {
                errors.add(error(
                        row.getCsvRowNumber(),
                        columnName,
                        rawValue,
                        "LEVEL_NOT_SUPPORTED",
                        indicatorCode + " is not available at municipality level and was ignored"
                ));
            }
            return;
        }

        BigDecimal valueInThousands = parseRequiredDecimal(row, columnName, rawValue, errors);
        if (valueInThousands == null) {
            return;
        }

        values.add(valueDraft(
                batch,
                upload,
                territory,
                catalog.indicators().get(indicatorCode).id(),
                valueInThousands.multiply(THOUSANDS_MULTIPLIER),
                period,
                period,
                null
        ));
    }

    private Short parsePeriod(PopulationIndicatorsCsvRow row, List<UploadErrorDraft> errors) {
        String rawPeriod = row.getPeriodRaw();

        if (!hasText(rawPeriod)) {
            errors.add(error(row.getCsvRowNumber(), "Periodos", rawPeriod, "REQUIRED_FIELD_MISSING", "Periodos is required"));
            return null;
        }

        try {
            short period = Short.parseShort(rawPeriod.trim());

            if (!TARGET_ANALYSIS_YEARS.contains(period)) {
                errors.add(error(row.getCsvRowNumber(), "Periodos", rawPeriod, "INVALID_YEAR", "Periodos must be one of 2018, 2020, 2022, 2024"));
                return null;
            }

            return period;
        } catch (NumberFormatException e) {
            errors.add(error(row.getCsvRowNumber(), "Periodos", rawPeriod, "INVALID_YEAR", "Periodos must be numeric"));
            return null;
        }
    }

    private TerritoryReference parseTerritory(
            PopulationIndicatorsCsvRow row,
            ProcessingCatalog catalog,
            List<UploadErrorDraft> errors
    ) {
        String rawArea = row.getGeographicAreaRaw();

        if (!hasText(rawArea)) {
            errors.add(error(row.getCsvRowNumber(), "Área geográfica", rawArea, "REQUIRED_FIELD_MISSING", "Área geográfica is required"));
            return null;
        }

        String[] tokens = rawArea.trim().split("\\s+", 2);
        String code = tokens[0].trim();

        if (!code.matches("\\d{2}|\\d{5}")) {
            errors.add(error(row.getCsvRowNumber(), "Área geográfica", rawArea, "INVALID_TERRITORY_CODE", "Territory code must have 2 or 5 digits"));
            return null;
        }

        if ("00".equals(code)) {
            return new TerritoryReference(TerritoryLevel.country, null, null, code);
        }

        if (code.length() == 2) {
            Integer stateId = catalog.stateIdsByInegiCode().get(code);

            if (stateId == null) {
                errors.add(error(row.getCsvRowNumber(), "Área geográfica", rawArea, "UNKNOWN_TERRITORY", "State does not exist for INEGI code " + code));
                return null;
            }

            return new TerritoryReference(TerritoryLevel.state, stateId, null, code);
        }

        String stateCode = code.substring(0, 2);
        MunicipalityMetadata municipality = catalog.municipalitiesByInegiCode().get(code);

        if (municipality == null || !stateCode.equals(municipality.stateInegiCode())) {
            errors.add(error(row.getCsvRowNumber(), "Área geográfica", rawArea, "UNKNOWN_TERRITORY", "Municipality does not exist for INEGI code " + code));
            return null;
        }

        return new TerritoryReference(TerritoryLevel.municipality, null, municipality.id(), code);
    }

    private BigDecimal parseRequiredDecimal(
            PopulationIndicatorsCsvRow row,
            String columnName,
            String rawValue,
            List<UploadErrorDraft> errors
    ) {
        if (!hasText(rawValue)) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "MISSING_VALUE", columnName + " is empty"));
            return null;
        }

        return parseDecimal(row, columnName, rawValue, errors);
    }

    private void validateOptionalDecimal(
            PopulationIndicatorsCsvRow row,
            String columnName,
            String rawValue,
            List<UploadErrorDraft> errors
    ) {
        if (hasText(rawValue)) {
            parseDecimal(row, columnName, rawValue, errors);
        }
    }

    private BigDecimal parseDecimal(
            PopulationIndicatorsCsvRow row,
            String columnName,
            String rawValue,
            List<UploadErrorDraft> errors
    ) {
        try {
            return new BigDecimal(rawValue.trim().replace(",", ""));
        } catch (RuntimeException e) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "INVALID_NUMERIC_VALUE", columnName + " must be numeric"));
            return null;
        }
    }

    private TerritoryIndicatorValueWriteDraft valueDraft(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            TerritoryReference territory,
            Integer indicatorId,
            BigDecimal value,
            Short analysisYear,
            Short sourceYear,
            String methodologyNote
    ) {
        return new TerritoryIndicatorValueWriteDraft(
                territory.level().name(),
                territory.stateId(),
                territory.municipalityId(),
                indicatorId,
                value,
                analysisYear,
                sourceYear,
                batch.getDataSource().getId(),
                upload.getOriginalFileName(),
                AvailabilityStatus.available.name(),
                methodologyNote
        );
    }

    private List<DataAvailabilityWriteDraft> buildAvailability(Map<String, IndicatorMetadata> indicators) {
        List<DataAvailabilityWriteDraft> values = new ArrayList<>();

        addPopulationAvailability(values, indicators.get("total_population"));
        addPopulationAvailability(values, indicators.get("percentage_over_60"));
        addCountryStateAvailability(values, indicators.get("healthcare_access_deficiency"));
        addCountryStateAvailability(values, indicators.get("total_poverty_population"));

        return values;
    }

    private void addPopulationAvailability(List<DataAvailabilityWriteDraft> values, IndicatorMetadata indicator) {
        for (Short analysisYear : TARGET_ANALYSIS_YEARS) {
            String note = analysisYear.shortValue() == POPULATION_SOURCE_YEAR ? null : BASE_POPULATION_NOTE;

            values.add(available(indicator, TerritoryLevel.country, analysisYear, POPULATION_SOURCE_YEAR, note));
            values.add(available(indicator, TerritoryLevel.state, analysisYear, POPULATION_SOURCE_YEAR, note));
            values.add(available(indicator, TerritoryLevel.municipality, analysisYear, POPULATION_SOURCE_YEAR, note));
        }
    }

    private void addCountryStateAvailability(List<DataAvailabilityWriteDraft> values, IndicatorMetadata indicator) {
        for (Short analysisYear : TARGET_ANALYSIS_YEARS) {
            values.add(available(indicator, TerritoryLevel.country, analysisYear, analysisYear, null));
            values.add(available(indicator, TerritoryLevel.state, analysisYear, analysisYear, null));
            values.add(new DataAvailabilityWriteDraft(
                    indicator.categoryId(),
                    indicator.id(),
                    TerritoryLevel.municipality.name(),
                    analysisYear,
                    analysisYear,
                    false,
                    AvailabilityStatus.not_available.name(),
                    MUNICIPAL_UNAVAILABLE_NOTE
            ));
        }
    }

    private DataAvailabilityWriteDraft available(
            IndicatorMetadata indicator,
            TerritoryLevel territoryLevel,
            Short analysisYear,
            Short sourceYear,
            String note
    ) {
        return new DataAvailabilityWriteDraft(
                indicator.categoryId(),
                indicator.id(),
                territoryLevel.name(),
                analysisYear,
                sourceYear,
                true,
                AvailabilityStatus.available.name(),
                note
        );
    }

    private ProcessingCatalog loadCatalog(UploadBatchEntity batch) {
        Map<String, IndicatorMetadata> indicators = new LinkedHashMap<>(territoryIndicatorValueWriter.findIndicatorMetadata(INDICATOR_CODES));
        List<String> missingIndicators = INDICATOR_CODES.stream()
                .filter(code -> !indicators.containsKey(code))
                .toList();

        if (!missingIndicators.isEmpty()) {
            throw new NotFoundException("UNKNOWN_INDICATOR: Missing indicators " + missingIndicators);
        }

        if (batch.getDataSource() == null || batch.getDataSource().getId() == null) {
            throw new NotFoundException("UNKNOWN_DATA_SOURCE: Upload batch has no data source");
        }

        return new ProcessingCatalog(
                indicators,
                territoryIndicatorValueWriter.findStateIdsByInegiCode(),
                territoryIndicatorValueWriter.findMunicipalitiesByInegiCode()
        );
    }

    private void flushChunk(Integer uploadId, ChunkBuffer chunk, boolean writeFinalData) {
        if (!chunk.errors().isEmpty()) {
            dataUploadErrorRepository.appendErrors(uploadId, chunk.errors());
        }

        if (writeFinalData && !chunk.values().isEmpty()) {
            territoryIndicatorValueWriter.upsert(chunk.values());
        }
    }

    private void updateUpload(
            Integer uploadId,
            UploadStatus status,
            Integer totalRecords,
            Integer validRecords,
            Integer errorRecords,
            String errorDetail
    ) {
        dataUploadRepository.updateValidationResult(
                uploadId,
                status.name(),
                totalRecords,
                validRecords,
                errorRecords,
                errorDetail
        );
    }

    private UploadErrorDraft error(
            Integer csvRowNumber,
            String columnName,
            String rawValue,
            String errorCode,
            String errorMessage
    ) {
        return new UploadErrorDraft(csvRowNumber, columnName, rawValue, errorCode, errorMessage);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isWholeNumber(BigDecimal value) {
        return value.stripTrailingZeros().scale() <= 0;
    }

    private record ProcessingCatalog(
            Map<String, IndicatorMetadata> indicators,
            Map<String, Integer> stateIdsByInegiCode,
            Map<String, MunicipalityMetadata> municipalitiesByInegiCode
    ) {
        Set<Integer> indicatorIds() {
            Set<Integer> ids = new LinkedHashSet<>();
            indicators.values().forEach(indicator -> ids.add(indicator.id()));
            return ids;
        }
    }

    private record TerritoryReference(
            TerritoryLevel level,
            Integer stateId,
            Integer municipalityId,
            String code
    ) {
    }

    private record RowProcessingResult(
            List<TerritoryIndicatorValueWriteDraft> values,
            List<UploadErrorDraft> errors,
            boolean validRecord
    ) {
    }

    private record ChunkBuffer(
            List<TerritoryIndicatorValueWriteDraft> values,
            List<UploadErrorDraft> errors
    ) {
        ChunkBuffer() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        boolean shouldFlush() {
            return values.size() >= CHUNK_SIZE || errors.size() >= CHUNK_SIZE;
        }

        void clear() {
            values.clear();
            errors.clear();
        }
    }
}
