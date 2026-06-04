package com.itesm.application.service.upload.establishments;

import com.itesm.application.service.upload.CsvStorageService;
import com.itesm.domain.models.catalog.AvailabilityStatus;
import com.itesm.domain.models.catalog.TerritoryLevel;
import com.itesm.domain.models.healthunit.CareLevel;
import com.itesm.domain.models.upload.CatalogWriteResult;
import com.itesm.domain.models.upload.CsvFileRole;
import com.itesm.domain.models.upload.DataAvailabilityWriteDraft;
import com.itesm.domain.models.upload.UploadErrorDraft;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadErrorRepository;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.domain.repository.UploadBatchRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import com.itesm.infrastructure.persistence.repository.DataAvailabilityWriter;
import com.itesm.infrastructure.persistence.repository.EstablishmentTypeCatalogWriter;
import com.itesm.infrastructure.persistence.repository.HealthEstablishmentsIndicatorWriter;
import com.itesm.infrastructure.persistence.repository.HealthUnitWriter;
import com.itesm.infrastructure.persistence.repository.HealthUnitWriter.HealthUnitWriteDraft;
import com.itesm.infrastructure.persistence.repository.InstitutionCatalogWriter;
import com.itesm.infrastructure.persistence.repository.MedicalUnitTypeCatalogWriter;
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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HealthEstablishmentsCsvProcessor {

    private static final int CHUNK_SIZE = 500;
    private static final String INDICATOR_CODE = "health_establishments";
    private static final String METHODOLOGY_NOTE = "Conteo de establecimientos de salud registrados en el catalogo DGIS.";
    private static final String PERIOD_DESCRIPTION = "Datos oficiales cargados desde catalogo de establecimientos DGIS.";

    private final CsvStorageService csvStorageService;
    private final DataUploadRepository dataUploadRepository;
    private final DataUploadErrorRepository dataUploadErrorRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final HealthEstablishmentsCsvAdapter csvAdapter;
    private final TerritoryCatalogWriter territoryCatalogWriter;
    private final InstitutionCatalogWriter institutionCatalogWriter;
    private final EstablishmentTypeCatalogWriter establishmentTypeCatalogWriter;
    private final MedicalUnitTypeCatalogWriter medicalUnitTypeCatalogWriter;
    private final HealthUnitWriter healthUnitWriter;
    private final HealthEstablishmentsIndicatorWriter healthEstablishmentsIndicatorWriter;
    private final TerritoryIndicatorValueWriter territoryIndicatorValueWriter;
    private final DataAvailabilityWriter dataAvailabilityWriter;
    private final PeriodCatalogWriter periodCatalogWriter;

    public HealthEstablishmentsCsvProcessor(
            CsvStorageService csvStorageService,
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            UploadBatchRepository uploadBatchRepository,
            HealthEstablishmentsCsvAdapter csvAdapter,
            TerritoryCatalogWriter territoryCatalogWriter,
            InstitutionCatalogWriter institutionCatalogWriter,
            EstablishmentTypeCatalogWriter establishmentTypeCatalogWriter,
            MedicalUnitTypeCatalogWriter medicalUnitTypeCatalogWriter,
            HealthUnitWriter healthUnitWriter,
            HealthEstablishmentsIndicatorWriter healthEstablishmentsIndicatorWriter,
            TerritoryIndicatorValueWriter territoryIndicatorValueWriter,
            DataAvailabilityWriter dataAvailabilityWriter,
            PeriodCatalogWriter periodCatalogWriter
    ) {
        this.csvStorageService = csvStorageService;
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.csvAdapter = csvAdapter;
        this.territoryCatalogWriter = territoryCatalogWriter;
        this.institutionCatalogWriter = institutionCatalogWriter;
        this.establishmentTypeCatalogWriter = establishmentTypeCatalogWriter;
        this.medicalUnitTypeCatalogWriter = medicalUnitTypeCatalogWriter;
        this.healthUnitWriter = healthUnitWriter;
        this.healthEstablishmentsIndicatorWriter = healthEstablishmentsIndicatorWriter;
        this.territoryIndicatorValueWriter = territoryIndicatorValueWriter;
        this.dataAvailabilityWriter = dataAvailabilityWriter;
        this.periodCatalogWriter = periodCatalogWriter;
    }

    public HealthEstablishmentProcessingResult process(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        // Carga el catalogo operativo de unidades medicas y despues recalcula el
        // indicador agregado health_establishments para dashboard/mapa/comparacion.
        List<DataUploadEntity> establishmentUploads = uploads.stream()
                .filter(upload -> upload.getFileRole() == CsvFileRole.establishments_catalog)
                .toList();

        if (establishmentUploads.isEmpty()) {
            throw new BadRequestException("INVALID_FILE_ROLE: el procesamiento de establecimientos requiere fileRole=establishments_catalog");
        }

        boolean writeFinalData = mode != UploadProcessingMode.validate_only;
        Short sourceYear = batch.getSourceYear();

        if (sourceYear == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: sourceYear es obligatorio");
        }

        if (writeFinalData) {
            periodCatalogWriter.ensurePeriod(sourceYear, PERIOD_DESCRIPTION);
        }

        if (writeFinalData && (mode == UploadProcessingMode.replace || replaceExistingForYear)) {
            healthUnitWriter.markInactiveBySourceYear(sourceYear);
        }

        HealthEstablishmentProcessingResult result = new HealthEstablishmentProcessingResult(0, 0, 0, 0, 0, 0, 0, 0, 0);

        for (DataUploadEntity upload : establishmentUploads) {
            result = result.add(processUpload(batch, upload, writeFinalData));
        }

        if (writeFinalData && result.healthUnitsUpserted() > 0) {
            ProcessingCatalog catalog = loadCatalog(batch);
            // El indicador se recalcula desde health_units para dejar un valor
            // territorial unico en territory_indicator_values.
            int indicatorRows = healthEstablishmentsIndicatorWriter.recalculate(
                    sourceYear,
                    catalog.indicator().id(),
                    batch.getDataSource().getId(),
                    sourceFileForIndicators(batch, establishmentUploads)
            );

            dataAvailabilityWriter.upsert(buildAvailability(catalog.indicator(), sourceYear));
            result = result.add(new HealthEstablishmentProcessingResult(0, 0, 0, 0, 0, indicatorRows, 0, 0, 0));
        }

        uploadBatchRepository.recalculateCounters(batch.getId());
        return result;
    }

    private HealthEstablishmentProcessingResult processUpload(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            boolean writeFinalData
    ) {
        dataUploadErrorRepository.deleteByUploadId(upload.getId());

        Path path = csvStorageService.resolveStoredPath(upload.getStoredFileName());
        ProcessingContext context = new ProcessingContext();
        ChunkBuffer chunk = new ChunkBuffer();
        int dataRows = 0;
        int skippedRows = 0;
        int validRecords = 0;
        int errorRecords = 0;
        int warningRecords = 0;
        int coordinateWarnings = 0;
        int healthUnitsUpserted = 0;
        int catalogValuesChanged = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                UploadErrorDraft error = error(1, null, null, "EMPTY_FILE", "El archivo CSV está vacío o no contiene fila de encabezados.");
                dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
                updateUpload(upload.getId(), UploadStatus.error, 0, 0, 1, processingSummary(1, 0));
                return new HealthEstablishmentProcessingResult(1, 0, 0, 0, 0, 0, 1, 0, 0);
            }

            HealthEstablishmentsCsvAdapter.HealthEstablishmentsColumns columns =
                    csvAdapter.detectColumns(csvAdapter.parseCsvLine(headerLine));
            List<String> missingHeaders = csvAdapter.missingHeaders(columns);

            if (!missingHeaders.isEmpty()) {
                List<UploadErrorDraft> errors = missingHeaders.stream()
                        .map(header -> error(1, header, null, "MISSING_REQUIRED_HEADER", "Falta el encabezado requerido del CSV: " + header))
                        .toList();
                dataUploadErrorRepository.appendErrors(upload.getId(), errors);
                updateUpload(upload.getId(), UploadStatus.error, 0, 0, errors.size(), processingSummary(errors.size(), 0));
                return new HealthEstablishmentProcessingResult(1, 0, 0, 0, 0, 0, errors.size(), 0, 0);
            }

            String line;
            int csvRowNumber = 1;
            while ((line = readCsvRecord(reader)) != null) {
                csvRowNumber++;

                if (line.isBlank()) {
                    skippedRows++;
                    continue;
                }

                List<String> values = csvAdapter.parseCsvLine(line);
                if (csvAdapter.isBlankRow(values)) {
                    skippedRows++;
                    continue;
                }

                HealthEstablishmentCsvRow row = csvAdapter.toRow(csvRowNumber, values, columns);
                if (isSkippableRow(row)) {
                    skippedRows++;
                    continue;
                }

                dataRows++;
                RowProcessingResult rowResult = processRow(batch, row, context, writeFinalData);
                chunk.errors().addAll(rowResult.errors());
                rowResult.healthUnitOptional().ifPresent(chunk.healthUnits()::add);
                errorRecords += countBlockingIssues(rowResult.errors());
                warningRecords += countWarningIssues(rowResult.errors());
                coordinateWarnings += countCoordinateWarnings(rowResult.errors());
                catalogValuesChanged += rowResult.catalogValuesChanged();

                if (rowResult.validRecord()) {
                    validRecords++;
                }

                if (chunk.shouldFlush()) {
                    healthUnitsUpserted += flushChunk(upload.getId(), chunk, writeFinalData);
                    chunk.clear();
                }
            }

            healthUnitsUpserted += flushChunk(upload.getId(), chunk, writeFinalData);

            int persistedUnits = writeFinalData ? healthUnitsUpserted : 0;
            int countedIssueRecords = errorRecords + warningRecords;
            UploadStatus status = statusFor(errorRecords, warningRecords, persistedUnits);
            updateUpload(
                    upload.getId(),
                    status,
                    dataRows,
                    validRecords,
                    countedIssueRecords,
                    countedIssueRecords == 0 ? null : processingSummary(errorRecords, warningRecords)
            );

            return new HealthEstablishmentProcessingResult(
                    1,
                    dataRows,
                    skippedRows,
                    persistedUnits,
                    writeFinalData ? catalogValuesChanged : 0,
                    0,
                    errorRecords,
                    warningRecords,
                    coordinateWarnings
            );
        } catch (IOException e) {
            UploadErrorDraft error = error(null, null, null, "UPLOAD_STORAGE_ERROR", "No fue posible leer el archivo CSV almacenado.");
            dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
            updateUpload(upload.getId(), UploadStatus.error, dataRows, validRecords, errorRecords + warningRecords + 1, "No fue posible leer el archivo CSV almacenado.");

            return new HealthEstablishmentProcessingResult(
                    1,
                    dataRows,
                    skippedRows,
                    writeFinalData ? healthUnitsUpserted : 0,
                    writeFinalData ? catalogValuesChanged : 0,
                    0,
                    errorRecords + 1,
                    warningRecords,
                    coordinateWarnings
            );
        }
    }

    private RowProcessingResult processRow(
            UploadBatchEntity batch,
            HealthEstablishmentCsvRow row,
            ProcessingContext context,
            boolean writeFinalData
    ) {
        List<UploadErrorDraft> errors = new ArrayList<>();
        String clues = required(row, "CLUES", row.getCluesRaw(), errors);
        String institutionName = required(row, "NOMBRE DE LA INSTITUCION", row.getInstitutionNameRaw(), errors);
        String stateName = required(row, "ENTIDAD", row.getStateNameRaw(), errors);
        String municipalityName = required(row, "MUNICIPIO", row.getMunicipalityNameRaw(), errors);
        String establishmentTypeName = required(row, "NOMBRE TIPO ESTABLECIMIENTO", row.getEstablishmentTypeRaw(), errors);
        String medicalUnitTypeName = required(row, "NOMBRE DE TIPOLOGIA", row.getMedicalUnitTypeRaw(), errors);
        String unitName = required(row, "NOMBRE DE LA UNIDAD", row.getUnitNameRaw(), errors);
        String operationStatus = optionalText(row.getOperationStatusRaw());
        String stateCode = normalizeStateCode(row, errors);
        String municipalityCode = normalizeMunicipalityCode(row, stateCode, errors);
        CareLevel careLevel = normalizeCareLevel(row, errors);
        BigDecimal latitude = parseCoordinate(row, "LATITUD", row.getLatitudeRaw(), BigDecimal.valueOf(-90), BigDecimal.valueOf(90), errors);
        BigDecimal longitude = parseCoordinate(row, "LONGITUD", row.getLongitudeRaw(), BigDecimal.valueOf(-180), BigDecimal.valueOf(180), errors);
        if (hasCoordinateWarning(errors)) {
            latitude = null;
            longitude = null;
        }

        if (clues != null && !context.seenClues().add(clues)) {
            errors.add(error(
                    row.getCsvRowNumber(),
                    "CLUES",
                    clues,
                    "DUPLICATED_CLUES_IN_FILE",
                    "CLUES está duplicado en este archivo y la fila fue omitida."
            ));
            return new RowProcessingResult(null, errors, false, 0);
        }

        if (hasBlockingErrors(errors) || !writeFinalData) {
            return new RowProcessingResult(null, errors, !hasBlockingErrors(errors), 0);
        }

        int catalogChanges = 0;
        MunicipalityCatalogResult municipality = context.municipalitiesByCode().get(municipalityCode);
        if (municipality == null) {
            municipality = territoryCatalogWriter.ensureMunicipality(stateCode, stateName, municipalityCode, municipalityName);
            context.municipalitiesByCode().put(municipalityCode, municipality);
        }

        CatalogWriteResult institution = ensureCatalog(context.institutionsByName(), institutionName, institutionCatalogWriter::ensure);
        CatalogWriteResult establishmentType = ensureCatalog(context.establishmentTypesByName(), establishmentTypeName, establishmentTypeCatalogWriter::ensure);
        CatalogWriteResult medicalUnitType = ensureCatalog(context.medicalUnitTypesByName(), medicalUnitTypeName, medicalUnitTypeCatalogWriter::ensure);
        catalogChanges += institution.changed() ? 1 : 0;
        catalogChanges += establishmentType.changed() ? 1 : 0;
        catalogChanges += medicalUnitType.changed() ? 1 : 0;

        HealthUnitWriteDraft unit = new HealthUnitWriteDraft(
                clues,
                unitName,
                municipality.municipalityId(),
                institution.id(),
                establishmentType.id(),
                medicalUnitType.id(),
                careLevel.name(),
                batch.getSourceYear(),
                operationStatus,
                optionalText(row.getLocalityNameRaw()),
                latitude,
                longitude,
                true
        );

        return new RowProcessingResult(unit, errors, true, catalogChanges);
    }

    private CatalogWriteResult ensureCatalog(
            Map<String, CatalogWriteResult> cache,
            String name,
            CatalogEnsurer ensurer
    ) {
        CatalogWriteResult cached = cache.get(name);
        if (cached != null) {
            return new CatalogWriteResult(cached.id(), false);
        }

        CatalogWriteResult result = ensurer.ensure(name);
        cache.put(name, result);
        return result;
    }

    private int flushChunk(Integer uploadId, ChunkBuffer chunk, boolean writeFinalData) {
        if (!chunk.errors().isEmpty()) {
            dataUploadErrorRepository.appendErrors(uploadId, chunk.errors());
        }

        if (writeFinalData && !chunk.healthUnits().isEmpty()) {
            return healthUnitWriter.upsert(chunk.healthUnits());
        }

        return 0;
    }

    private String required(HealthEstablishmentCsvRow row, String columnName, String rawValue, List<UploadErrorDraft> errors) {
        String value = optionalText(rawValue);

        if (value == null) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "REQUIRED_FIELD_MISSING", columnName + " es obligatorio."));
        }

        return value;
    }

    private String optionalText(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        return rawValue.trim().replaceAll("\\s+", " ");
    }

    private String normalizeStateCode(HealthEstablishmentCsvRow row, List<UploadErrorDraft> errors) {
        String rawValue = row.getStateCodeRaw();

        if (rawValue == null || rawValue.isBlank()) {
            errors.add(error(row.getCsvRowNumber(), "CLAVE DE LA ENTIDAD", rawValue, "REQUIRED_FIELD_MISSING", "La clave de estado es obligatoria."));
            return null;
        }

        String value = rawValue.trim();
        if (!value.matches("\\d{1,2}")) {
            errors.add(error(row.getCsvRowNumber(), "CLAVE DE LA ENTIDAD", rawValue, "INVALID_STATE_CODE", "La clave de estado debe tener 1 o 2 dígitos."));
            return null;
        }

        String normalized = value.length() == 1 ? "0" + value : value;
        if ("00".equals(normalized)) {
            errors.add(error(row.getCsvRowNumber(), "CLAVE DE LA ENTIDAD", rawValue, "INVALID_STATE_CODE", "La clave de estado 00 no es válida para unidades de salud."));
            return null;
        }

        return normalized;
    }

    private String normalizeMunicipalityCode(HealthEstablishmentCsvRow row, String stateCode, List<UploadErrorDraft> errors) {
        String rawValue = row.getMunicipalityCodeRaw();

        if (rawValue == null || rawValue.isBlank()) {
            errors.add(error(row.getCsvRowNumber(), "CLAVE DEL MUNICIPIO", rawValue, "REQUIRED_FIELD_MISSING", "La clave de municipio es obligatoria."));
            return null;
        }

        if (stateCode == null) {
            return null;
        }

        String value = rawValue.trim();
        if (!value.matches("\\d{1,5}")) {
            errors.add(error(row.getCsvRowNumber(), "CLAVE DEL MUNICIPIO", rawValue, "INVALID_MUNICIPALITY_CODE", "La clave de municipio debe ser numérica."));
            return null;
        }

        String localMunicipalityCode = value;
        if (value.length() == 5 && value.startsWith(stateCode)) {
            localMunicipalityCode = value.substring(2);
        }

        if (localMunicipalityCode.length() > 3) {
            errors.add(error(row.getCsvRowNumber(), "CLAVE DEL MUNICIPIO", rawValue, "INVALID_MUNICIPALITY_CODE", "La clave de municipio debe tener 3 dígitos o ser una clave INEGI de 5 dígitos correspondiente al estado."));
            return null;
        }

        return stateCode + leftPad(localMunicipalityCode, 3);
    }

    private CareLevel normalizeCareLevel(HealthEstablishmentCsvRow row, List<UploadErrorDraft> errors) {
        String rawValue = row.getCareLevelRaw();
        String normalized = normalize(rawValue);

        if (normalized.isBlank()
                || normalized.contains("no aplica")
                || normalized.contains("no especificado")) {
            return CareLevel.not_specified;
        }

        if (normalized.contains("primer")) {
            return CareLevel.primary;
        }

        if (normalized.contains("segundo")) {
            return CareLevel.secondary;
        }

        if (normalized.contains("tercer")) {
            return CareLevel.tertiary;
        }

        errors.add(error(
                row.getCsvRowNumber(),
                "NIVEL ATENCION",
                rawValue,
                "INVALID_CARE_LEVEL",
                "El nivel de atención no se reconoció y se guardó como no especificado."
        ));
        return CareLevel.not_specified;
    }

    private BigDecimal parseCoordinate(
            HealthEstablishmentCsvRow row,
            String columnName,
            String rawValue,
            BigDecimal min,
            BigDecimal max,
            List<UploadErrorDraft> errors
    ) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        try {
            BigDecimal value = new BigDecimal(rawValue.trim());
            if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
                errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "INVALID_COORDINATE", columnName + " está fuera del rango válido."));
                return null;
            }

            return value;
        } catch (RuntimeException e) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "INVALID_COORDINATE", columnName + " debe ser numérico."));
            return null;
        }
    }

    private boolean hasBlockingErrors(List<UploadErrorDraft> errors) {
        return errors.stream().anyMatch(this::isBlockingIssue);
    }

    private int countBlockingIssues(List<UploadErrorDraft> errors) {
        return (int) errors.stream()
                .filter(this::isBlockingIssue)
                .count();
    }

    private int countWarningIssues(List<UploadErrorDraft> errors) {
        return (int) errors.stream()
                .filter(error -> !isBlockingIssue(error))
                .filter(error -> !isCoordinateWarning(error))
                .count();
    }

    private int countCoordinateWarnings(List<UploadErrorDraft> errors) {
        return (int) errors.stream()
                .filter(this::isCoordinateWarning)
                .count();
    }

    private boolean isBlockingIssue(UploadErrorDraft error) {
        return "REQUIRED_FIELD_MISSING".equals(error.getErrorCode())
                || "INVALID_STATE_CODE".equals(error.getErrorCode())
                || "INVALID_MUNICIPALITY_CODE".equals(error.getErrorCode())
                || "DUPLICATED_CLUES_IN_FILE".equals(error.getErrorCode())
                || "INVALID_ROW_FORMAT".equals(error.getErrorCode());
    }

    private boolean hasCoordinateWarning(List<UploadErrorDraft> errors) {
        return errors.stream().anyMatch(this::isCoordinateWarning);
    }

    private boolean isCoordinateWarning(UploadErrorDraft error) {
        return "INVALID_COORDINATE".equals(error.getErrorCode());
    }

    private ProcessingCatalog loadCatalog(UploadBatchEntity batch) {
        Map<String, IndicatorMetadata> indicators = territoryIndicatorValueWriter.findIndicatorMetadata(Set.of(INDICATOR_CODE));
        IndicatorMetadata indicator = indicators.get(INDICATOR_CODE);

        if (indicator == null) {
            throw new NotFoundException("UNKNOWN_INDICATOR: falta el indicador requerido: " + INDICATOR_CODE);
        }

        if (batch.getDataSource() == null || batch.getDataSource().getId() == null) {
            throw new NotFoundException("UNKNOWN_DATA_SOURCE: el lote de carga no tiene fuente de datos");
        }

        return new ProcessingCatalog(indicator);
    }

    private List<DataAvailabilityWriteDraft> buildAvailability(IndicatorMetadata indicator, Short sourceYear) {
        return List.of(
                available(indicator, TerritoryLevel.country, sourceYear),
                available(indicator, TerritoryLevel.state, sourceYear),
                available(indicator, TerritoryLevel.municipality, sourceYear)
        );
    }

    private DataAvailabilityWriteDraft available(
            IndicatorMetadata indicator,
            TerritoryLevel territoryLevel,
            Short sourceYear
    ) {
        return new DataAvailabilityWriteDraft(
                indicator.categoryId(),
                indicator.id(),
                territoryLevel.name(),
                sourceYear,
                sourceYear,
                true,
                AvailabilityStatus.available.name(),
                METHODOLOGY_NOTE
        );
    }

    private String sourceFileForIndicators(UploadBatchEntity batch, List<DataUploadEntity> uploads) {
        if (uploads.size() == 1) {
            return uploads.get(0).getOriginalFileName();
        }

        return "batch:" + batch.getBatchVersion();
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

    private String readCsvRecord(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        if (firstLine == null) {
            return null;
        }

        StringBuilder record = new StringBuilder(firstLine);
        while (hasOpenQuotes(record)) {
            String continuation = reader.readLine();
            if (continuation == null) {
                break;
            }

            record.append('\n').append(continuation);
        }

        return record.toString();
    }

    private boolean hasOpenQuotes(CharSequence value) {
        boolean inQuotes = false;

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '"') {
                continue;
            }

            if (inQuotes && i + 1 < value.length() && value.charAt(i + 1) == '"') {
                i++;
                continue;
            }

            inQuotes = !inQuotes;
        }

        return inQuotes;
    }

    private boolean isSkippableRow(HealthEstablishmentCsvRow row) {
        if (row.isBlank()) {
            return true;
        }

        return isBlank(row.getCluesRaw())
                && !hasAnyText(
                row.getInstitutionNameRaw(),
                row.getStateCodeRaw(),
                row.getStateNameRaw(),
                row.getMunicipalityCodeRaw(),
                row.getMunicipalityNameRaw(),
                row.getEstablishmentTypeRaw(),
                row.getMedicalUnitTypeRaw(),
                row.getUnitNameRaw(),
                row.getOperationStatusRaw(),
                row.getCareLevelRaw()
        );
    }

    private boolean hasAnyText(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return true;
            }
        }

        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String processingSummary(int errorRecords, int warningRecords) {
        if (errorRecords == 0) {
            return "El procesamiento CSV encontró " + warningRecords + " advertencia(s).";
        }

        if (warningRecords == 0) {
            return "El procesamiento CSV encontró " + errorRecords + " error(es).";
        }

        return "El procesamiento CSV encontró " + errorRecords + " error(es) y " + warningRecords + " advertencia(s).";
    }

    private UploadStatus statusFor(int errorRecords, int warningRecords, int healthUnitsUpserted) {
        if (errorRecords == 0 && warningRecords == 0) {
            return UploadStatus.completed;
        }

        if (errorRecords == 0) {
            return UploadStatus.warning;
        }

        return healthUnitsUpserted > 0 ? UploadStatus.warning : UploadStatus.error;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        String withoutDiacritics = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return withoutDiacritics
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String leftPad(String value, int targetLength) {
        if (value.length() >= targetLength) {
            return value;
        }

        return "0".repeat(targetLength - value.length()) + value;
    }

    private interface CatalogEnsurer {
        CatalogWriteResult ensure(String name);
    }

    private record ProcessingContext(
            Set<String> seenClues,
            Map<String, MunicipalityCatalogResult> municipalitiesByCode,
            Map<String, CatalogWriteResult> institutionsByName,
            Map<String, CatalogWriteResult> establishmentTypesByName,
            Map<String, CatalogWriteResult> medicalUnitTypesByName
    ) {
        ProcessingContext() {
            this(new HashSet<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        }
    }

    private record ProcessingCatalog(
            IndicatorMetadata indicator
    ) {
    }

    private record RowProcessingResult(
            HealthUnitWriteDraft healthUnit,
            List<UploadErrorDraft> errors,
            boolean validRecord,
            int catalogValuesChanged
    ) {
        java.util.Optional<HealthUnitWriteDraft> healthUnitOptional() {
            return java.util.Optional.ofNullable(healthUnit);
        }
    }

    private record ChunkBuffer(
            List<HealthUnitWriteDraft> healthUnits,
            List<UploadErrorDraft> errors
    ) {
        ChunkBuffer() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        boolean shouldFlush() {
            return healthUnits.size() >= CHUNK_SIZE || errors.size() >= CHUNK_SIZE;
        }

        void clear() {
            healthUnits.clear();
            errors.clear();
        }
    }
}
