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
import com.itesm.infrastructure.persistence.repository.PeriodCatalogWriter;
import com.itesm.infrastructure.persistence.repository.TerritoryCatalogWriter;
import com.itesm.infrastructure.persistence.repository.TerritoryCatalogWriter.MunicipalityCatalogResult;
import com.itesm.infrastructure.persistence.repository.TerritoryIndicatorValueWriter;
import com.itesm.infrastructure.persistence.repository.TerritoryIndicatorValueWriter.IndicatorMetadata;
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
import java.util.HashMap;
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
    private static final String PERIOD_DESCRIPTION = "Datos oficiales cargados desde fuente poblacional.";
    private static final List<Short> TARGET_ANALYSIS_YEARS = List.of((short) 2018, (short) 2020, (short) 2022, (short) 2024);
    private static final Set<String> INDICATOR_CODES = Set.of(
            "total_population",
            "percentage_over_60",
            "healthcare_access_deficiency",
            "total_poverty_population"
    );
    private static final Set<CsvFileRole> SUPPORTED_ROLES = EnumSet.of(
            CsvFileRole.population_indicators,
            CsvFileRole.population_municipal_base,
            CsvFileRole.population_state_national_indicators
    );

    private final CsvStorageService csvStorageService;
    private final DataUploadRepository dataUploadRepository;
    private final DataUploadErrorRepository dataUploadErrorRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final PopulationIndicatorsCsvAdapter csvAdapter;
    private final TerritoryIndicatorValueWriter territoryIndicatorValueWriter;
    private final DataAvailabilityWriter dataAvailabilityWriter;
    private final TerritoryCatalogWriter territoryCatalogWriter;
    private final PeriodCatalogWriter periodCatalogWriter;

    public PopulationIndicatorsCsvProcessor(
            CsvStorageService csvStorageService,
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            UploadBatchRepository uploadBatchRepository,
            PopulationIndicatorsCsvAdapter csvAdapter,
            TerritoryIndicatorValueWriter territoryIndicatorValueWriter,
            DataAvailabilityWriter dataAvailabilityWriter,
            TerritoryCatalogWriter territoryCatalogWriter,
            PeriodCatalogWriter periodCatalogWriter
    ) {
        this.csvStorageService = csvStorageService;
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.csvAdapter = csvAdapter;
        this.territoryIndicatorValueWriter = territoryIndicatorValueWriter;
        this.dataAvailabilityWriter = dataAvailabilityWriter;
        this.territoryCatalogWriter = territoryCatalogWriter;
        this.periodCatalogWriter = periodCatalogWriter;
    }

    public PopulationProcessingResult process(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        // Este procesador transforma datos poblacionales en valores materializados
        // por territorio y anio. No escribe tablas operativas de salud.
        List<DataUploadEntity> populationUploads = uploads.stream()
                .filter(upload -> SUPPORTED_ROLES.contains(upload.getFileRole()))
                .toList();

        if (populationUploads.isEmpty()) {
            throw new BadRequestException("INVALID_FILE_ROLE: population processing requires a population fileRole");
        }

        ProcessingCatalog catalog = loadCatalog(batch);
        boolean writeFinalData = mode != UploadProcessingMode.validate_only;

        if (writeFinalData) {
            ensurePeriods();
        }

        if (writeFinalData && (mode == UploadProcessingMode.replace || replaceExistingForYear)) {
            territoryIndicatorValueWriter.deleteExistingPopulationValues(
                    batch.getDataSource().getId(),
                    catalog.indicatorIds(),
                    TARGET_ANALYSIS_YEARS
            );
        }

        PopulationProcessingResult result = new PopulationProcessingResult(0, 0, 0, 0, 0, 0);

        for (DataUploadEntity upload : populationUploads) {
            result = result.add(processUpload(batch, upload, catalog, writeFinalData));
        }

        if (writeFinalData && result.dataRows() > 0) {
            // La disponibilidad se actualiza al final para que refleje el resultado
            // consolidado del lote, no solo el estado de un archivo individual.
            dataAvailabilityWriter.upsert(buildAvailability(catalog.indicators()));
        }

        uploadBatchRepository.recalculateCounters(batch.getId());
        return result;
    }

    private void ensurePeriods() {
        for (Short year : TARGET_ANALYSIS_YEARS) {
            periodCatalogWriter.ensurePeriod(year, PERIOD_DESCRIPTION);
        }
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
        UploadProcessingContext context = new UploadProcessingContext();
        int dataRows = 0;
        int skippedRows = 0;
        int unsupportedPeriodRows = 0;
        int validRecords = 0;
        int errorRecords = 0;
        int valuesUpserted = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_16LE)) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                UploadErrorDraft error = error(1, null, null, "EMPTY_FILE", "El archivo CSV está vacío o no contiene fila de encabezados.");
                dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
                updateUpload(upload.getId(), UploadStatus.error, 0, 0, 1, processingSummary(1));
                return new PopulationProcessingResult(1, 0, 0, 0, 0, 1);
            }

            PopulationIndicatorsCsvAdapter.PopulationIndicatorsColumns columns =
                    csvAdapter.detectColumns(csvAdapter.parseCsvLine(headerLine));
            List<String> missingHeaders = csvAdapter.missingHeaders(columns, upload.getFileRole());

            if (!missingHeaders.isEmpty()) {
                List<UploadErrorDraft> errors = missingHeaders.stream()
                        .map(header -> error(1, header, null, "MISSING_REQUIRED_HEADER", "Falta el encabezado requerido del CSV: " + header))
                        .toList();
                dataUploadErrorRepository.appendErrors(upload.getId(), errors);
                updateUpload(upload.getId(), UploadStatus.error, 0, 0, errors.size(), processingSummary(errors.size()));
                return new PopulationProcessingResult(1, 0, 0, 0, 0, errors.size());
            }

            String line;
            int csvRowNumber = 1;
            while ((line = reader.readLine()) != null) {
                csvRowNumber++;

                if (line.isBlank()) {
                    continue;
                }

                PopulationIndicatorsCsvRow row = csvAdapter.toRow(
                        csvRowNumber,
                        csvAdapter.parseCsvLine(line),
                        columns,
                        upload.getFileRole()
                );

                if (isMetadataRow(row)) {
                    skippedRows++;
                    continue;
                }

                if (isUnsupportedPeriodForRole(row.getPeriodRaw(), upload.getFileRole())) {
                    unsupportedPeriodRows++;
                    continue;
                }

                dataRows++;
                RowProcessingResult rowResult = processRow(batch, upload, row, catalog, context, upload.getFileRole());
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

            int persistedValues = writeFinalData ? valuesUpserted : 0;
            UploadStatus status = statusFor(errorRecords, persistedValues);
            updateUpload(
                    upload.getId(),
                    status,
                    dataRows,
                    validRecords,
                    errorRecords,
                    errorRecords == 0 ? null : processingSummary(errorRecords)
            );

            return new PopulationProcessingResult(1, dataRows, skippedRows, unsupportedPeriodRows, persistedValues, errorRecords);
        } catch (IOException e) {
            UploadErrorDraft error = error(null, null, null, "UPLOAD_STORAGE_ERROR", "No fue posible leer el archivo CSV almacenado.");
            dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
            updateUpload(upload.getId(), UploadStatus.error, dataRows, validRecords, errorRecords + 1, "No fue posible leer el archivo CSV almacenado.");
            return new PopulationProcessingResult(1, dataRows, skippedRows, unsupportedPeriodRows, writeFinalData ? valuesUpserted : 0, errorRecords + 1);
        }
    }
    private RowProcessingResult processRow(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            PopulationIndicatorsCsvRow row,
            ProcessingCatalog catalog,
            UploadProcessingContext context,
            CsvFileRole fileRole
    ) {
        List<UploadErrorDraft> errors = new ArrayList<>();
        List<TerritoryIndicatorValueWriteDraft> values = new ArrayList<>();

        Short period = parsePeriod(row, errors);
        TerritoryReference territory = parseTerritory(row, context, errors, fileRole);

        if (period == null || territory == null) {
            return new RowProcessingResult(values, errors, false);
        }

        if (!isTerritoryLevelSupported(fileRole, territory.level())) {
            return new RowProcessingResult(values, errors, false);
        }

        addPopulationBaseValues(batch, upload, row, territory, period, catalog, values, errors);

        if (processesCountryStateIndicators(fileRole)) {
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
                    "Poblacion en situacion de pobreza",
                    row.getTotalPovertyPopulationRaw(),
                    values,
                    errors
            );
        }

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
                    "Población total debe ser un valor entero."
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
                        indicatorCode + " no está disponible a nivel municipal y fue ignorado."
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
            errors.add(error(row.getCsvRowNumber(), "Periodos", rawPeriod, "REQUIRED_FIELD_MISSING", "Periodos es obligatorio."));
            return null;
        }

        try {
            short period = Short.parseShort(rawPeriod.trim());

            if (!TARGET_ANALYSIS_YEARS.contains(period)) {
                errors.add(error(row.getCsvRowNumber(), "Periodos", rawPeriod, "INVALID_YEAR", "Periodos debe ser 2018, 2020, 2022 o 2024."));
                return null;
            }

            return period;
        } catch (NumberFormatException e) {
            errors.add(error(row.getCsvRowNumber(), "Periodos", rawPeriod, "INVALID_YEAR", "Periodos debe ser numérico."));
            return null;
        }
    }
    private TerritoryReference parseTerritory(
            PopulationIndicatorsCsvRow row,
            UploadProcessingContext context,
            List<UploadErrorDraft> errors,
            CsvFileRole fileRole
    ) {
        String rawArea = row.getGeographicAreaRaw();

        if (!hasText(rawArea)) {
            errors.add(error(row.getCsvRowNumber(), "Area geografica", rawArea, "REQUIRED_FIELD_MISSING", "Área geográfica es obligatoria."));
            return null;
        }

        String[] tokens = rawArea.trim().split("\\s+", 2);
        String code = tokens[0].trim();
        String name = tokens.length > 1 ? tokens[1].trim() : null;

        if (!code.matches("\\d{2}|\\d{5}")) {
            errors.add(error(row.getCsvRowNumber(), "Area geografica", rawArea, "INVALID_TERRITORY_CODE", "La clave territorial debe tener 2 o 5 dígitos."));
            return null;
        }

        if ("00".equals(code)) {
            return new TerritoryReference(TerritoryLevel.country, null, null, code);
        }

        if (code.length() == 2) {
            if (!hasText(name)) {
                errors.add(error(row.getCsvRowNumber(), "Area geografica", rawArea, "REQUIRED_FIELD_MISSING", "El nombre del estado es obligatorio."));
                return null;
            }

            context.stateNamesByCode().put(code, name);
            Integer stateId;

            try {
                stateId = context.stateIdsByCode().computeIfAbsent(
                        code,
                        stateCode -> territoryCatalogWriter.ensureState(stateCode, name)
                );
            } catch (RuntimeException e) {
                errors.add(error(row.getCsvRowNumber(), "Area geografica", rawArea, "TERRITORY_CATALOG_ERROR", safeErrorMessage(e)));
                return null;
            }

            return new TerritoryReference(TerritoryLevel.state, stateId, null, code);
        }

        if (!supportsMunicipality(fileRole)) {
            return new TerritoryReference(TerritoryLevel.municipality, null, null, code);
        }

        String stateCode = code.substring(0, 2);

        if (!hasText(name)) {
            errors.add(error(row.getCsvRowNumber(), "Area geografica", rawArea, "REQUIRED_FIELD_MISSING", "El nombre del municipio es obligatorio."));
            return null;
        }

        MunicipalityCatalogResult municipality;

        try {
            municipality = context.municipalitiesByCode().computeIfAbsent(
                    code,
                    municipalityCode -> territoryCatalogWriter.ensureMunicipality(
                            stateCode,
                            resolveStateName(stateCode, context),
                            municipalityCode,
                            name
                    )
            );
        } catch (RuntimeException e) {
            errors.add(error(row.getCsvRowNumber(), "Area geografica", rawArea, "TERRITORY_CATALOG_ERROR", safeErrorMessage(e)));
            return null;
        }

        return new TerritoryReference(TerritoryLevel.municipality, municipality.stateId(), municipality.municipalityId(), code);
    }

    private BigDecimal parseRequiredDecimal(
            PopulationIndicatorsCsvRow row,
            String columnName,
            String rawValue,
            List<UploadErrorDraft> errors
    ) {
        if (!hasText(rawValue)) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "MISSING_VALUE", columnName + " está vacío."));
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
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "INVALID_NUMERIC_VALUE", columnName + " debe ser numérico."));
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
                    null,
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
            throw new NotFoundException("UNKNOWN_INDICATOR: faltan indicadores " + missingIndicators);
        }

        if (batch.getDataSource() == null || batch.getDataSource().getId() == null) {
            throw new NotFoundException("UNKNOWN_DATA_SOURCE: el lote de carga no tiene fuente de datos");
        }

        return new ProcessingCatalog(
                indicators
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

    private String processingSummary(int errorRecords) {
        return "El procesamiento CSV encontró " + errorRecords + " error(es).";
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

    private String safeErrorMessage(RuntimeException exception) {
        if (exception.getMessage() == null || exception.getMessage().isBlank()) {
            return "No fue posible actualizar el catálogo territorial.";
        }

        return exception.getMessage();
    }

    private String resolveStateName(String stateCode, UploadProcessingContext context) {
        String cachedName = context.stateNamesByCode().get(stateCode);

        if (hasText(cachedName)) {
            return cachedName;
        }

        String storedName = territoryCatalogWriter.findStateNameByCode(stateCode).orElse(null);

        if (hasText(storedName)) {
            context.stateNamesByCode().put(stateCode, storedName);
            return storedName;
        }

        return "Estado " + stateCode;
    }

    private boolean isMetadataRow(PopulationIndicatorsCsvRow row) {
        String period = normalize(row.getPeriodRaw());
        String area = normalize(row.getGeographicAreaRaw());

        return startsAsMetadata(period)
                || startsAsMetadata(area)
                || (!isSupportedDataPeriod(period) && looksLikeMetadataText(period))
                || (!isSupportedDataPeriod(period) && looksLikeMetadataText(area));
    }

    private boolean startsAsMetadata(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalized = normalize(value);

        return normalized.startsWith("notas")
                || normalized.startsWith("nota")
                || normalized.startsWith("fuente")
                || normalized.startsWith("/f")
                || normalized.startsWith("/a")
                || normalized.startsWith("/b")
                || normalized.startsWith("/c")
                || normalized.startsWith("/d")
                || normalized.startsWith("/e");
    }

    private boolean looksLikeMetadataText(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalized = normalize(value);

        return normalized.contains("informacion")
                || normalized.contains("actualiza")
                || normalized.contains("censos")
                || normalized.contains("encuesta")
                || normalized.contains("pobreza")
                || normalized.contains("coneval")
                || normalized.contains("inegi");
    }

    private boolean isSupportedDataPeriod(String rawPeriod) {
        if (rawPeriod == null) {
            return false;
        }

        String period = rawPeriod.trim();
        return "2018".equals(period)
                || "2020".equals(period)
                || "2022".equals(period)
                || "2024".equals(period);
    }

    private boolean isUnsupportedPeriodForRole(String rawPeriod, CsvFileRole fileRole) {
        if (rawPeriod == null || rawPeriod.isBlank()) {
            return false;
        }

        String period = rawPeriod.trim();
        if (!period.matches("\\d{4}")) {
            return false;
        }

        return !isSupportedPeriodForRole(period, fileRole);
    }

    private boolean isSupportedPeriodForRole(String period, CsvFileRole fileRole) {
        if (fileRole == CsvFileRole.population_municipal_base) {
            return String.valueOf(POPULATION_SOURCE_YEAR).equals(period);
        }

        return isSupportedDataPeriod(period);
    }

    private boolean isTerritoryLevelSupported(CsvFileRole fileRole, TerritoryLevel territoryLevel) {
        if (fileRole == CsvFileRole.population_municipal_base) {
            return territoryLevel == TerritoryLevel.municipality;
        }

        if (fileRole == CsvFileRole.population_state_national_indicators) {
            return territoryLevel == TerritoryLevel.country || territoryLevel == TerritoryLevel.state;
        }

        return true;
    }

    private boolean supportsMunicipality(CsvFileRole fileRole) {
        return fileRole == CsvFileRole.population_indicators
                || fileRole == CsvFileRole.population_municipal_base;
    }

    private boolean processesCountryStateIndicators(CsvFileRole fileRole) {
        return fileRole == CsvFileRole.population_indicators
                || fileRole == CsvFileRole.population_state_national_indicators;
    }

    private UploadStatus statusFor(int errorRecords, int valuesUpserted) {
        if (errorRecords == 0) {
            return UploadStatus.completed;
        }

        return valuesUpserted > 0 ? UploadStatus.warning : UploadStatus.error;
    }

    private String normalize(String value) {
        return csvAdapter.normalize(value);
    }

    private record UploadProcessingContext(
            Map<String, String> stateNamesByCode,
            Map<String, Integer> stateIdsByCode,
            Map<String, MunicipalityCatalogResult> municipalitiesByCode
    ) {
        UploadProcessingContext() {
            this(new HashMap<>(), new HashMap<>(), new HashMap<>());
        }
    }

    private record ProcessingCatalog(
            Map<String, IndicatorMetadata> indicators
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


