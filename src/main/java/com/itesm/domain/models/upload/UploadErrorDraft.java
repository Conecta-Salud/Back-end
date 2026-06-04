package com.itesm.domain.models.upload;

public class UploadErrorDraft {

    private final Integer csvRowNumber;
    private final String columnName;
    private final String rawValue;
    private final String errorCode;
    private final String errorMessage;

    public UploadErrorDraft(
            Integer csvRowNumber,
            String columnName,
            String rawValue,
            String errorCode,
            String errorMessage
    ) {
        this.csvRowNumber = csvRowNumber;
        this.columnName = columnName;
        this.rawValue = rawValue;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getCsvRowNumber() {
        return csvRowNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getRawValue() {
        return rawValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
