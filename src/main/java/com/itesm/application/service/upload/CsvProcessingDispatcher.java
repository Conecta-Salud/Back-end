package com.itesm.application.service.upload;

import com.itesm.application.service.upload.establishments.HealthEstablishmentProcessingResult;
import com.itesm.application.service.upload.establishments.HealthEstablishmentsCsvProcessor;
import com.itesm.application.service.upload.population.PopulationIndicatorsCsvProcessor;
import com.itesm.application.service.upload.population.PopulationProcessingResult;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CsvProcessingDispatcher {

    private final DataUploadRepository dataUploadRepository;
    private final PopulationIndicatorsCsvProcessor populationIndicatorsCsvProcessor;
    private final HealthEstablishmentsCsvProcessor healthEstablishmentsCsvProcessor;

    public CsvProcessingDispatcher(
            DataUploadRepository dataUploadRepository,
            PopulationIndicatorsCsvProcessor populationIndicatorsCsvProcessor,
            HealthEstablishmentsCsvProcessor healthEstablishmentsCsvProcessor
    ) {
        this.dataUploadRepository = dataUploadRepository;
        this.populationIndicatorsCsvProcessor = populationIndicatorsCsvProcessor;
        this.healthEstablishmentsCsvProcessor = healthEstablishmentsCsvProcessor;
    }

    public CsvProcessingResult dispatch(
            UploadBatchEntity batch,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        List<DataUploadEntity> uploads = dataUploadRepository.findByBatchId(batch.getId());

        CsvProcessingResult result = switch (batch.getSourceType()) {
            case population -> processPopulation(batch, uploads, mode, replaceExistingForYear);
            case health_sectorial -> stub("Health sectorial CSV batch accepted. Transformation to health_unit_staff and health_unit_infrastructure is deferred to the next implementation block.");
            case health_establishments -> processHealthEstablishments(batch, uploads, mode, replaceExistingForYear);
        };

        return new CsvProcessingResult(
                result.status(),
                result.message() + " mode=" + mode.name() + ", replaceExistingForYear=" + replaceExistingForYear
        );
    }

    private CsvProcessingResult processPopulation(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        PopulationProcessingResult result = populationIndicatorsCsvProcessor.process(
                batch,
                uploads,
                mode,
                replaceExistingForYear
        );

        return new CsvProcessingResult(
                statusFor(result),
                "Population indicators processed: "
                + result.filesProcessed()
                + " files, "
                + result.dataRows()
                + " data rows, "
                + result.skippedRows()
                + " metadata rows skipped, "
                + result.unsupportedPeriodRows()
                + " unsupported period rows skipped, "
                + result.valuesUpserted()
                + " values upserted, "
                + result.errorRecords()
                + " errors."
        );
    }

    private CsvProcessingResult stub(String message) {
        return new CsvProcessingResult(UploadStatus.completed, message);
    }

    private CsvProcessingResult processHealthEstablishments(
            UploadBatchEntity batch,
            List<DataUploadEntity> uploads,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        HealthEstablishmentProcessingResult result = healthEstablishmentsCsvProcessor.process(
                batch,
                uploads,
                mode,
                replaceExistingForYear
        );

        return new CsvProcessingResult(
                statusFor(result),
                "Health establishments processed: "
                + result.filesProcessed()
                + " files, "
                + result.dataRows()
                + " rows, "
                + result.skippedRows()
                + " skipped rows, "
                + result.healthUnitsUpserted()
                + " health units upserted, "
                + result.catalogValuesChanged()
                + " catalog values created/updated, "
                + result.territorialIndicatorsUpserted()
                + " territorial indicators upserted, "
                + result.errorRecords()
                + " errors."
        );
    }

    private UploadStatus statusFor(PopulationProcessingResult result) {
        if (result.errorRecords() == 0) {
            return UploadStatus.completed;
        }

        return result.valuesUpserted() > 0 ? UploadStatus.warning : UploadStatus.error;
    }

    private UploadStatus statusFor(HealthEstablishmentProcessingResult result) {
        if (result.errorRecords() == 0) {
            return UploadStatus.completed;
        }

        return result.healthUnitsUpserted() > 0 ? UploadStatus.warning : UploadStatus.error;
    }
}
