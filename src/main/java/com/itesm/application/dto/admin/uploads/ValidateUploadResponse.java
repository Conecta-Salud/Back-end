package com.itesm.application.dto.admin.uploads;

public class ValidateUploadResponse {

    private final Integer uploadId;
    private final String status;
    private final Integer totalRecords;
    private final Integer validRecords;
    private final Integer errorRecords;

    public ValidateUploadResponse(
            Integer uploadId,
            String status,
            Integer totalRecords,
            Integer validRecords,
            Integer errorRecords
    ) {
        this.uploadId = uploadId;
        this.status = status;
        this.totalRecords = totalRecords;
        this.validRecords = validRecords;
        this.errorRecords = errorRecords;
    }

    public Integer getUploadId() { return uploadId; }
    public String getStatus() { return status; }
    public Integer getTotalRecords() { return totalRecords; }
    public Integer getValidRecords() { return validRecords; }
    public Integer getErrorRecords() { return errorRecords; }
}
