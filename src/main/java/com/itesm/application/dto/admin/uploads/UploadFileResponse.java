package com.itesm.application.dto.admin.uploads;

public class UploadFileResponse {

    private final UploadFileSummaryResponse file;

    public UploadFileResponse(UploadFileSummaryResponse file) {
        this.file = file;
    }

    public UploadFileSummaryResponse getFile() {
        return file;
    }
}
