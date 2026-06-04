package com.itesm.application.dto.admin.uploads;

public class CreateUploadBatchRequest {

    private String sourceType;
    private String dataSourceCode;
    private Integer sourceYear;
    private Integer analysisYear;
    private Integer expectedFiles;
    private String batchVersion;
    private String processingMode;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public Integer getSourceYear() {
        return sourceYear;
    }

    public void setSourceYear(Integer sourceYear) {
        this.sourceYear = sourceYear;
    }

    public Integer getAnalysisYear() {
        return analysisYear;
    }

    public void setAnalysisYear(Integer analysisYear) {
        this.analysisYear = analysisYear;
    }

    public Integer getExpectedFiles() {
        return expectedFiles;
    }

    public void setExpectedFiles(Integer expectedFiles) {
        this.expectedFiles = expectedFiles;
    }

    public String getBatchVersion() {
        return batchVersion;
    }

    public void setBatchVersion(String batchVersion) {
        this.batchVersion = batchVersion;
    }

    public String getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(String processingMode) {
        this.processingMode = processingMode;
    }
}
