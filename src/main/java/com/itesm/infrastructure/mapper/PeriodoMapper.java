package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.periodo.Periodo;
import com.itesm.infrastructure.persistence.entity.PeriodoEntity;

public class PeriodoMapper {

    public static Periodo toDomain(PeriodoEntity entity) {
        return new Periodo(
                entity.getId(),
                entity.getAnio(),
                entity.getEstatus()
        );
    }
}
