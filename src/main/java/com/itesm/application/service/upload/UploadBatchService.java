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

    public UploadBatchService(
            UploadBatchRepository uploadBatchRepository,
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            CsvStorageService csvStorageService
    ) {
        this.uploadBatchRepository = uploadBatchRepository;
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.csvStorageService = csvStorageService;
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
                .orElseThrow(() -> new BadRequestException("UNKNOWN_DATA_SOURCE: dataSourceCode does not exist"));

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

        DataUploadEntity created = dataUploadRepository.create(upload, batch.getId());
        uploadBatchRepository.recalculateCounters(batch.getId());

        return new UploadFileResponse(toFileSummaryResponse(created));
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

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
