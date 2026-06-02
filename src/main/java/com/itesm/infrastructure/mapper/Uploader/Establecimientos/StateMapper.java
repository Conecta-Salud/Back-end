package com.itesm.infrastructure.mapper.Uploader.Establecimientos;

import com.itesm.domain.models.Uploader.Establecimiento.State;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;

public class StateMapper {

    private StateMapper() {}

    public static State toDomain(StateEntity entity) {
        if (entity == null) {
            return null;
        }

        return new State(
                entity.getId(),
                entity.getName(),
                entity.getInegiCode()
        );
    }
}
