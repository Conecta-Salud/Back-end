package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.indicador.IndicadorEstado;
import com.itesm.infrastructure.persistence.entity.IndicadorEstadoEntity;

public class IndicadorEstadoMapper {

    public static IndicadorEstado toDomain(IndicadorEstadoEntity indicadorEstadoEntity) {
        return new IndicadorEstado(
                indicadorEstadoEntity.getId(),

                indicadorEstadoEntity.getEstado().getId(),
                indicadorEstadoEntity.getEstado().getNombre(),
                indicadorEstadoEntity.getPeriodo().getId(),
                indicadorEstadoEntity.getPeriodo().getAnio(),

                indicadorEstadoEntity.getPoblacionTotal(),
                indicadorEstadoEntity.getPorcentaje60mas(),
                indicadorEstadoEntity.getCarenciaAccesoSalud(),
                indicadorEstadoEntity.getSituacionPobrezaTotal()
        );
    }
}
