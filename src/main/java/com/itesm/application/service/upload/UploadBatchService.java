package com.itesm.application.service.upload;

import com.itesm.application.dto.admin.uploads.*;
import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.upload.CsvFileRole;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadSourceType;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadErrorRepository;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.domain.repository.UploadBatchRepository;
import com.itesm.infrastructure.persistence.entity.DataSourceEntity;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.DataUploadErrorEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UploadBatchService {

    private final UploadBatchRepository uploadBatchRepository;
    private final DataUploadRepository dataUploadRepository;
    private final DataUploadErrorRepository dataUploadErrorRepository;
    private final CsvStorageService csvStorageService;
    private final CsvUploadContractRegistry csvUploadContractRegistry;
    private final CsvValidationService csvValidationService;
    private final CsvProcessingDispatcher csvProcessingDispatcher;

    public UploadBatchService(
            UploadBatchRepository uploadBatchRepository,
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            CsvStorageService csvStorageService,
            CsvUploadContractRegistry csvUploadContractRegistry,
            CsvValidationService csvValidationService,
            CsvProcessingDispatcher csvProcessingDispatcher
    ) {
        this.uploadBatchRepository = uploadBatchRepository;
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.csvStorageService = csvStorageService;
        this.csvUploadContractRegistry = csvUploadContractRegistry;
        this.csvValidationService = csvValidationService;
        this.csvProcessingDispatcher = csvProcessingDispatcher;
    }

    public UploadBatchResponse createBatch(CreateUploadBatchRequest request, UUID userId) {
        if (request == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: request body is required");
        }

        UploadSourceType sourceType = parseSourceType(request.getSourceType(), true);
        UploadProcessingMode processingMode = parseProcessingMode(request.getProcessingMode(), false);

        if (request.getDataSourceCode() == null || request.getDataSourceCode().isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: dataSourceCode is required");
        }

        if (request.getSourceYear() == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: sourceYear is required");
        }

        if (request.getExpectedFiles() == null || request.getExpectedFiles() < 1) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: expectedFiles must be >= 1");
        }

        if (request.getBatchVersion() == null || request.getBatchVersion().isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: batchVersion is required");
        }

        DataSourceEntity dataSource = uploadBatchRepository.findDataSourceByCode(request.getDataSourceCode())
                .orElseThrow(() -> new NotFoundException("UNKNOWN_DATA_SOURCE: dataSourceCode does not exist"));

        UploadBatchEntity batch = new UploadBatchEntity();
        batch.setSourceType(sourceType);
        batch.setDataSource(dataSource);
        batch.setSourceYear(toShortYear(request.getSourceYear(), "sourceYear"));
        batch.setAnalysisYear(request.getAnalysisYear() == null ? null : toShortYear(request.getAnalysisYear(), "analysisYear"));
        batch.setExpectedFiles(request.getExpectedFiles());
        batch.setBatchVersion(request.getBatchVersion().trim());
        batch.setProcessingMode(processingMode);
        batch.setStatus(UploadStatus.pending);

        UploadBatchEntity created = uploadBatchRepository.create(batch, userId, dataSource.getId());
        return toBatchResponse(created);
    }

    public UploadFileResponse uploadFile(
            Integer batchId,
            String fileRole,
            String originalFileName,
            String mimeType,
            InputStream inputStream
    ) {
        if (inputStream == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: file is required");
        }

        UploadBatchEntity batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_BATCH: Upload batch not found"));
        CsvFileRole parsedFileRole = parseFileRole(fileRole);
        assertCanUploadFile(batch);
        assertFileRoleAllowed(batch.getSourceType(), parsedFileRole);
        assertExpectedFilesNotExceeded(batch);
        if (!allowsMultipleFiles(parsedFileRole)) {
            assertFileRoleNotDuplicated(batch.getId(), parsedFileRole);
        }

        StoredCsvFile storedFile = csvStorageService.store(batch.getId(), originalFileName, mimeType, inputStream);

        if (dataUploadRepository.existsChecksumInBatch(batch.getId(), storedFile.getChecksum())) {
            csvStorageService.deleteQuietly(storedFile);
            throw new BadRequestException("DUPLICATED_FILE: A file with the same checksum already exists in this batch");
        }

        DataUploadEntity upload = new DataUploadEntity();
        upload.setFileRole(parsedFileRole);
        upload.setOriginalFileName(storedFile.getOriginalFileName());
        upload.setStoredFileName(storedFile.getStoredFileName());
        upload.setFileVersion(batch.getBatchVersion());
        upload.setFileSize(storedFile.getFileSize());
        upload.setMimeType(storedFile.getMimeType());
        upload.setChecksum(storedFile.getChecksum());
        upload.setStatus(UploadStatus.pending);

        DataUploadEntity created;
        try {
            created = dataUploadRepository.create(upload, batch.getId());
        } catch (RuntimeException e) {
            csvStorageService.deleteQuietly(storedFile.getStoredFileName());
            throw e;
        }

        uploadBatchRepository.recalculateCounters(batch.getId());
        return new UploadFileResponse(toFileSummaryResponse(created));
    }

    public ValidateUploadResponse validateUpload(Integer uploadId) {
        DataUploadEntity upload = dataUploadRepository.findById(uploadId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_UPLOAD: Upload not found"));

        assertCanValidateUpload(upload);
        return csvValidationService.validate(upload);
    }

    public ProcessUploadBatchResponse processBatch(Integer batchId, ProcessUploadBatchRequest request) {
        if (request == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: request body is required");
        }

        UploadBatchEntity batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_BATCH: Upload batch not found"));
        UploadProcessingMode mode = parseProcessingMode(request.getMode(), true);
        boolean replaceExistingForYear = Boolean.TRUE.equals(request.getReplaceExistingForYear());
        boolean failOnErrors = Boolean.TRUE.equals(request.getFailOnErrors());

        assertCanProcessBatch(batch);

        List<DataUploadEntity> uploads = dataUploadRepository.findByBatchId(batchId);
        if (uploads.isEmpty()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: Batch has no uploaded files");
        }

        uploadBatchRepository.recalculateCounters(batchId);
        batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_BATCH: Upload batch not found"));
        boolean hasRegisteredErrors = uploads.stream()
                .anyMatch(upload -> dataUploadErrorRepository.countByUploadId(upload.getId()) > 0);

        if (failOnErrors && (hasRegisteredErrors || safeInteger(batch.getErrorRecords()) > 0)) {
            throw new BadRequestException("BATCH_HAS_ERRORS: batchId=" + batchId + " has validation errors");
        }

        uploadBatchRepository.updateStatus(batchId, UploadStatus.processing, null, false);

        try {
            CsvProcessingResult processingResult = csvProcessingDispatcher.dispatch(batch, mode, replaceExistingForYear);
            uploadBatchRepository.recalculateCounters(batchId);
            UploadStatus finalStatus = processingResult.status();

            uploadBatchRepository.updateStatus(
                    batchId,
                    finalStatus,
                    finalStatus == UploadStatus.completed ? null : processingResult.message(),
                    true
            );

            return new ProcessUploadBatchResponse(
                    batchId,
                    finalStatus.name(),
                    batch.getSourceType().name(),
                    mode.name(),
                    replaceExistingForYear,
                    failOnErrors,
                    processingResult.message()
            );
        } catch (RuntimeException e) {
            String message = e.getMessage() == null ? "CSV batch processing failed" : e.getMessage();
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
    }

    public PageResponseDto<UploadBatchResponse> findBatches(
            String sourceType,
            Integer sourceYear,
            String status,
            int page,
            int size
    ) {
        UploadSourceType parsedSourceType = parseSourceType(sourceType, false);
        UploadStatus parsedStatus = parseStatus(status, false);
        PageResult<UploadBatchEntity> result = uploadBatchRepository.findBatches(
                parsedSourceType,
                sourceYear,
                parsedStatus,
                page,
                size
        );

        return new PageResponseDto<>(
                result.getItems().stream().map(this::toBatchResponse).toList(),
                result.getTotalItems(),
                result.getPage(),
                result.getSize(),
                result.getTotalPages()
        );
    }

    public UploadBatchDetailResponse findBatchDetail(Integer batchId) {
        UploadBatchEntity batch = uploadBatchRepository.findById(batchId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_BATCH: Upload batch not found"));
        List<UploadFileSummaryResponse> files = dataUploadRepository.findByBatchId(batchId)
                .stream()
                .map(this::toFileSummaryResponse)
                .toList();

        return new UploadBatchDetailResponse(
                toBatchResponse(batch),
                files
        );
    }

    public PageResponseDto<UploadErrorResponse> findUploadErrors(Integer uploadId, int page, int size) {
        dataUploadRepository.findById(uploadId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_UPLOAD: Upload not found"));

        PageResult<DataUploadErrorEntity> result = dataUploadErrorRepository.findByUploadId(uploadId, page, size);

        return new PageResponseDto<>(
                result.getItems().stream().map(this::toErrorResponse).toList(),
                result.getTotalItems(),
                result.getPage(),
                result.getSize(),
                result.getTotalPages()
        );
    }

    private void assertCanUploadFile(UploadBatchEntity batch) {
        UploadStatus status = batch.getStatus();

        if (status != UploadStatus.pending && status != UploadStatus.warning) {
            throw new BadRequestException("INVALID_BATCH_STATUS: Files can only be uploaded to pending or warning batches");
        }
    }

    private void assertCanValidateUpload(DataUploadEntity upload) {
        UploadStatus status = upload.getStatus();

        if (status == UploadStatus.processing || status == UploadStatus.completed) {
            throw new BadRequestException("INVALID_UPLOAD_STATUS: Upload can only be validated while pending, warning or error");
        }
    }

    private void assertCanProcessBatch(UploadBatchEntity batch) {
        UploadStatus status = batch.getStatus();

        if (status != UploadStatus.pending && status != UploadStatus.warning) {
            throw new BadRequestException("INVALID_BATCH_STATUS: Batch can only be processed while pending or warning");
        }
    }

    private void assertFileRoleAllowed(UploadSourceType sourceType, CsvFileRole fileRole) {
        if (!csvUploadContractRegistry.isFileRoleAllowed(sourceType, fileRole)) {
            throw new BadRequestException(
                    "INVALID_FILE_ROLE_FOR_SOURCE_TYPE: sourceType=" + sourceType + " no permite fileRole=" + fileRole
            );
        }
    }

    private void assertExpectedFilesNotExceeded(UploadBatchEntity batch) {
        long currentFiles = dataUploadRepository.countByBatchId(batch.getId());

        if (currentFiles >= safeInteger(batch.getExpectedFiles())) {
            throw new BadRequestException(
                    "EXPECTED_FILES_EXCEEDED: batchId=" + batch.getId()
                            + ", expectedFiles=" + batch.getExpectedFiles()
                            + ", currentFiles=" + currentFiles
            );
        }
    }

    private void assertFileRoleNotDuplicated(Integer batchId, CsvFileRole fileRole) {
        if (dataUploadRepository.existsByBatchIdAndFileRole(batchId, fileRole)) {
            throw new BadRequestException(
                    "DUPLICATED_FILE_ROLE_IN_BATCH: batchId=" + batchId + ", fileRole=" + fileRole
            );
        }
    }

    private boolean allowsMultipleFiles(CsvFileRole fileRole) {
        return fileRole == CsvFileRole.population_indicators
                || fileRole == CsvFileRole.population_municipal_base;
    }

    private UploadBatchResponse toBatchResponse(UploadBatchEntity batch) {
        DataSourceEntity dataSource = batch.getDataSource();

        return new UploadBatchResponse(
                batch.getId(),
                enumName(batch.getSourceType()),
                dataSource == null ? null : dataSource.getCode(),
                dataSource == null ? null : dataSource.getName(),
                toInteger(batch.getSourceYear()),
                toInteger(batch.getAnalysisYear()),
                batch.getBatchVersion(),
                enumName(batch.getProcessingMode()),
                enumName(batch.getStatus()),
                batch.getExpectedFiles(),
                batch.getUploadedFiles(),
                batch.getTotalRecords(),
                batch.getValidRecords(),
                batch.getErrorRecords(),
                batch.getErrorDetail(),
                batch.getCreatedAt(),
                batch.getProcessedAt()
        );
    }

    private UploadFileSummaryResponse toFileSummaryResponse(DataUploadEntity upload) {
        return new UploadFileSummaryResponse(
                upload.getId(),
                enumName(upload.getFileRole()),
                upload.getOriginalFileName(),
                upload.getStoredFileName(),
                upload.getFileSize(),
                upload.getMimeType(),
                upload.getChecksum(),
                enumName(upload.getStatus()),
                upload.getTotalRecords(),
                upload.getValidRecords(),
                upload.getErrorRecords(),
                upload.getErrorDetail(),
                upload.getCreatedAt(),
                upload.getProcessedAt()
        );
    }

    private UploadErrorResponse toErrorResponse(DataUploadErrorEntity error) {
        return new UploadErrorResponse(
                error.getId(),
                error.getDataUpload() == null ? null : error.getDataUpload().getId(),
                error.getCsvRowNumber(),
                error.getColumnName(),
                error.getRawValue(),
                error.getErrorCode(),
                error.getErrorMessage()
        );
    }

    private UploadSourceType parseSourceType(String value, boolean required) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new BadRequestException("REQUIRED_FIELD_MISSING: sourceType is required");
            }
            return null;
        }

        try {
            return UploadSourceType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_SOURCE_TYPE: sourceType is not supported");
        }
    }

    private UploadProcessingMode parseProcessingMode(String value, boolean required) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new BadRequestException("REQUIRED_FIELD_MISSING: processingMode is required");
            }
            return UploadProcessingMode.validate_only;
        }

        try {
            return UploadProcessingMode.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_PROCESSING_MODE: processingMode is not supported");
        }
    }

    private CsvFileRole parseFileRole(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: fileRole is required");
        }

        try {
            return CsvFileRole.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_FILE_ROLE: fileRole is not supported");
        }
    }

    private UploadStatus parseStatus(String value, boolean required) {
        if (value == null || value.isBlank()) {
            if (required) {
                throw new BadRequestException("REQUIRED_FIELD_MISSING: status is required");
            }
            return null;
        }

        try {
            return UploadStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_STATUS: status is not supported");
        }
    }

    private Short toShortYear(Integer value, String fieldName) {
        if (value == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + fieldName + " is required");
        }

        if (value < 1900 || value > 2100) {
            throw new BadRequestException("INVALID_YEAR: " + fieldName + " must be between 1900 and 2100");
        }

        return value.shortValue();
    }

    private Integer toInteger(Short value) {
        return value == null ? null : value.intValue();
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
