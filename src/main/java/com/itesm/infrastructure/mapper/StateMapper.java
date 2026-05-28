package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.state.State;
import com.itesm.infrastructure.persistence.entity.StateEntity;

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
