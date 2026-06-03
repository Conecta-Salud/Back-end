package com.itesm.application.service.upload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CsvStorageService {

    private static final int BUFFER_SIZE = 8192;

    @ConfigProperty(name = "conectasalud.upload.storage-dir", defaultValue = "uploads")
    String storageDir;

    @ConfigProperty(name = "conectasalud.upload.max-file-size-bytes", defaultValue = "52428800")
    Long maxFileSizeBytes;

    public StoredCsvFile store(
            Integer batchId,
            String originalFileName,
            String mimeType,
            InputStream inputStream
    ) {
        String safeOriginalName = sanitizeOriginalFileName(originalFileName);
        validateCsvExtension(safeOriginalName);

        Path root = storageRoot();
        Path batchDirectory = root.resolve("batches").resolve(String.valueOf(batchId)).normalize();

        if (!batchDirectory.startsWith(root)) {
            throw new BadRequestException("INVALID_FILE_NAME: Invalid upload target path");
        }

        try {
            Files.createDirectories(batchDirectory);
            String storedNameOnly = UUID.randomUUID() + "-" + safeOriginalName;
            Path target = batchDirectory.resolve(storedNameOnly).normalize();

            if (!target.startsWith(batchDirectory)) {
                throw new BadRequestException("INVALID_FILE_NAME: Invalid upload target path");
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long bytesWritten = writeWithLimit(inputStream, target, digest);
            String checksum = HexFormat.of().formatHex(digest.digest());
            String relativeStoredName = root.relativize(target).toString().replace('\\', '/');

            return new StoredCsvFile(
                    safeOriginalName,
                    relativeStoredName,
                    mimeType,
                    bytesWritten,
                    checksum,
                    target
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        } catch (IOException e) {
            throw new BadRequestException("UPLOAD_STORAGE_ERROR: Could not store uploaded CSV file");
        }
    }

    public Path resolveStoredPath(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            throw new BadRequestException("UNKNOWN_UPLOAD: Upload file path is missing");
        }

        Path root = storageRoot();
        Path resolved = root.resolve(storedFileName).normalize();

        if (!resolved.startsWith(root)) {
            throw new BadRequestException("INVALID_FILE_NAME: Stored file path is outside upload storage");
        }

        return resolved;
    }

    public void deleteQuietly(StoredCsvFile file) {
        if (file == null || file.getAbsolutePath() == null) {
            return;
        }

        try {
            Files.deleteIfExists(file.getAbsolutePath());
        } catch (IOException ignored) {
            // Best-effort cleanup only; upload registration remains the source of truth.
        }
    }

    public void deleteQuietly(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(resolveStoredPath(storedFileName));
        } catch (RuntimeException | IOException ignored) {
            // Best-effort cleanup only; caller must preserve the original failure.
        }
    }

    private long writeWithLimit(InputStream inputStream, Path target, MessageDigest digest) throws IOException {
        long totalBytes = 0L;
        byte[] buffer = new byte[BUFFER_SIZE];

        try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest);
             var output = Files.newOutputStream(target)) {
            int read;
            while ((read = digestInputStream.read(buffer)) != -1) {
                totalBytes += read;

                if (totalBytes > maxFileSizeBytes) {
                    Files.deleteIfExists(target);
                    throw new BadRequestException("FILE_TOO_LARGE: CSV file exceeds maximum allowed size");
                }

                output.write(buffer, 0, read);
            }
        }

        return totalBytes;
    }

    private Path storageRoot() {
        return Paths.get(storageDir).toAbsolutePath().normalize();
    }

    private String sanitizeOriginalFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: file name is required");
        }

        String fileName = Paths.get(originalFileName).getFileName().toString();
        String sanitized = fileName.replaceAll("[^A-Za-z0-9._-]", "_");

        if (sanitized.isBlank()) {
            throw new BadRequestException("INVALID_FILE_NAME: file name is invalid");
        }

        return sanitized;
    }

    private void validateCsvExtension(String fileName) {
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new BadRequestException("INVALID_FILE_TYPE: Only .csv files are accepted");
        }
    }
}
