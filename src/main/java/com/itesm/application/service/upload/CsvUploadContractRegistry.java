package com.itesm.application.service.upload;

import com.itesm.domain.models.upload.CsvFileRole;
import com.itesm.domain.models.upload.UploadSourceType;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class CsvUploadContractRegistry {

    private final Map<UploadSourceType, Set<CsvFileRole>> allowedRoles = new EnumMap<>(UploadSourceType.class);

    public CsvUploadContractRegistry() {
        allowedRoles.put(UploadSourceType.population, EnumSet.of(
                CsvFileRole.population_total,
                CsvFileRole.percentage_over_60,
                CsvFileRole.healthcare_access_deficiency,
                CsvFileRole.total_poverty_population
        ));
        allowedRoles.put(UploadSourceType.health_establishments, EnumSet.of(
                CsvFileRole.establishments_catalog
        ));
        allowedRoles.put(UploadSourceType.health_sectorial, EnumSet.of(
                CsvFileRole.sectorial_data
        ));
    }

    public boolean isFileRoleAllowed(UploadSourceType sourceType, CsvFileRole fileRole) {
        return sourceType != null
                && fileRole != null
                && getAllowedRoles(sourceType).contains(fileRole);
    }

    public Set<CsvFileRole> getAllowedRoles(UploadSourceType sourceType) {
        Set<CsvFileRole> roles = allowedRoles.get(sourceType);
        return roles == null ? Set.of() : Set.copyOf(roles);
    }
}
