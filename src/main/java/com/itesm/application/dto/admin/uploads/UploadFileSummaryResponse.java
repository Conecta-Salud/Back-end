package com.itesm.application.dto.admin.uploads;

import java.time.LocalDateTime;

public class UploadFileSummaryResponse {

    private final Integer id;
    private final String fileRole;
    private final String originalFileName;
    private final String storedFileName;
    private final Long fileSize;
    private final String mimeType;
    private final String checksum;
    private final String status;
    private final Integer totalRecords;
    private final Integer validRecords;
    private final Integer errorRecords;
    private final String errorDetail;
    private final LocalDateTime createdAt;
    private final LocalDateTime processedAt;

    public UploadFileSummaryResponse(
            Integer id,
            String fileRole,
            String originalFileName,
            String storedFileName,
            Long fileSize,
            String mimeType,
            String checksum,
            String status,
            Integer totalRecords,
            Integer validRecords,
            Integer errorRecords,
            String errorDetail,
            LocalDateTime createdAt,
            LocalDateTime processedAt
    ) {
        this.id = id;
        this.fileRole = fileRole;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.checksum = checksum;
        this.status = status;
        this.totalRecords = totalRecords;
        this.validRecords = validRecords;
        this.errorRecords = errorRecords;
        this.errorDetail = errorDetail;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public Integer getId() { return id; }
    public String getFileRole() { return fileRole; }
    public String getOriginalFileName() { return originalFileName; }
    public String getStoredFileName() { return storedFileName; }
    public Long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public String getChecksum() { return checksum; }
    public String getStatus() { return status; }
    public Integer getTotalRecords() { return totalRecords; }
    public Integer getValidRecords() { return validRecords; }
    public Integer getErrorRecords() { return errorRecords; }
    public String getErrorDetail() { return errorDetail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
