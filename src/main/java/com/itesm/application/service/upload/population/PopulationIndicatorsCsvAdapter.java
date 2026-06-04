package com.itesm.application.service.upload.population;

import com.itesm.domain.models.upload.CsvFileRole;
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
        return missingHeaders(columns, CsvFileRole.population_indicators);
    }

    public List<String> missingHeaders(PopulationIndicatorsColumns columns, CsvFileRole fileRole) {
        List<String> missing = new ArrayList<>();

        if (columns.periodIndex() < 0) {
            missing.add("Periodos");
        }
        if (columns.geographicAreaIndex() < 0) {
            missing.add("Area geografica");
        }
        if (columns.totalPopulationIndex() < 0) {
            missing.add("Poblacion total");
        }
        if (columns.percentageOver60Index() < 0) {
            missing.add("Porcentaje de poblacion de 60");
        }

        if (requiresCountryStateIndicators(fileRole)) {
            if (columns.healthcareAccessDeficiencyIndex() < 0) {
                missing.add("Carencia por acceso");
            }
            if (columns.totalPovertyPopulationIndex() < 0) {
                missing.add("Poblacion en situacion de pobreza");
            }
        }

        return missing;
    }

    public PopulationIndicatorsCsvRow toRow(int csvRowNumber, List<String> values, PopulationIndicatorsColumns columns) {
        return toRow(csvRowNumber, values, columns, CsvFileRole.population_indicators);
    }

    public PopulationIndicatorsCsvRow toRow(
            int csvRowNumber,
            List<String> values,
            PopulationIndicatorsColumns columns,
            CsvFileRole fileRole
    ) {
        List<String> normalizedValues = normalizeValues(values, fileRole);

        return new PopulationIndicatorsCsvRow(
                csvRowNumber,
                valueAt(normalizedValues, columns.periodIndex()),
                valueAt(normalizedValues, columns.geographicAreaIndex()),
                valueAt(normalizedValues, columns.totalPopulationIndex()),
                valueAt(normalizedValues, columns.percentageOver60Index()),
                valueAt(normalizedValues, columns.healthcareAccessDeficiencyIndex()),
                valueAt(normalizedValues, columns.totalPovertyPopulationIndex())
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

    private List<String> normalizeValues(List<String> values, CsvFileRole fileRole) {
        if (fileRole != CsvFileRole.population_municipal_base || values == null || values.size() <= 4) {
            return values;
        }

        List<String> trimmedValues = new ArrayList<>(values);
        while (trimmedValues.size() > 4 && isBlank(trimmedValues.get(trimmedValues.size() - 1))) {
            trimmedValues.remove(trimmedValues.size() - 1);
        }

        if (trimmedValues.size() <= 4) {
            return trimmedValues;
        }

        int percentageIndex = findRightmostNumericIndex(trimmedValues, trimmedValues.size() - 1);
        if (percentageIndex < 0) {
            return values;
        }

        int totalPopulationIndex = findRightmostNumericIndex(trimmedValues, percentageIndex - 1);
        if (totalPopulationIndex <= 1) {
            return values;
        }

        String geographicArea = joinGeographicArea(trimmedValues, 1, totalPopulationIndex);
        if (geographicArea.isBlank()) {
            return values;
        }

        return List.of(
                trimmedValues.get(0),
                geographicArea,
                trimmedValues.get(totalPopulationIndex),
                trimmedValues.get(percentageIndex)
        );
    }

    private int findRightmostNumericIndex(List<String> values, int startIndex) {
        for (int i = startIndex; i >= 0; i--) {
            if (isNumeric(values.get(i))) {
                return i;
            }
        }

        return -1;
    }

    private String joinGeographicArea(List<String> values, int startInclusive, int endExclusive) {
        List<String> areaParts = new ArrayList<>();

        for (int i = startInclusive; i < endExclusive; i++) {
            String value = values.get(i);
            if (value != null && !value.isBlank()) {
                areaParts.add(value.trim());
            }
        }

        return String.join(", ", areaParts);
    }

    private boolean isNumeric(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            new java.math.BigDecimal(value.trim().replace(",", ""));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean requiresCountryStateIndicators(CsvFileRole fileRole) {
        return fileRole == CsvFileRole.population_indicators
                || fileRole == CsvFileRole.population_state_national_indicators;
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
