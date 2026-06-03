package com.itesm.application.service.upload;

import com.itesm.domain.models.upload.CsvFileRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CsvSchemaRegistry {

    private final Map<CsvFileRole, List<String>> requiredHeaders = new EnumMap<>(CsvFileRole.class);

    public CsvSchemaRegistry() {
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
                "clues", "institution_name", "state_code", "state_name", "municipality_code",
                "municipality_name", "unit_name", "establishment_type", "medical_unit_type",
                "care_level", "operation_status", "locality_name", "address", "latitude", "longitude"
        ));
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
}
