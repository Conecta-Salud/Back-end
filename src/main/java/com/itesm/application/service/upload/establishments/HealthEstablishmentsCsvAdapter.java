package com.itesm.application.service.upload.establishments;

import jakarta.enterprise.context.ApplicationScoped;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class HealthEstablishmentsCsvAdapter {

    public HealthEstablishmentsColumns detectColumns(List<String> headers) {
        return new HealthEstablishmentsColumns(
                findIndex(headers, "clues"),
                findIndex(headers, "nombre de la institucion"),
                findIndex(headers, "clave de la entidad"),
                findIndex(headers, "entidad"),
                findIndex(headers, "clave del municipio"),
                findIndex(headers, "municipio"),
                findIndex(headers, "localidad"),
                findIndex(headers, "nombre tipo establecimiento"),
                findIndex(headers, "nombre de tipologia"),
                findIndex(headers, "nombre de la unidad"),
                findIndex(headers, "estatus de operacion"),
                findIndex(headers, "nivel atencion"),
                findIndex(headers, "latitud"),
                findIndex(headers, "longitud")
        );
    }

    public List<String> missingHeaders(HealthEstablishmentsColumns columns) {
        List<String> missing = new ArrayList<>();

        if (columns.cluesIndex() < 0) {
            missing.add("CLUES");
        }
        if (columns.institutionNameIndex() < 0) {
            missing.add("NOMBRE DE LA INSTITUCION");
        }
        if (columns.stateCodeIndex() < 0) {
            missing.add("CLAVE DE LA ENTIDAD");
        }
        if (columns.stateNameIndex() < 0) {
            missing.add("ENTIDAD");
        }
        if (columns.municipalityCodeIndex() < 0) {
            missing.add("CLAVE DEL MUNICIPIO");
        }
        if (columns.municipalityNameIndex() < 0) {
            missing.add("MUNICIPIO");
        }
        if (columns.establishmentTypeIndex() < 0) {
            missing.add("NOMBRE TIPO ESTABLECIMIENTO");
        }
        if (columns.medicalUnitTypeIndex() < 0) {
            missing.add("NOMBRE DE TIPOLOGIA");
        }
        if (columns.unitNameIndex() < 0) {
            missing.add("NOMBRE DE LA UNIDAD");
        }
        if (columns.operationStatusIndex() < 0) {
            missing.add("ESTATUS DE OPERACION");
        }
        if (columns.careLevelIndex() < 0) {
            missing.add("NIVEL ATENCION");
        }

        return missing;
    }

    public HealthEstablishmentCsvRow toRow(
            int csvRowNumber,
            List<String> values,
            HealthEstablishmentsColumns columns
    ) {
        return new HealthEstablishmentCsvRow(
                csvRowNumber,
                valueAt(values, columns.cluesIndex()),
                valueAt(values, columns.institutionNameIndex()),
                valueAt(values, columns.stateCodeIndex()),
                valueAt(values, columns.stateNameIndex()),
                valueAt(values, columns.municipalityCodeIndex()),
                valueAt(values, columns.municipalityNameIndex()),
                valueAt(values, columns.localityNameIndex()),
                valueAt(values, columns.establishmentTypeIndex()),
                valueAt(values, columns.medicalUnitTypeIndex()),
                valueAt(values, columns.unitNameIndex()),
                valueAt(values, columns.operationStatusIndex()),
                valueAt(values, columns.careLevelIndex()),
                valueAt(values, columns.latitudeIndex()),
                valueAt(values, columns.longitudeIndex())
        );
    }

    public List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        int i = 0;
        while (i < line.length()) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i += 2;
                } else {
                    inQuotes = !inQuotes;
                    i++;
                }
            } else if (currentChar == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
                i++;
            } else {
                current.append(currentChar);
                i++;
            }
        }

        values.add(current.toString());
        return values;
    }

    public boolean isBlankRow(List<String> values) {
        return values == null || values.stream().allMatch(value -> value == null || value.isBlank());
    }

    public String normalize(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if (!trimmed.isEmpty() && trimmed.charAt(0) == '\ufeff') {
            trimmed = trimmed.substring(1);
        }

        String withoutDiacritics = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return withoutDiacritics
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int findIndex(List<String> headers, String fragment) {
        String normalizedFragment = normalize(fragment);

        for (int i = 0; i < headers.size(); i++) {
            if (normalize(headers.get(i)).equals(normalizedFragment)) {
                return i;
            }
        }

        if ("entidad".equals(normalizedFragment) || "municipio".equals(normalizedFragment)) {
            return -1;
        }

        for (int i = 0; i < headers.size(); i++) {
            if (normalize(headers.get(i)).contains(normalizedFragment)) {
                return i;
            }
        }

        return -1;
    }

    private String valueAt(List<String> values, int index) {
        if (index < 0 || index >= values.size()) {
            return null;
        }

        String value = values.get(index);
        return value == null ? null : value.trim();
    }

    public record HealthEstablishmentsColumns(
            int cluesIndex,
            int institutionNameIndex,
            int stateCodeIndex,
            int stateNameIndex,
            int municipalityCodeIndex,
            int municipalityNameIndex,
            int localityNameIndex,
            int establishmentTypeIndex,
            int medicalUnitTypeIndex,
            int unitNameIndex,
            int operationStatusIndex,
            int careLevelIndex,
            int latitudeIndex,
            int longitudeIndex
    ) {
    }
}
