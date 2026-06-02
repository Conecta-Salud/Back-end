package com.itesm.infrastructure.mapper.Uploader.Establecimientos;

import com.itesm.domain.models.Uploader.Establecimiento.Establishment;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.EstablishmentEntity;

public class EstablishmentMapper {

    private EstablishmentMapper() {}

    public static Establishment toDomain(EstablishmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Establishment(
                entity.getId(),
                entity.getName()
        );
    }
}