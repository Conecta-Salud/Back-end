package com.itesm.application.dto.admin.uploads;

import java.util.List;

public class UploadBatchDetailResponse {

    private final UploadBatchResponse batch;
    private final List<UploadFileSummaryResponse> files;

    public UploadBatchDetailResponse(
            UploadBatchResponse batch,
            List<UploadFileSummaryResponse> files
    ) {
        this.batch = batch;
        this.files = files;
    }

    public UploadBatchResponse getBatch() {
        return batch;
    }

    public List<UploadFileSummaryResponse> getFiles() {
        return files;
    }
}
