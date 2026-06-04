package com.itesm.application.dto.admin.uploads;

public class ProcessUploadBatchRequest {

    private String mode;
    private Boolean replaceExistingForYear;
    private Boolean failOnErrors;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Boolean getReplaceExistingForYear() {
        return replaceExistingForYear;
    }

    public void setReplaceExistingForYear(Boolean replaceExistingForYear) {
        this.replaceExistingForYear = replaceExistingForYear;
    }

    public Boolean getFailOnErrors() {
        return failOnErrors;
    }

    public void setFailOnErrors(Boolean failOnErrors) {
        this.failOnErrors = failOnErrors;
    }
}
