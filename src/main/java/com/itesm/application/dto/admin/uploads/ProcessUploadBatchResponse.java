package com.itesm.application.dto.admin.uploads;

public class ProcessUploadBatchResponse {

    private final Integer batchId;
    private final String status;
    private final String sourceType;
    private final String mode;
    private final boolean replaceExistingForYear;
    private final boolean failOnErrors;
    private final String message;

    public ProcessUploadBatchResponse(
            Integer batchId,
            String status,
            String sourceType,
            String mode,
            boolean replaceExistingForYear,
            boolean failOnErrors,
            String message
    ) {
        this.batchId = batchId;
        this.status = status;
        this.sourceType = sourceType;
        this.mode = mode;
        this.replaceExistingForYear = replaceExistingForYear;
        this.failOnErrors = failOnErrors;
        this.message = message;
    }

    public Integer getBatchId() { return batchId; }
    public String getStatus() { return status; }
    public String getSourceType() { return sourceType; }
    public String getMode() { return mode; }
    public boolean isReplaceExistingForYear() { return replaceExistingForYear; }
    public boolean isFailOnErrors() { return failOnErrors; }
    public String getMessage() { return message; }
}
