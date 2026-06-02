package com.itesm.infrastructure.mapper.Uploader.Establecimientos;

import com.itesm.domain.models.Uploader.Establecimiento.Institution;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.InstitutionEntity;

public class InstitutionMapper {
    private InstitutionMapper() {}

    public static Institution toDomain(InstitutionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Institution(
                entity.getId(),
                entity.getName()
        );
    }
}
