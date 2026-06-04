package com.itesm.application.service.upload;

import com.itesm.domain.models.upload.UploadStatus;

public record CsvProcessingResult(
        UploadStatus status,
        String message
) {
}
