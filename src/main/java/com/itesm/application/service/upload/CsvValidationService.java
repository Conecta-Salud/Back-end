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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CsvValidationService {

    // Validacion ligera previa al procesamiento: revisa lectura del archivo y
    // encabezados requeridos, sin ejecutar reglas completas de negocio.
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
                .orElseThrow(() -> new NotFoundException("UNKNOWN_UPLOAD: archivo de carga no encontrado"));

        return validate(upload);
    }

    public ValidateUploadResponse validate(DataUploadEntity upload) {
        if (upload == null) {
            throw new NotFoundException("UNKNOWN_UPLOAD: archivo de carga no encontrado");
        }

        ValidationScan scan = scan(upload);
        UploadStatus status = scan.errors().isEmpty()
                ? UploadStatus.completed
                : UploadStatus.warning;

        Integer uploadId = upload.getId();
        dataUploadErrorRepository.replaceErrors(uploadId, scan.errors());
        dataUploadRepository.updateValidationResult(
                uploadId,
                status.name(),
                scan.totalRecords(),
                scan.validRecords(),
                scan.errors().size(),
                scan.errors().isEmpty() ? null : validationSummary(scan.errors().size())
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
        if (upload.getFileRole() != null && csvSchemaRegistry.charsets(upload.getFileRole()).size() > 1) {
            return scanWithCharsetFallback(upload);
        }

        return scan(upload, csvSchemaRegistry.charset(upload.getFileRole()));
    }

    private ValidationScan scanWithCharsetFallback(DataUploadEntity upload) {
        ValidationScan bestScan = null;

        // Algunos CSV oficiales pueden variar de encoding; se intenta cada charset
        // permitido por fileRole y se conserva el resultado con menos errores.
        for (Charset charset : csvSchemaRegistry.charsets(upload.getFileRole())) {
            ValidationScan scan = scan(upload, charset);

            if (!hasHeaderOrStorageErrors(scan)) {
                return scan;
            }

            if (bestScan == null || scan.errors().size() < bestScan.errors().size()) {
                bestScan = scan;
            }
        }

        return bestScan == null ? new ValidationScan(0, 0, List.of()) : bestScan;
    }

    private ValidationScan scan(DataUploadEntity upload, Charset charset) {
        Path path = csvStorageService.resolveStoredPath(upload.getStoredFileName());
        List<UploadErrorDraft> errors = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                errors.add(new UploadErrorDraft(
                        1,
                        null,
                        null,
                        "EMPTY_FILE",
                        "El archivo CSV está vacío o no contiene fila de encabezados."
                ));
                return new ValidationScan(0, 0, errors);
            }

            for (String missingHeader : csvSchemaRegistry.missingRequiredHeaders(upload.getFileRole(), parseCsvLine(headerLine))) {
                errors.add(new UploadErrorDraft(
                        1,
                        missingHeader,
                        null,
                        "MISSING_REQUIRED_HEADER",
                        "Falta el encabezado requerido del CSV: " + missingHeader
                ));
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
                    "No fue posible leer el archivo CSV almacenado."
            ));
            return new ValidationScan(0, 0, errors);
        }
    }

    private String validationSummary(int errorCount) {
        return "La validación CSV encontró " + errorCount + " error(es).";
    }

    private boolean hasHeaderOrStorageErrors(ValidationScan scan) {
        return scan.errors().stream()
                .anyMatch(error -> "MISSING_REQUIRED_HEADER".equals(error.getErrorCode())
                        || "UPLOAD_STORAGE_ERROR".equals(error.getErrorCode()));
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        // Parser minimo para encabezados CSV: respeta comillas y comillas escapadas
        // sin introducir una dependencia adicional solo para esta validacion.
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
