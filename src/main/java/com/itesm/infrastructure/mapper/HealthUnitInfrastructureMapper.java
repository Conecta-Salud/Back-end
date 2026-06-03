package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.healthunit.HealthUnitInfrastructure;
import com.itesm.infrastructure.persistence.entity.HealthUnitInfrastructureEntity;

public class HealthUnitInfrastructureMapper {

    private HealthUnitInfrastructureMapper() {}

    public static HealthUnitInfrastructure toDomain(HealthUnitInfrastructureEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HealthUnitInfrastructure(
                entity.getId(),
                entity.getHealthUnit() != null ? entity.getHealthUnit().getId() : null,
                entity.getPeriod() != null ? entity.getPeriod().getId() : null,
                entity.getDataSource() != null ? entity.getDataSource().getId() : null,
                entity.getSourceFile()
        );
    }

    public static HealthUnitInfrastructureEntity toEntity(HealthUnitInfrastructure domain) {
        if (domain == null) {
            return null;
        }
        HealthUnitInfrastructureEntity entity = new HealthUnitInfrastructureEntity();
        entity.setId(domain.getId());
        entity.setSourceFile(domain.getSourceFile());
        return entity;
    }
}
