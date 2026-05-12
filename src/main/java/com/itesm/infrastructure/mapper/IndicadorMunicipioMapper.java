package com.itesm.infrastructure.mapper;


import com.itesm.domain.models.indicador.IndicadorMunicipio;
import com.itesm.infrastructure.persistence.entity.IndicadorMunicipioEntity;

public class IndicadorMunicipioMapper {

    public static IndicadorMunicipio toDomain(IndicadorMunicipioEntity indicadorMunicipioEntity) {
        return new  IndicadorMunicipio(
                indicadorMunicipioEntity.getId(),

                indicadorMunicipioEntity.getMunicipio().getId(),
                indicadorMunicipioEntity.getMunicipio().getNombre(),
                indicadorMunicipioEntity.getMunicipio().getEstado().getId(),
                indicadorMunicipioEntity.getMunicipio().getEstado().getNombre(),
                indicadorMunicipioEntity.getPeriodo().getId(),
                indicadorMunicipioEntity.getPeriodo().getAnio(),

                indicadorMunicipioEntity.getPoblacionTotal(),
                indicadorMunicipioEntity.getPorcentaje60mas(),
                indicadorMunicipioEntity.getCarenciaAccesoSalud(),
                indicadorMunicipioEntity.getSituacionPobrezaTotal()
        );
    }
}
