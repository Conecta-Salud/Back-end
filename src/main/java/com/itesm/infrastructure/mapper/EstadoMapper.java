package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.estado.Estado;
import com.itesm.domain.models.periodo.Periodo;
import com.itesm.infrastructure.persistence.entity.EstadoEntity;

public class EstadoMapper {

    public static Estado toDomain(EstadoEntity entity) {
        return new Estado(
                entity.getId(),
                entity.getNombre(),
                entity.getClaveInegi(),
                entity.getLat(),
                entity.getLng()
        );
    }
}
