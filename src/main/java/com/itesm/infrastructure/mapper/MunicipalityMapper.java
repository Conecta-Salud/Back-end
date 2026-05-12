package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.municipality.Municipality;
import com.itesm.infrastructure.persistence.entity.MunicipalityEntity;

public class MunicipalityMapper {

    private MunicipalityMapper() {}

    public static Municipality toDomain(MunicipalityEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Municipality(
                entity.getId(),
                entity.getState().getId(),
                entity.getName(),
                entity.getInegiCode(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
