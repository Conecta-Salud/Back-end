package com.itesm.application.service.upload;

import com.itesm.domain.models.upload.CsvFileRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class CsvSchemaRegistry {

    private final Map<CsvFileRole, List<String>> requiredHeaders = new EnumMap<>(CsvFileRole.class);
    private final Set<CsvFileRole> partialHeaderRoles = new LinkedHashSet<>();

    public CsvSchemaRegistry() {
        requiredHeaders.put(CsvFileRole.population_indicators, List.of(
                "periodos",
                "area geografica",
                "poblacion total",
                "porcentaje de poblacion de 60",
                "carencia por acceso",
                "poblacion en situacion de pobreza"
        ));
        partialHeaderRoles.add(CsvFileRole.population_indicators);

        requiredHeaders.put(CsvFileRole.population_municipal_base, List.of(
                "periodos",
                "area geografica",
                "poblacion total",
                "porcentaje de poblacion de 60"
        ));
        partialHeaderRoles.add(CsvFileRole.population_municipal_base);

        requiredHeaders.put(CsvFileRole.population_state_national_indicators, List.of(
                "periodos",
                "area geografica",
                "poblacion total",
                "porcentaje de poblacion de 60",
                "carencia por acceso",
                "poblacion en situacion de pobreza"
        ));
        partialHeaderRoles.add(CsvFileRole.population_state_national_indicators);

        requiredHeaders.put(CsvFileRole.population_total, List.of(
                "territory_level", "inegi_code", "state_inegi_code", "territory_name", "value"
        ));
        requiredHeaders.put(CsvFileRole.percentage_over_60, List.of(
                "territory_level", "inegi_code", "state_inegi_code", "territory_name", "value"
        ));
        requiredHeaders.put(CsvFileRole.healthcare_access_deficiency, List.of(
                "territory_level", "inegi_code", "territory_name", "value"
        ));
        requiredHeaders.put(CsvFileRole.total_poverty_population, List.of(
                "territory_level", "inegi_code", "territory_name", "value"
        ));
        requiredHeaders.put(CsvFileRole.establishments_catalog, List.of(
                "clues",
                "nombre de la institucion",
                "clave de la entidad",
                "entidad",
                "clave del municipio",
                "municipio",
                "nombre tipo establecimiento",
                "nombre de tipologia",
                "nombre de la unidad",
                "estatus de operacion",
                "nivel atencion"
        ));
        partialHeaderRoles.add(CsvFileRole.establishments_catalog);
        requiredHeaders.put(CsvFileRole.sectorial_data, List.of(
                "clues", "state_code", "state_name", "municipality_code", "municipality_name",
                "total_doctors", "total_nurses", "total_camas_hospitalizacion", "total_consultorios"
        ));
    }

    public List<String> requiredHeaders(CsvFileRole fileRole) {
        List<String> headers = requiredHeaders.get(fileRole);

        if (headers == null) {
            throw new BadRequestException("INVALID_FILE_ROLE: CSV file role is not supported");
        }

        return headers;
    }

    public List<String> missingRequiredHeaders(CsvFileRole fileRole, List<String> presentHeaders) {
        List<String> required = requiredHeaders(fileRole);
        List<String> normalizedPresent = presentHeaders.stream()
                .map(this::normalize)
                .filter(value -> !value.isBlank())
                .toList();

        List<String> missing = new ArrayList<>();

        for (String requiredHeader : required) {
            String normalizedRequired = normalize(requiredHeader);
            boolean present = hasHeader(fileRole, normalizedPresent, normalizedRequired);

            if (!present) {
                missing.add(requiredHeader);
            }
        }

        return missing;
    }

    public Charset charset(CsvFileRole fileRole) {
        return charsets(fileRole).get(0);
    }

    public List<Charset> charsets(CsvFileRole fileRole) {
        if (fileRole == CsvFileRole.population_indicators
                || fileRole == CsvFileRole.population_municipal_base
                || fileRole == CsvFileRole.population_state_national_indicators) {
            return List.of(StandardCharsets.UTF_16LE);
        }

        if (fileRole == CsvFileRole.establishments_catalog) {
            return List.of(StandardCharsets.UTF_8);
        }

        return List.of(StandardCharsets.UTF_8);
    }

    public boolean usesPartialHeaderMatch(CsvFileRole fileRole) {
        return partialHeaderRoles.contains(fileRole);
    }

    private boolean hasHeader(CsvFileRole fileRole, List<String> normalizedPresent, String normalizedRequired) {
        if (!usesPartialHeaderMatch(fileRole)) {
            return normalizedPresent.contains(normalizedRequired);
        }

        if (fileRole == CsvFileRole.establishments_catalog
                && ("entidad".equals(normalizedRequired) || "municipio".equals(normalizedRequired))) {
            return normalizedPresent.contains(normalizedRequired);
        }

        return normalizedPresent.stream().anyMatch(header -> header.contains(normalizedRequired));
    }

    private String normalize(String value) {
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
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }
}
