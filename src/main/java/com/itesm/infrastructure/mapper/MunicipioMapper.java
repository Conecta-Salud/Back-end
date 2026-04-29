package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.municipio.Municipio;
import com.itesm.infrastructure.persistence.entity.MunicipioEntity;

public class MunicipioMapper {

    public static Municipio toDomain(MunicipioEntity entity) {
        return new Municipio(
                entity.getId(),
                entity.getEstado().getId(),
                entity.getNombre(),
                entity.getClaveInegi(),
                entity.getLat(),
                entity.getLng()
        );
    }
}
