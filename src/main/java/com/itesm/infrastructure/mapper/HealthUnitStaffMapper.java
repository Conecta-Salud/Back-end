package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.healthunit.HealthUnitStaff;
import com.itesm.infrastructure.persistence.entity.HealthUnitStaffEntity;

public class HealthUnitStaffMapper {

    private HealthUnitStaffMapper() {}

    public static HealthUnitStaff toDomain(HealthUnitStaffEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HealthUnitStaff(
                entity.getId(),
                entity.getHealthUnit() != null ? entity.getHealthUnit().getId() : null,
                entity.getPeriod() != null ? entity.getPeriod().getId() : null,
                entity.getTotalDoctors(),
                entity.getTotalNurses(),
                entity.getDataSource() != null ? entity.getDataSource().getId() : null,
                entity.getSourceFile()
        );
    }

    public static HealthUnitStaffEntity toEntity(HealthUnitStaff domain) {
        if (domain == null) {
            return null;
        }
        HealthUnitStaffEntity entity = new HealthUnitStaffEntity();
        entity.setId(domain.getId());
        entity.setTotalDoctors(domain.getTotalDoctors());
        entity.setTotalNurses(domain.getTotalNurses());
        entity.setSourceFile(domain.getSourceFile());
        return entity;
    }
}
