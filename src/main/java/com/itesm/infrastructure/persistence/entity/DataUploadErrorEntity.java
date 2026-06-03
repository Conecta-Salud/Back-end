package com.itesm.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "data_upload_errors")
public class DataUploadErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "data_upload_id", nullable = false)
    private DataUploadEntity dataUpload;

    @Column(name = "csv_row_number")
    private Integer csvRowNumber;

    @Column(name = "column_name", length = 150)
    private String columnName;

    @Column(name = "raw_value", columnDefinition = "TEXT")
    private String rawValue;

    @Column(name = "error_code", nullable = false, length = 100)
    private String errorCode;

    @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataUploadEntity getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(DataUploadEntity dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Integer getCsvRowNumber() {
        return csvRowNumber;
    }

    public void setCsvRowNumber(Integer csvRowNumber) {
        this.csvRowNumber = csvRowNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}