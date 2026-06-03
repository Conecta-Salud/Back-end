package com.itesm.application.dto.admin.uploads;

public class UploadErrorResponse {

    private final Long id;
    private final Integer uploadId;
    private final Integer csvRowNumber;
    private final String columnName;
    private final String rawValue;
    private final String errorCode;
    private final String errorMessage;

    public UploadErrorResponse(
            Long id,
            Integer uploadId,
            Integer csvRowNumber,
            String columnName,
            String rawValue,
            String errorCode,
            String errorMessage
    ) {
        this.id = id;
        this.uploadId = uploadId;
        this.csvRowNumber = csvRowNumber;
        this.columnName = columnName;
        this.rawValue = rawValue;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Long getId() { return id; }
    public Integer getUploadId() { return uploadId; }
    public Integer getCsvRowNumber() { return csvRowNumber; }
    public String getColumnName() { return columnName; }
    public String getRawValue() { return rawValue; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
}
