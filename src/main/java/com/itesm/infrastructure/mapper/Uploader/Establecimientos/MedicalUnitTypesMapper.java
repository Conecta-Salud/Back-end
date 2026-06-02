package com.itesm.infrastructure.mapper.Uploader.Establecimientos;

import com.itesm.domain.models.Uploader.Establecimiento.MedicalUnitTypes;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MedicalUnitTypeEntity;

public class MedicalUnitTypesMapper {
    private MedicalUnitTypesMapper() {}

    public static MedicalUnitTypes toDomain(MedicalUnitTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MedicalUnitTypes(
                entity.getId(),
                entity.getName()
        );
    }
}
