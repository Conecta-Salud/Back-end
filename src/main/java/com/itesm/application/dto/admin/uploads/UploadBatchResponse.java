package com.itesm.application.dto.admin.uploads;

import java.time.LocalDateTime;

public class UploadBatchResponse {

    private final Integer id;
    private final String sourceType;
    private final String dataSourceCode;
    private final String dataSourceName;
    private final Integer sourceYear;
    private final Integer analysisYear;
    private final String batchVersion;
    private final String processingMode;
    private final String status;
    private final Integer expectedFiles;
    private final Integer uploadedFiles;
    private final Integer totalRecords;
    private final Integer validRecords;
    private final Integer errorRecords;
    private final String errorDetail;
    private final LocalDateTime createdAt;
    private final LocalDateTime processedAt;

    public UploadBatchResponse(
            Integer id,
            String sourceType,
            String dataSourceCode,
            String dataSourceName,
            Integer sourceYear,
            Integer analysisYear,
            String batchVersion,
            String processingMode,
            String status,
            Integer expectedFiles,
            Integer uploadedFiles,
            Integer totalRecords,
            Integer validRecords,
            Integer errorRecords,
            String errorDetail,
            LocalDateTime createdAt,
            LocalDateTime processedAt
    ) {
        this.id = id;
        this.sourceType = sourceType;
        this.dataSourceCode = dataSourceCode;
        this.dataSourceName = dataSourceName;
        this.sourceYear = sourceYear;
        this.analysisYear = analysisYear;
        this.batchVersion = batchVersion;
        this.processingMode = processingMode;
        this.status = status;
        this.expectedFiles = expectedFiles;
        this.uploadedFiles = uploadedFiles;
        this.totalRecords = totalRecords;
        this.validRecords = validRecords;
        this.errorRecords = errorRecords;
        this.errorDetail = errorDetail;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public Integer getId() { return id; }
    public String getSourceType() { return sourceType; }
    public String getDataSourceCode() { return dataSourceCode; }
    public String getDataSourceName() { return dataSourceName; }
    public Integer getSourceYear() { return sourceYear; }
    public Integer getAnalysisYear() { return analysisYear; }
    public String getBatchVersion() { return batchVersion; }
    public String getProcessingMode() { return processingMode; }
    public String getStatus() { return status; }
    public Integer getExpectedFiles() { return expectedFiles; }
    public Integer getUploadedFiles() { return uploadedFiles; }
    public Integer getTotalRecords() { return totalRecords; }
    public Integer getValidRecords() { return validRecords; }
    public Integer getErrorRecords() { return errorRecords; }
    public String getErrorDetail() { return errorDetail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
