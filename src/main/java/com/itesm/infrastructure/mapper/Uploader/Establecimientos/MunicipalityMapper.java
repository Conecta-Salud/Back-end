package com.itesm.infrastructure.mapper.Uploader.Establecimientos;

import com.itesm.domain.models.Uploader.Establecimiento.Municipality;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;

public class MunicipalityMapper {

    private MunicipalityMapper() {}

    public static Municipality toDomain(MunicipalityEntity entity) {
        if (entity == null) {
            return null;
        }

        Municipality municipality = new Municipality(
                entity.getId(),
                entity.getName(),
                entity.getInegiCode()
        );
        if (entity.getState() != null) {
            municipality.setStateInegiCode(entity.getState().getInegiCode());
        }

        return municipality;
    }
}
