package com.itesm.domain.models.upload;

public record UploadErrorRow(
        Long id,
        Integer uploadId,
        String originalFileName,
        Integer csvRowNumber,
        String columnName,
        String rawValue,
        String errorCode,
        String errorMessage
) {
}
