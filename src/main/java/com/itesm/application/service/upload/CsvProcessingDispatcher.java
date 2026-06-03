package com.itesm.application.service.upload;

import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CsvProcessingDispatcher {

    public String dispatch(
            UploadBatchEntity batch,
            UploadProcessingMode mode,
            boolean replaceExistingForYear
    ) {
        return switch (batch.getSourceType()) {
            case population -> "Population CSV batch accepted. Transformation to territory_indicator_values is deferred to the next implementation block.";
            case health_sectorial -> "Health sectorial CSV batch accepted. Transformation to health_unit_staff and health_unit_infrastructure is deferred to the next implementation block.";
            case health_establishments -> "Health establishments CSV batch accepted. Transformation to health_units catalog is deferred to the next implementation block.";
        } + " mode=" + mode.name() + ", replaceExistingForYear=" + replaceExistingForYear;
    }

}
