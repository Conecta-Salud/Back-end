package com.itesm.application.service.upload.sectorial;

import jakarta.enterprise.context.ApplicationScoped;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class HealthSectorialCsvAdapter {

    public static final Map<String, String> SPECIALTY_HEADERS_BY_CODE = Map.ofEntries(
            Map.entry("medicos_generales", "medicos generales"),
            Map.entry("pediatras", "pediatras"),
            Map.entry("ginecoobstetras", "ginecoobstetras"),
            Map.entry("cirujanos", "medicos cirujanos"),
            Map.entry("internistas", "medicos internistas"),
            Map.entry("oftalmologos", "medicos oftalmologos"),
            Map.entry("traumatologos", "medicos traumatologos"),
            Map.entry("dermatologos", "medicos dermatologos"),
            Map.entry("anestesiologos", "medicos anestesiologos"),
            Map.entry("odontologos", "odontologos"),
            Map.entry("cardiologos", "medicos cardiologos"),
            Map.entry("urgenciologos", "medicos urgenciologos"),
            Map.entry("geriatras", "medicos geriatras")
    );

    public HealthSectorialColumns detectColumns(List<String> headers) {
        Map<String, Integer> specialtyIndexes = new LinkedHashMap<>();
        SPECIALTY_HEADERS_BY_CODE.forEach((code, header) -> {
            int index = findIndex(headers, header, false);
            if (index >= 0) {
                specialtyIndexes.put(code, index);
            }
        });

        return new HealthSectorialColumns(
                findIndex(headers, "ano", false),
                findIndex(headers, "clues", true),
                findIndex(headers, "institucion", false),
                findIndex(headers, "clave estado", false),
                findIndex(headers, "nombre estado", false),
                findIndex(headers, "clave municipio", false),
                findIndex(headers, "nombre municipio", false),
                findIndex(headers, "nombre de la unidad", false),
                findIndex(headers, "tipo de establecimiento", false),
                findIndex(headers, "tipologia", false),
                findIndex(headers, "total de consultorios", false),
                findIndex(headers, "total camas area hospitalizacion", false),
                findIndex(headers, "total medicos generales especialistas", false),
                findIndex(headers, "total enfermeras en contacto", false),
                specialtyIndexes
        );
    }

    public List<String> missingHeaders(HealthSectorialColumns columns) {
        List<String> missing = new ArrayList<>();

        if (columns.yearIndex() < 0) missing.add("ANO");
        if (columns.cluesIndex() < 0) missing.add("CLUES");
        if (columns.institutionNameIndex() < 0) missing.add("Institucion");
        if (columns.stateCodeIndex() < 0) missing.add("Clave Estado");
        if (columns.stateNameIndex() < 0) missing.add("Nombre Estado");
        if (columns.municipalityCodeIndex() < 0) missing.add("Clave Municipio");
        if (columns.municipalityNameIndex() < 0) missing.add("Nombre Municipio");
        if (columns.unitNameIndex() < 0) missing.add("Nombre de la Unidad");
        if (columns.establishmentTypeIndex() < 0) missing.add("Tipo de Establecimiento");
        if (columns.medicalUnitTypeIndex() < 0) missing.add("Tipologia");
        if (columns.totalConsultingRoomsIndex() < 0) missing.add("TOTAL DE CONSULTORIOS");
        if (columns.totalHospitalBedsIndex() < 0) missing.add("TOTAL CAMAS AREA HOSPITALIZACION");
        if (columns.totalDoctorsIndex() < 0) missing.add("Total medicos generales especialistas y odontologos");
        if (columns.totalNursesIndex() < 0) missing.add("Total enfermeras en contacto con el paciente");

        return missing;
    }

    public HealthSectorialCsvRow toRow(int csvRowNumber, List<String> values, HealthSectorialColumns columns) {
        Map<String, String> specialtyValues = new LinkedHashMap<>();
        columns.specialtyIndexes().forEach((code, index) -> specialtyValues.put(code, valueAt(values, index)));

        return new HealthSectorialCsvRow(
                csvRowNumber,
                valueAt(values, columns.yearIndex()),
                valueAt(values, columns.cluesIndex()),
                valueAt(values, columns.institutionNameIndex()),
                valueAt(values, columns.stateCodeIndex()),
                valueAt(values, columns.stateNameIndex()),
                valueAt(values, columns.municipalityCodeIndex()),
                valueAt(values, columns.municipalityNameIndex()),
                valueAt(values, columns.unitNameIndex()),
                valueAt(values, columns.establishmentTypeIndex()),
                valueAt(values, columns.medicalUnitTypeIndex()),
                valueAt(values, columns.totalConsultingRoomsIndex()),
                valueAt(values, columns.totalHospitalBedsIndex()),
                valueAt(values, columns.totalDoctorsIndex()),
                valueAt(values, columns.totalNursesIndex()),
                specialtyValues
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

    private int findIndex(List<String> headers, String fragment, boolean exactPreferred) {
        String normalizedFragment = normalize(fragment);

        for (int i = 0; i < headers.size(); i++) {
            if (normalize(headers.get(i)).equals(normalizedFragment)) {
                return i;
            }
        }

        if (exactPreferred) {
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

    public record HealthSectorialColumns(
            int yearIndex,
            int cluesIndex,
            int institutionNameIndex,
            int stateCodeIndex,
            int stateNameIndex,
            int municipalityCodeIndex,
            int municipalityNameIndex,
            int unitNameIndex,
            int establishmentTypeIndex,
            int medicalUnitTypeIndex,
            int totalConsultingRoomsIndex,
            int totalHospitalBedsIndex,
            int totalDoctorsIndex,
            int totalNursesIndex,
            Map<String, Integer> specialtyIndexes
    ) {
    }
}
