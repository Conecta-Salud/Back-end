package com.itesm.application.service.upload;

import java.nio.file.Path;

public class StoredCsvFile {

    private final String originalFileName;
    private final String storedFileName;
    private final String mimeType;
    private final Long fileSize;
    private final String checksum;
    private final Path absolutePath;

    public StoredCsvFile(
            String originalFileName,
            String storedFileName,
            String mimeType,
            Long fileSize,
            String checksum,
            Path absolutePath
    ) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.checksum = checksum;
        this.absolutePath = absolutePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getChecksum() {
        return checksum;
    }

    public Path getAbsolutePath() {
        return absolutePath;
    }
}
