package com.itesm.application.service.upload;

import com.itesm.application.dto.admin.uploads.ValidateUploadResponse;
import com.itesm.domain.models.upload.UploadErrorDraft;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadErrorRepository;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.domain.repository.UploadBatchRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@ApplicationScoped
public class CsvValidationService {

    private final DataUploadRepository dataUploadRepository;
    private final DataUploadErrorRepository dataUploadErrorRepository;
    private final UploadBatchRepository uploadBatchRepository;
    private final CsvStorageService csvStorageService;
    private final CsvSchemaRegistry csvSchemaRegistry;

    public CsvValidationService(
            DataUploadRepository dataUploadRepository,
            DataUploadErrorRepository dataUploadErrorRepository,
            UploadBatchRepository uploadBatchRepository,
            CsvStorageService csvStorageService,
            CsvSchemaRegistry csvSchemaRegistry
    ) {
        this.dataUploadRepository = dataUploadRepository;
        this.dataUploadErrorRepository = dataUploadErrorRepository;
        this.uploadBatchRepository = uploadBatchRepository;
        this.csvStorageService = csvStorageService;
        this.csvSchemaRegistry = csvSchemaRegistry;
    }

    public ValidateUploadResponse validate(Integer uploadId) {
        DataUploadEntity upload = dataUploadRepository.findById(uploadId)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_UPLOAD: Upload not found"));

        ValidationScan scan = scan(upload);
        UploadStatus status = scan.errors().isEmpty()
                ? UploadStatus.completed
                : UploadStatus.warning;

        dataUploadErrorRepository.replaceErrors(uploadId, scan.errors());
        dataUploadRepository.updateValidationResult(
                uploadId,
                status.name(),
                scan.totalRecords(),
                scan.validRecords(),
                scan.errors().size(),
                scan.errors().isEmpty() ? null : "CSV validation found " + scan.errors().size() + " error(s)"
        );
        uploadBatchRepository.recalculateCounters(upload.getBatch().getId());

        return new ValidateUploadResponse(
                uploadId,
                status.name(),
                scan.totalRecords(),
                scan.validRecords(),
                scan.errors().size()
        );
    }

    private ValidationScan scan(DataUploadEntity upload) {
        Path path = csvStorageService.resolveStoredPath(upload.getStoredFileName());
        List<UploadErrorDraft> errors = new ArrayList<>();
        List<String> requiredHeaders = csvSchemaRegistry.requiredHeaders(upload.getFileRole());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                errors.add(new UploadErrorDraft(
                        1,
                        null,
                        null,
                        "EMPTY_FILE",
                        "CSV file is empty or has no header row"
                ));
                return new ValidationScan(0, 0, errors);
            }

            Set<String> presentHeaders = normalizeHeaders(parseCsvLine(headerLine));
            for (String requiredHeader : requiredHeaders) {
                if (!presentHeaders.contains(requiredHeader)) {
                    errors.add(new UploadErrorDraft(
                            1,
                            requiredHeader,
                            null,
                            "MISSING_REQUIRED_HEADER",
                            "Required CSV header is missing: " + requiredHeader
                    ));
                }
            }

            int totalRecords = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    totalRecords++;
                }
            }

            int validRecords = errors.isEmpty() ? totalRecords : 0;
            return new ValidationScan(totalRecords, validRecords, errors);
        } catch (IOException e) {
            errors.add(new UploadErrorDraft(
                    null,
                    null,
                    null,
                    "UPLOAD_STORAGE_ERROR",
                    "Could not read stored CSV file"
            ));
            return new ValidationScan(0, 0, errors);
        }
    }

    private Set<String> normalizeHeaders(List<String> headers) {
        Set<String> normalized = new LinkedHashSet<>();

        for (String header : headers) {
            String value = header == null ? "" : header.trim();

            if (!value.isEmpty() && value.charAt(0) == '\ufeff') {
                value = value.substring(1);
            }

            normalized.add(value.toLowerCase(Locale.ROOT));
        }

        return normalized;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (currentChar == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(currentChar);
        }

        values.add(current.toString());
        return values;
    }

    private record ValidationScan(
            Integer totalRecords,
            Integer validRecords,
            List<UploadErrorDraft> errors
    ) {
    }
}
