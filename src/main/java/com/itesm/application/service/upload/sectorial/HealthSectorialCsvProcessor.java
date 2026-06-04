package com.itesm.application.service.upload.sectorial;

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
import com.itesm.infrastructure.persistence.repository.HealthSectorialIndicatorWriter;
import com.itesm.infrastructure.persistence.repository.HealthSectorialIndicatorWriter.SectorialIndicatorWriteResult;
import com.itesm.infrastructure.persistence.repository.HealthUnitInfrastructureDetailWriter;
import com.itesm.infrastructure.persistence.repository.HealthUnitInfrastructureDetailWriter.HealthUnitInfrastructureDetailDraft;
import com.itesm.infrastructure.persistence.repository.HealthUnitInfrastructureWriter;
import com.itesm.infrastructure.persistence.repository.HealthUnitInfrastructureWriter.HealthUnitInfrastructureDraft;
import com.itesm.infrastructure.persistence.repository.HealthUnitStaffSpecialtyWriter;
import com.itesm.infrastructure.persistence.repository.HealthUnitStaffSpecialtyWriter.HealthUnitStaffSpecialtyDraft;
import com.itesm.infrastructure.persistence.repository.HealthUnitStaffWriter;
import com.itesm.infrastructure.persistence.repository.HealthUnitStaffWriter.HealthUnitStaffDraft;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class HealthSectorialCsvProcessor {

    private static final int CHUNK_SIZE = 500;
    private static final Charset SOURCE_CHARSET = Charset.forName("windows-1252");
    private static final Set<String> REQUIRED_INDICATORS = Set.of(
            "total_doctors",
            "total_nurses",
            "hospital_beds",
            "consulting_rooms",
            "doctors_per_1000",
            "beds_per_1000",
            "total_population"
    );
    private static final Set<String> REQUIRED_INFRASTRUCTURE_TYPES = Set.of(
            "total_consultorios",
            "total_camas_hospitalizacion"
    );
    private static final String PERIOD_DESCRIPTION = "Datos oficiales cargados desde fuente sectorial DGIS.";
    private static final String RATE_NOT_AVAILABLE_NOTE = "No se pudo calcular la tasa porque falta poblacion para el territorio/anio.";

    private final CsvStorageService csvStorageService;
    private final DataUploadRepository dataUploadRepository;
    private final DataUploadErrorRepository dataUploadErrorRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final HealthSectorialCsvAdapter csvAdapter;
    private final PeriodCatalogWriter periodCatalogWriter;
    private final TerritoryCatalogWriter territoryCatalogWriter;
    private final InstitutionCatalogWriter institutionCatalogWriter;
    private final EstablishmentTypeCatalogWriter establishmentTypeCatalogWriter;
    private final MedicalUnitTypeCatalogWriter medicalUnitTypeCatalogWriter;
    private final HealthUnitWriter healthUnitWriter;
    private final HealthUnitStaffWriter healthUnitStaffWriter;
    private final HealthUnitStaffSpecialtyWriter healthUnitStaffSpecialtyWriter;
    private final HealthUnitInfrastructureWriter healthUnitInfrastructureWriter;
    private final HealthUnitInfrastructureDetailWriter healthUnitInfrastructureDetailWriter;
    private final HealthSectorialIndicatorWriter healthSectorialIndicatorWriter;
    private final TerritoryIndicatorValueWriter territoryIndicatorValueWriter;
    private final DataAvailabilityWriter dataAvailabilityWriter;

    public HealthSectorialCsvProcessor(
            CsvStorageService csvStorageService,
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            UploadBatchRepository uploadBatchRepository,
            HealthSectorialCsvAdapter csvAdapter,
            PeriodCatalogWriter periodCatalogWriter,
            TerritoryCatalogWriter territoryCatalogWriter,
            InstitutionCatalogWriter institutionCatalogWriter,
            EstablishmentTypeCatalogWriter establishmentTypeCatalogWriter,
            MedicalUnitTypeCatalogWriter medicalUnitTypeCatalogWriter,
            HealthUnitWriter healthUnitWriter,
            HealthUnitStaffWriter healthUnitStaffWriter,
            HealthUnitStaffSpecialtyWriter healthUnitStaffSpecialtyWriter,
            HealthUnitInfrastructureWriter healthUnitInfrastructureWriter,
            HealthUnitInfrastructureDetailWriter healthUnitInfrastructureDetailWriter,
            HealthSectorialIndicatorWriter healthSectorialIndicatorWriter,
            TerritoryIndicatorValueWriter territoryIndicatorValueWriter,
            DataAvailabilityWriter dataAvailabilityWriter
    ) {
        this.csvStorageService = csvStorageService;
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.csvAdapter = csvAdapter;
        this.periodCatalogWriter = periodCatalogWriter;
        this.territoryCatalogWriter = territoryCatalogWriter;
        this.institutionCatalogWriter = institutionCatalogWriter;
        this.establishmentTypeCatalogWriter = establishmentTypeCatalogWriter;
        this.medicalUnitTypeCatalogWriter = medicalUnitTypeCatalogWriter;
        this.healthUnitWriter = healthUnitWriter;
        this.healthUnitStaffWriter = healthUnitStaffWriter;
        this.healthUnitStaffSpecialtyWriter = healthUnitStaffSpecialtyWriter;
        this.healthUnitInfrastructureWriter = healthUnitInfrastructureWriter;
        this.healthUnitInfrastructureDetailWriter = healthUnitInfrastructureDetailWriter;
        this.healthSectorialIndicatorWriter = healthSectorialIndicatorWriter;
        this.territoryIndicatorValueWriter = territoryIndicatorValueWriter;
        this.dataAvailabilityWriter = dataAvailabilityWriter;
    }

    public HealthSectorialProcessingResult process(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        List<DataUploadEntity> sectorialUploads = uploads.stream()
                .filter(upload -> upload.getFileRole() == CsvFileRole.sectorial_data)
                .toList();

        if (sectorialUploads.isEmpty()) {
            throw new BadRequestException("INVALID_FILE_ROLE: health sectorial processing requires fileRole=sectorial_data");
        }

        Short sourceYear = batch.getSourceYear();
        if (sourceYear == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: sourceYear is required");
        }

        boolean writeFinalData = mode != UploadProcessingMode.validate_only;
        Integer periodId = writeFinalData ? periodCatalogWriter.ensurePeriod(sourceYear, PERIOD_DESCRIPTION) : null;
        ProcessingCatalog catalog = loadCatalog();
        ProcessingContext context = new ProcessingContext();

        if (writeFinalData && (mode == UploadProcessingMode.replace || replaceExistingForYear)) {
            healthUnitStaffWriter.deleteByPeriodAndDataSource(periodId, batch.getDataSource().getId());
            healthUnitInfrastructureWriter.deleteByPeriodAndDataSource(periodId, batch.getDataSource().getId());
            healthSectorialIndicatorWriter.deleteExistingValues(sourceYear, batch.getDataSource().getId(), catalog.sectorialIndicatorIds());
            dataAvailabilityWriter.deleteByIndicatorIdsAndAnalysisYear(catalog.sectorialIndicatorIds(), sourceYear);
        }

        HealthSectorialProcessingResult result = new HealthSectorialProcessingResult(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        for (DataUploadEntity upload : sectorialUploads) {
            result = result.add(processUpload(batch, upload, writeFinalData, periodId, catalog, context));
        }

        if (writeFinalData && result.staffRowsUpserted() > 0) {
            SectorialIndicatorWriteResult indicatorResult = healthSectorialIndicatorWriter.recalculate(
                    sourceYear,
                    periodId,
                    batch.getDataSource().getId(),
                    sourceFileForIndicators(batch, sectorialUploads),
                    catalog.indicatorIdsByCode(),
                    catalog.infrastructureTypeIdsByCode()
            );
            dataAvailabilityWriter.upsert(buildAvailability(catalog.indicatorsByCode(), sourceYear, indicatorResult.availableLevelsByIndicatorCode()));
            result = result.add(new HealthSectorialProcessingResult(0, 0, 0, 0, 0, 0, 0, 0, 0, indicatorResult.rowsUpserted(), 0));
        }

        uploadBatchRepository.recalculateCounters(batch.getId());
        return result;
    }

    private HealthSectorialProcessingResult processUpload(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            boolean writeFinalData,
            Integer periodId,
            ProcessingCatalog catalog,
            ProcessingContext context
    ) {
        dataUploadErrorRepository.deleteByUploadId(upload.getId());

        Path path = csvStorageService.resolveStoredPath(upload.getStoredFileName());
        ChunkBuffer chunk = new ChunkBuffer();
        Set<String> seenClues = new HashSet<>();
        int dataRows = 0;
        int skippedRows = 0;
        int validRecords = 0;
        int errorRecords = 0;
        int staffRowsUpserted = 0;
        int specialtyRowsUpserted = 0;
        int infrastructureRowsUpserted = 0;
        int infrastructureDetailRowsUpserted = 0;
        int minimalHealthUnitsCreated = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, SOURCE_CHARSET)) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                UploadErrorDraft error = error(1, null, null, "EMPTY_FILE", "CSV file is empty or has no header row");
                dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
                updateUpload(upload.getId(), UploadStatus.error, 0, 0, 1, "CSV processing found 1 error(s)");
                return new HealthSectorialProcessingResult(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
            }

            HealthSectorialCsvAdapter.HealthSectorialColumns columns = csvAdapter.detectColumns(csvAdapter.parseCsvLine(headerLine));
            List<String> missingHeaders = csvAdapter.missingHeaders(columns);
            if (!missingHeaders.isEmpty()) {
                List<UploadErrorDraft> errors = missingHeaders.stream()
                        .map(header -> error(1, header, null, "MISSING_REQUIRED_HEADER", "Required CSV header is missing: " + header))
                        .toList();
                dataUploadErrorRepository.appendErrors(upload.getId(), errors);
                updateUpload(upload.getId(), UploadStatus.error, 0, 0, errors.size(), "CSV processing found " + errors.size() + " error(s)");
                return new HealthSectorialProcessingResult(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, errors.size());
            }

            validatePresentSpecialtySeeds(columns, catalog.specialtyIdsByCode());

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

                HealthSectorialCsvRow row = csvAdapter.toRow(csvRowNumber, values, columns);
                if (row.isBlank()) {
                    skippedRows++;
                    continue;
                }

                dataRows++;
                RowProcessingResult rowResult = processRow(batch, upload, row, writeFinalData, periodId, catalog, context, seenClues);
                chunk.errors().addAll(rowResult.errors());
                rowResult.staffDraftOptional().ifPresent(chunk.staff()::add);
                rowResult.infrastructureDraftOptional().ifPresent(chunk.infrastructure()::add);
                chunk.specialties().addAll(rowResult.specialties());
                chunk.infrastructureDetails().addAll(rowResult.infrastructureDetails());
                errorRecords += rowResult.errors().size();
                minimalHealthUnitsCreated += rowResult.minimalHealthUnitCreated() ? 1 : 0;

                if (rowResult.validRecord()) {
                    validRecords++;
                }

                if (chunk.shouldFlush()) {
                    FlushResult flushResult = flushChunk(upload.getId(), periodId, chunk, writeFinalData, catalog);
                    staffRowsUpserted += flushResult.staffRowsUpserted();
                    specialtyRowsUpserted += flushResult.specialtyRowsUpserted();
                    infrastructureRowsUpserted += flushResult.infrastructureRowsUpserted();
                    infrastructureDetailRowsUpserted += flushResult.infrastructureDetailRowsUpserted();
                    chunk.clear();
                }
            }

            FlushResult flushResult = flushChunk(upload.getId(), periodId, chunk, writeFinalData, catalog);
            staffRowsUpserted += flushResult.staffRowsUpserted();
            specialtyRowsUpserted += flushResult.specialtyRowsUpserted();
            infrastructureRowsUpserted += flushResult.infrastructureRowsUpserted();
            infrastructureDetailRowsUpserted += flushResult.infrastructureDetailRowsUpserted();

            int persistedRows = staffRowsUpserted + infrastructureRowsUpserted;
            UploadStatus status = statusFor(errorRecords, persistedRows);
            updateUpload(upload.getId(), status, dataRows, validRecords, errorRecords,
                    errorRecords == 0 ? null : "CSV processing found " + errorRecords + " error(s)");

            return new HealthSectorialProcessingResult(1, dataRows, skippedRows, validRecords,
                    staffRowsUpserted, specialtyRowsUpserted, infrastructureRowsUpserted,
                    infrastructureDetailRowsUpserted, writeFinalData ? minimalHealthUnitsCreated : 0, 0, errorRecords);
        } catch (IOException e) {
            UploadErrorDraft error = error(null, null, null, "UPLOAD_STORAGE_ERROR", "Could not read stored CSV file");
            dataUploadErrorRepository.appendErrors(upload.getId(), List.of(error));
            updateUpload(upload.getId(), UploadStatus.error, dataRows, validRecords, errorRecords + 1, "Could not read stored CSV file");
            return new HealthSectorialProcessingResult(1, dataRows, skippedRows, validRecords,
                    staffRowsUpserted, specialtyRowsUpserted, infrastructureRowsUpserted,
                    infrastructureDetailRowsUpserted, minimalHealthUnitsCreated, 0, errorRecords + 1);
        }
    }

    private RowProcessingResult processRow(
            UploadBatchEntity batch,
            DataUploadEntity upload,
            HealthSectorialCsvRow row,
            boolean writeFinalData,
            Integer periodId,
            ProcessingCatalog catalog,
            ProcessingContext context,
            Set<String> seenClues
    ) {
        List<UploadErrorDraft> errors = new ArrayList<>();
        Short expectedYear = batch.getSourceYear();
        parseYear(row, expectedYear, errors);
        String clues = required(row, "CLUES", row.getCluesRaw(), errors);
        String institutionName = required(row, "Institucion", row.getInstitutionNameRaw(), errors);
        String stateName = required(row, "Nombre Estado", row.getStateNameRaw(), errors);
        String municipalityName = required(row, "Nombre Municipio", row.getMunicipalityNameRaw(), errors);
        String unitName = required(row, "Nombre de la Unidad", row.getUnitNameRaw(), errors);
        String establishmentTypeName = required(row, "Tipo de Establecimiento", row.getEstablishmentTypeRaw(), errors);
        String medicalUnitTypeName = required(row, "Tipologia", row.getMedicalUnitTypeRaw(), errors);
        String stateCode = normalizeStateCode(row, errors);
        String municipalityCode = normalizeMunicipalityCode(row, stateCode, errors);
        Integer totalDoctors = parseCount(row, "Total medicos", row.getTotalDoctorsRaw(), true, errors);
        Integer totalNurses = parseCount(row, "Total enfermeras", row.getTotalNursesRaw(), true, errors);
        Integer consultingRooms = parseCount(row, "TOTAL DE CONSULTORIOS", row.getTotalConsultingRoomsRaw(), true, errors);
        Integer hospitalBeds = parseCount(row, "TOTAL CAMAS AREA HOSPITALIZACION", row.getTotalHospitalBedsRaw(), true, errors);
        Map<String, Integer> specialtyQuantities = parseSpecialties(row, errors);

        if (clues != null && !seenClues.add(clues)) {
            errors.add(error(row.getCsvRowNumber(), "CLUES", clues, "DUPLICATED_CLUES_IN_FILE", "CLUES is duplicated in this file and was skipped"));
            return RowProcessingResult.invalid(errors);
        }

        if (!errors.isEmpty() || !writeFinalData) {
            return new RowProcessingResult(null, null, List.of(), List.of(), errors, errors.isEmpty(), false);
        }

        Integer healthUnitId = resolveHealthUnitId(row, context, clues, unitName, stateCode, stateName,
                municipalityCode, municipalityName, institutionName, establishmentTypeName, medicalUnitTypeName, expectedYear);
        boolean minimalCreated = context.minimalHealthUnitsCreated().remove(clues);

        HealthUnitStaffDraft staffDraft = new HealthUnitStaffDraft(
                healthUnitId, periodId, totalDoctors, totalNurses, batch.getDataSource().getId(), upload.getOriginalFileName());
        HealthUnitInfrastructureDraft infrastructureDraft = new HealthUnitInfrastructureDraft(
                healthUnitId, periodId, batch.getDataSource().getId(), upload.getOriginalFileName());
        List<PendingSpecialtyDetail> specialties = specialtyQuantities.entrySet().stream()
                .map(entry -> new PendingSpecialtyDetail(healthUnitId, entry.getKey(), entry.getValue()))
                .toList();
        List<PendingInfrastructureDetail> infrastructureDetails = List.of(
                new PendingInfrastructureDetail(healthUnitId, "total_consultorios", consultingRooms),
                new PendingInfrastructureDetail(healthUnitId, "total_camas_hospitalizacion", hospitalBeds)
        );

        return new RowProcessingResult(staffDraft, infrastructureDraft, specialties, infrastructureDetails, errors, true, minimalCreated);
    }

    private Integer resolveHealthUnitId(
            HealthSectorialCsvRow row,
            ProcessingContext context,
            String clues,
            String unitName,
            String stateCode,
            String stateName,
            String municipalityCode,
            String municipalityName,
            String institutionName,
            String establishmentTypeName,
            String medicalUnitTypeName,
            Short sourceYear
    ) {
        Integer cached = context.healthUnitIdsByClues().get(clues);
        if (cached != null) {
            return cached;
        }

        Integer existing = healthUnitWriter.findIdByClues(clues).orElse(null);
        if (existing != null) {
            context.healthUnitIdsByClues().put(clues, existing);
            return existing;
        }

        MunicipalityCatalogResult municipality = context.municipalitiesByCode().get(municipalityCode);
        if (municipality == null) {
            municipality = territoryCatalogWriter.ensureMunicipality(stateCode, stateName, municipalityCode, municipalityName);
            context.municipalitiesByCode().put(municipalityCode, municipality);
        }

        CatalogWriteResult institution = ensureCatalog(context.institutionsByName(), institutionName, institutionCatalogWriter::ensure);
        CatalogWriteResult establishmentType = ensureCatalog(context.establishmentTypesByName(), establishmentTypeName, establishmentTypeCatalogWriter::ensure);
        CatalogWriteResult medicalUnitType = ensureCatalog(context.medicalUnitTypesByName(), medicalUnitTypeName, medicalUnitTypeCatalogWriter::ensure);

        Integer created = healthUnitWriter.ensureMinimal(new HealthUnitWriteDraft(
                clues, unitName, municipality.municipalityId(), institution.id(), establishmentType.id(), medicalUnitType.id(),
                CareLevel.not_specified.name(), sourceYear, "from_sectorial_source", null, null, null, true));
        context.healthUnitIdsByClues().put(clues, created);
        context.minimalHealthUnitsCreated().add(clues);
        return created;
    }

    private CatalogWriteResult ensureCatalog(Map<String, CatalogWriteResult> cache, String name, CatalogEnsurer ensurer) {
        CatalogWriteResult cached = cache.get(name);
        if (cached != null) {
            return new CatalogWriteResult(cached.id(), false);
        }
        CatalogWriteResult result = ensurer.ensure(name);
        cache.put(name, result);
        return result;
    }

    private FlushResult flushChunk(Integer uploadId, Integer periodId, ChunkBuffer chunk, boolean writeFinalData, ProcessingCatalog catalog) {
        if (!chunk.errors().isEmpty()) {
            dataUploadErrorRepository.appendErrors(uploadId, chunk.errors());
        }
        if (!writeFinalData) {
            return new FlushResult(0, 0, 0, 0);
        }

        int staffRows = healthUnitStaffWriter.upsert(chunk.staff());
        int infrastructureRows = healthUnitInfrastructureWriter.upsert(chunk.infrastructure());
        Set<Integer> healthUnitIds = chunk.staff().stream().map(HealthUnitStaffDraft::healthUnitId).collect(Collectors.toSet());
        Map<Integer, Integer> staffIds = healthUnitStaffWriter.findIdsByHealthUnitIds(periodId, healthUnitIds);
        Map<Integer, Integer> infrastructureIds = healthUnitInfrastructureWriter.findIdsByHealthUnitIds(periodId, healthUnitIds);

        List<HealthUnitStaffSpecialtyDraft> specialtyDrafts = chunk.specialties().stream()
                .map(value -> new HealthUnitStaffSpecialtyDraft(staffIds.get(value.healthUnitId()),
                        catalog.specialtyIdsByCode().get(value.specialtyCode()), value.quantity()))
                .filter(value -> value.healthUnitStaffId() != null && value.specialtyId() != null)
                .toList();
        List<HealthUnitInfrastructureDetailDraft> infrastructureDetailDrafts = chunk.infrastructureDetails().stream()
                .map(value -> new HealthUnitInfrastructureDetailDraft(infrastructureIds.get(value.healthUnitId()),
                        catalog.infrastructureTypeIdsByCode().get(value.infrastructureTypeCode()), value.quantity()))
                .filter(value -> value.healthUnitInfrastructureId() != null && value.infrastructureTypeId() != null)
                .toList();

        int specialtyRows = healthUnitStaffSpecialtyWriter.upsert(specialtyDrafts);
        int infrastructureDetailRows = healthUnitInfrastructureDetailWriter.upsert(infrastructureDetailDrafts);
        return new FlushResult(staffRows, specialtyRows, infrastructureRows, infrastructureDetailRows);
    }

    private ProcessingCatalog loadCatalog() {
        Map<String, IndicatorMetadata> indicators = territoryIndicatorValueWriter.findIndicatorMetadata(REQUIRED_INDICATORS);
        Set<String> missingIndicators = REQUIRED_INDICATORS.stream()
                .filter(code -> !indicators.containsKey(code))
                .collect(Collectors.toSet());
        if (!missingIndicators.isEmpty()) {
            throw new NotFoundException("UNKNOWN_INDICATOR: Missing indicators " + missingIndicators);
        }

        Map<String, Integer> indicatorIds = indicators.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().id()));
        Map<String, Integer> specialtyIds = healthUnitStaffSpecialtyWriter.findSpecialtyIdsByCode(HealthSectorialCsvAdapter.SPECIALTY_HEADERS_BY_CODE.keySet());
        Map<String, Integer> infrastructureTypeIds = healthUnitInfrastructureDetailWriter.findInfrastructureTypeIdsByCode(REQUIRED_INFRASTRUCTURE_TYPES);
        Set<String> missingInfrastructureTypes = REQUIRED_INFRASTRUCTURE_TYPES.stream()
                .filter(code -> !infrastructureTypeIds.containsKey(code))
                .collect(Collectors.toSet());
        if (!missingInfrastructureTypes.isEmpty()) {
            throw new NotFoundException("UNKNOWN_INFRASTRUCTURE_TYPE: Missing infrastructure types " + missingInfrastructureTypes);
        }

        return new ProcessingCatalog(indicators, indicatorIds, specialtyIds, infrastructureTypeIds);
    }

    private void validatePresentSpecialtySeeds(HealthSectorialCsvAdapter.HealthSectorialColumns columns, Map<String, Integer> specialtyIdsByCode) {
        Set<String> missing = columns.specialtyIndexes().keySet().stream()
                .filter(code -> !specialtyIdsByCode.containsKey(code))
                .collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new NotFoundException("UNKNOWN_SPECIALTY: Missing specialties " + missing);
        }
    }

    private List<DataAvailabilityWriteDraft> buildAvailability(
            Map<String, IndicatorMetadata> indicators,
            Short analysisYear,
            Map<String, Set<String>> availableLevelsByCode
    ) {
        List<DataAvailabilityWriteDraft> result = new ArrayList<>();
        List<String> sectorialCodes = List.of(
                "total_doctors", "total_nurses", "hospital_beds", "consulting_rooms", "doctors_per_1000", "beds_per_1000");
        List<String> levels = List.of(TerritoryLevel.country.name(), TerritoryLevel.state.name(), TerritoryLevel.municipality.name());

        for (String code : sectorialCodes) {
            IndicatorMetadata indicator = indicators.get(code);
            Set<String> availableLevels = availableLevelsByCode.getOrDefault(code, Set.of());
            for (String level : levels) {
                boolean available = availableLevels.contains(level);
                boolean rate = code.endsWith("_per_1000");
                result.add(new DataAvailabilityWriteDraft(
                        indicator.categoryId(), indicator.id(), level, analysisYear,
                        available ? analysisYear : null,
                        available,
                        available ? AvailabilityStatus.available.name() : AvailabilityStatus.not_available.name(),
                        available ? null : (rate ? RATE_NOT_AVAILABLE_NOTE : "No se cargaron datos sectoriales para el territorio/anio.")
                ));
            }
        }
        return result;
    }

    private Short parseYear(HealthSectorialCsvRow row, Short expectedYear, List<UploadErrorDraft> errors) {
        String value = required(row, "ANO", row.getYearRaw(), errors);
        if (value == null) {
            return null;
        }
        try {
            short year = Short.parseShort(value.trim());
            if (!Short.valueOf(year).equals(expectedYear)) {
                errors.add(error(row.getCsvRowNumber(), "ANO", row.getYearRaw(), "INVALID_YEAR", "CSV row year does not match upload batch sourceYear"));
            }
            return year;
        } catch (RuntimeException e) {
            errors.add(error(row.getCsvRowNumber(), "ANO", row.getYearRaw(), "INVALID_YEAR", "ANO must be numeric"));
            return null;
        }
    }

    private String required(HealthSectorialCsvRow row, String columnName, String rawValue, List<UploadErrorDraft> errors) {
        String value = optionalText(rawValue);
        if (value == null) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "REQUIRED_FIELD_MISSING", columnName + " is required"));
        }
        return value;
    }

    private Integer parseCount(HealthSectorialCsvRow row, String columnName, String rawValue, boolean emptyAsZero, List<UploadErrorDraft> errors) {
        if (rawValue == null || rawValue.isBlank()) {
            return emptyAsZero ? 0 : null;
        }
        try {
            String normalized = rawValue.trim().replace(",", "");
            BigDecimal decimal = new BigDecimal(normalized);
            if (decimal.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "INVALID_NUMERIC_VALUE", columnName + " must be greater than or equal to zero"));
                return 0;
            }
            return decimal.intValue();
        } catch (RuntimeException e) {
            errors.add(error(row.getCsvRowNumber(), columnName, rawValue, "INVALID_NUMERIC_VALUE", columnName + " must be numeric"));
            return 0;
        }
    }

    private Map<String, Integer> parseSpecialties(HealthSectorialCsvRow row, List<UploadErrorDraft> errors) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : row.getSpecialtyValues().entrySet()) {
            result.put(entry.getKey(), parseCount(row, entry.getKey(), entry.getValue(), true, errors));
        }
        return result;
    }

    private String normalizeStateCode(HealthSectorialCsvRow row, List<UploadErrorDraft> errors) {
        String rawValue = row.getStateCodeRaw();
        if (rawValue == null || rawValue.isBlank()) {
            errors.add(error(row.getCsvRowNumber(), "Clave Estado", rawValue, "REQUIRED_FIELD_MISSING", "State code is required"));
            return null;
        }
        String value = rawValue.trim();
        if (!value.matches("\\d{1,2}")) {
            errors.add(error(row.getCsvRowNumber(), "Clave Estado", rawValue, "INVALID_STATE_CODE", "State code must have 1 or 2 digits"));
            return null;
        }
        String normalized = value.length() == 1 ? "0" + value : value;
        if ("00".equals(normalized)) {
            errors.add(error(row.getCsvRowNumber(), "Clave Estado", rawValue, "INVALID_STATE_CODE", "State code 00 is not valid for health units"));
            return null;
        }
        return normalized;
    }

    private String normalizeMunicipalityCode(HealthSectorialCsvRow row, String stateCode, List<UploadErrorDraft> errors) {
        String rawValue = row.getMunicipalityCodeRaw();
        if (rawValue == null || rawValue.isBlank()) {
            errors.add(error(row.getCsvRowNumber(), "Clave Municipio", rawValue, "REQUIRED_FIELD_MISSING", "Municipality code is required"));
            return null;
        }
        if (stateCode == null) {
            return null;
        }
        String value = rawValue.trim();
        if (!value.matches("\\d{1,5}")) {
            errors.add(error(row.getCsvRowNumber(), "Clave Municipio", rawValue, "INVALID_MUNICIPALITY_CODE", "Municipality code must be numeric"));
            return null;
        }
        String localCode = value;
        if (value.length() == 5 && value.startsWith(stateCode)) {
            localCode = value.substring(2);
        }
        if (localCode.length() > 3) {
            errors.add(error(row.getCsvRowNumber(), "Clave Municipio", rawValue, "INVALID_MUNICIPALITY_CODE", "Municipality code must be 3 digits or a 5-digit INEGI code matching the state"));
            return null;
        }
        return stateCode + leftPad(localCode, 3);
    }

    private String optionalText(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return rawValue.trim().replaceAll("\\s+", " ");
    }

    private UploadErrorDraft error(Integer csvRowNumber, String columnName, String rawValue, String errorCode, String errorMessage) {
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

    private String leftPad(String value, int targetLength) {
        if (value.length() >= targetLength) {
            return value;
        }
        return "0".repeat(targetLength - value.length()) + value;
    }

    private void updateUpload(Integer uploadId, UploadStatus status, Integer totalRecords, Integer validRecords, Integer errorRecords, String errorDetail) {
        dataUploadRepository.updateValidationResult(uploadId, status.name(), totalRecords, validRecords, errorRecords, errorDetail);
    }

    private UploadStatus statusFor(int errorRecords, int rowsUpserted) {
        if (errorRecords == 0) {
            return UploadStatus.completed;
        }
        return rowsUpserted > 0 ? UploadStatus.warning : UploadStatus.error;
    }

    private String sourceFileForIndicators(UploadBatchEntity batch, List<DataUploadEntity> uploads) {
        if (uploads.size() == 1) {
            return uploads.get(0).getOriginalFileName();
        }
        return "batch:" + batch.getBatchVersion();
    }

    @SuppressWarnings("unused")
    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        String withoutDiacritics = Normalizer.normalize(trimmed, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutDiacritics.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private interface CatalogEnsurer {
        CatalogWriteResult ensure(String name);
    }

    private record ProcessingCatalog(
            Map<String, IndicatorMetadata> indicatorsByCode,
            Map<String, Integer> indicatorIdsByCode,
            Map<String, Integer> specialtyIdsByCode,
            Map<String, Integer> infrastructureTypeIdsByCode
    ) {
        Set<Integer> sectorialIndicatorIds() {
            return Set.of(
                    indicatorIdsByCode.get("total_doctors"),
                    indicatorIdsByCode.get("total_nurses"),
                    indicatorIdsByCode.get("hospital_beds"),
                    indicatorIdsByCode.get("consulting_rooms"),
                    indicatorIdsByCode.get("doctors_per_1000"),
                    indicatorIdsByCode.get("beds_per_1000")
            );
        }
    }

    private record ProcessingContext(
            Map<String, Integer> healthUnitIdsByClues,
            Map<String, MunicipalityCatalogResult> municipalitiesByCode,
            Map<String, CatalogWriteResult> institutionsByName,
            Map<String, CatalogWriteResult> establishmentTypesByName,
            Map<String, CatalogWriteResult> medicalUnitTypesByName,
            Set<String> minimalHealthUnitsCreated
    ) {
        ProcessingContext() {
            this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashSet<>());
        }
    }

    private record RowProcessingResult(
            HealthUnitStaffDraft staffDraft,
            HealthUnitInfrastructureDraft infrastructureDraft,
            List<PendingSpecialtyDetail> specialties,
            List<PendingInfrastructureDetail> infrastructureDetails,
            List<UploadErrorDraft> errors,
            boolean validRecord,
            boolean minimalHealthUnitCreated
    ) {
        static RowProcessingResult invalid(List<UploadErrorDraft> errors) {
            return new RowProcessingResult(null, null, List.of(), List.of(), errors, false, false);
        }

        java.util.Optional<HealthUnitStaffDraft> staffDraftOptional() {
            return java.util.Optional.ofNullable(staffDraft);
        }

        java.util.Optional<HealthUnitInfrastructureDraft> infrastructureDraftOptional() {
            return java.util.Optional.ofNullable(infrastructureDraft);
        }
    }

    private record ChunkBuffer(
            List<HealthUnitStaffDraft> staff,
            List<HealthUnitInfrastructureDraft> infrastructure,
            List<PendingSpecialtyDetail> specialties,
            List<PendingInfrastructureDetail> infrastructureDetails,
            List<UploadErrorDraft> errors
    ) {
        ChunkBuffer() {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        boolean shouldFlush() {
            return staff.size() >= CHUNK_SIZE || errors.size() >= CHUNK_SIZE;
        }

        void clear() {
            staff.clear();
            infrastructure.clear();
            specialties.clear();
            infrastructureDetails.clear();
            errors.clear();
        }
    }

    private record PendingSpecialtyDetail(Integer healthUnitId, String specialtyCode, Integer quantity) {
    }

    private record PendingInfrastructureDetail(Integer healthUnitId, String infrastructureTypeCode, Integer quantity) {
    }

    private record FlushResult(
            int staffRowsUpserted,
            int specialtyRowsUpserted,
            int infrastructureRowsUpserted,
            int infrastructureDetailRowsUpserted
    ) {
    }
}
