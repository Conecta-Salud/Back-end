package com.itesm.application.service.upload.population;

import jakarta.enterprise.context.ApplicationScoped;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class PopulationIndicatorsCsvAdapter {

    public PopulationIndicatorsColumns detectColumns(List<String> headers) {
        return new PopulationIndicatorsColumns(
                findIndex(headers, "periodos"),
                findIndex(headers, "area geografica"),
                findIndex(headers, "poblacion total"),
                findIndex(headers, "porcentaje de poblacion de 60"),
                findIndex(headers, "carencia por acceso"),
                findIndex(headers, "poblacion en situacion de pobreza")
        );
    }

    public List<String> missingHeaders(PopulationIndicatorsColumns columns) {
        List<String> missing = new ArrayList<>();

        if (columns.periodIndex() < 0) {
            missing.add("Periodos");
        }
        if (columns.geographicAreaIndex() < 0) {
            missing.add("Área geográfica");
        }
        if (columns.totalPopulationIndex() < 0) {
            missing.add("Población total");
        }
        if (columns.percentageOver60Index() < 0) {
            missing.add("Porcentaje de población de 60");
        }
        if (columns.healthcareAccessDeficiencyIndex() < 0) {
            missing.add("Carencia por acceso");
        }
        if (columns.totalPovertyPopulationIndex() < 0) {
            missing.add("Población en situación de pobreza");
        }

        return missing;
    }

    public PopulationIndicatorsCsvRow toRow(int csvRowNumber, List<String> values, PopulationIndicatorsColumns columns) {
        return new PopulationIndicatorsCsvRow(
                csvRowNumber,
                valueAt(values, columns.periodIndex()),
                valueAt(values, columns.geographicAreaIndex()),
                valueAt(values, columns.totalPopulationIndex()),
                valueAt(values, columns.percentageOver60Index()),
                valueAt(values, columns.healthcareAccessDeficiencyIndex()),
                valueAt(values, columns.totalPovertyPopulationIndex())
        );
    }

    public List<String> parseCsvLine(String line) {
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

    public record PopulationIndicatorsColumns(
            int periodIndex,
            int geographicAreaIndex,
            int totalPopulationIndex,
            int percentageOver60Index,
            int healthcareAccessDeficiencyIndex,
            int totalPovertyPopulationIndex
    ) {
    }
}
