package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.healthunit.HealthUnitInfrastructureSummary;
import com.itesm.domain.models.healthunit.HealthUnitStaffSummary;
import com.itesm.domain.models.healthunit.HealthUnitDetail;
import com.itesm.domain.models.healthunit.HealthUnitSummary;
import com.itesm.infrastructure.persistence.entity.HealthUnitEntity;

public class HealthUnitMapper {

    private HealthUnitMapper() {}

    public static HealthUnitSummary toSummary(HealthUnitEntity entity) {
        if (entity == null) {
            return null;
        }

        return new HealthUnitSummary(
                entity.getId(),
                entity.getClues(),
                entity.getName(),
                entity.getMunicipality().getId(),
                entity.getMunicipality().getName(),
                entity.getMunicipality().getState().getId(),
                entity.getMunicipality().getState().getName(),
                entity.getInstitution().getName(),
                entity.getEstablishmentType().getName(),
                entity.getMedicalUnitType().getName(),
                entity.getCareLevel()
        );
    }

    public static HealthUnitDetail toDetail(
            HealthUnitEntity entity,
            HealthUnitStaffSummary staff,
            HealthUnitInfrastructureSummary infrastructure
    ) {
        if (entity == null) {
            return null;
        }

        return new HealthUnitDetail(
                entity.getId(),
                entity.getClues(),
                entity.getName(),
                entity.getMunicipality().getId(),
                entity.getMunicipality().getName(),
                entity.getMunicipality().getState().getId(),
                entity.getMunicipality().getState().getName(),
                entity.getInstitution().getName(),
                entity.getEstablishmentType().getName(),
                entity.getMedicalUnitType().getName(),
                entity.getCareLevel(),
                staff,
                infrastructure
        );
    }
}
