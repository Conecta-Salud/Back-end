package com.itesm.application.service.upload;

import com.itesm.application.dto.admin.uploads.ProcessUploadBatchRequest;
import com.itesm.application.dto.admin.uploads.ProcessUploadBatchResponse;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.domain.repository.UploadBatchRepository;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class CsvProcessingDispatcher {

    private final UploadBatchRepository uploadBatchRepository;
    private final DataUploadRepository dataUploadRepository;

    public CsvProcessingDispatcher(
            UploadBatchRepository uploadBatchRepository,
            DataUploadRepository dataUploadRepository
    ) {
        this.uploadBatchRepository = uploadBatchRepository;
        this.dataUploadRepository = dataUploadRepository;
    }

    public ProcessUploadBatchResponse process(Integer batchId, ProcessUploadBatchRequest request) {
        UploadBatchEntity batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_BATCH: Upload batch not found"));

        UploadProcessingMode mode = parseProcessingMode(request == null ? null : request.getMode(), true);
        boolean replaceExistingForYear = request != null && Boolean.TRUE.equals(request.getReplaceExistingForYear());
        boolean failOnErrors = request != null && Boolean.TRUE.equals(request.getFailOnErrors());

        int uploadedFiles = dataUploadRepository.findByBatchId(batchId).size();
        if (uploadedFiles == 0) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: Batch has no uploaded files");
        }

        uploadBatchRepository.recalculateCounters(batchId);
        batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_BATCH: Upload batch not found"));

        if (failOnErrors && batch.getErrorRecords() != null && batch.getErrorRecords() > 0) {
            String message = "Batch has validation errors and failOnErrors=true";
            uploadBatchRepository.updateStatus(batchId, UploadStatus.error, message, true);
            return new ProcessUploadBatchResponse(
                    batchId,
                    UploadStatus.error.name(),
                    batch.getSourceType().name(),
                    mode.name(),
                    replaceExistingForYear,
                    failOnErrors,
                    message
            );
        }

        uploadBatchRepository.updateStatus(batchId, UploadStatus.processing, null, false);
        String message = dispatchStub(batch, mode, replaceExistingForYear);
        UploadStatus finalStatus = batch.getErrorRecords() != null && batch.getErrorRecords() > 0
                ? UploadStatus.warning
                : UploadStatus.completed;
        uploadBatchRepository.updateStatus(batchId, finalStatus, message, true);

        return new ProcessUploadBatchResponse(
                batchId,
                finalStatus.name(),
                batch.getSourceType().name(),
                mode.name(),
                replaceExistingForYear,
                failOnErrors,
                message
        );
    }

    private String dispatchStub(
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

    private UploadProcessingMode parseProcessingMode(String value, boolean required) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new BadRequestException("REQUIRED_FIELD_MISSING: mode is required");
            }
            return UploadProcessingMode.validate_only;
        }

        try {
            return UploadProcessingMode.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_PROCESSING_MODE: Processing mode is not supported");
        }
    }
}
