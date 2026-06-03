package com.itesm.application.service.upload;

import com.itesm.application.service.upload.population.PopulationIndicatorsCsvProcessor;
import com.itesm.application.service.upload.population.PopulationProcessingResult;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CsvProcessingDispatcher {

    private final DataUploadRepository dataUploadRepository;
    private final PopulationIndicatorsCsvProcessor populationIndicatorsCsvProcessor;

    public CsvProcessingDispatcher(
            DataUploadRepository dataUploadRepository,
            PopulationIndicatorsCsvProcessor populationIndicatorsCsvProcessor
    ) {
        this.dataUploadRepository = dataUploadRepository;
        this.populationIndicatorsCsvProcessor = populationIndicatorsCsvProcessor;
    }

    public String dispatch(
            UploadBatchEntity batch,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        List<DataUploadEntity> uploads = dataUploadRepository.findByBatchId(batch.getId());

        return switch (batch.getSourceType()) {
            case population -> processPopulation(batch, uploads, mode, replaceExistingForYear);
            case health_sectorial -> "Health sectorial CSV batch accepted. Transformation to health_unit_staff and health_unit_infrastructure is deferred to the next implementation block.";
            case health_establishments -> "Health establishments CSV batch accepted. Transformation to health_units catalog is deferred to the next implementation block.";
        } + " mode=" + mode.name() + ", replaceExistingForYear=" + replaceExistingForYear;
    }

    private String processPopulation(
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

        return "Population indicators processed: "
                + result.records()
                + " records, "
                + result.valuesUpserted()
                + " values upserted, "
                + result.errors()
                + " errors.";
    }
}
