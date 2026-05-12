package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.unidadSalud.InfraestructuraUnidadResumen;
import com.itesm.domain.models.unidadSalud.PersonalUnidadResumen;
import com.itesm.domain.models.unidadSalud.UnidadSaludDetalle;
import com.itesm.domain.models.unidadSalud.UnidadSaludResumen;
import com.itesm.infrastructure.persistence.entity.UnidadSaludEntity;

public class UnidadSaludMapper {

    public static UnidadSaludResumen toResumenDomain(
            UnidadSaludEntity entity
    ) {

        return new UnidadSaludResumen(
                entity.getId(),
                entity.getClues(),
                entity.getNombre(),
                entity.getMunicipio().getId(),
                entity.getMunicipio().getNombre(),
                entity.getMunicipio().getEstado().getId(),
                entity.getMunicipio().getEstado().getNombre(),
                entity.getInstitucion().getNombre(),
                entity.getTipoEstablecimiento().getNombre(),
                entity.getTipoTipologia().getNombre(),
                entity.getNivelAtencion()
        );
    }

    public static UnidadSaludDetalle toDetalleDomain(
            UnidadSaludEntity entity,
            PersonalUnidadResumen personal,
            InfraestructuraUnidadResumen infraestructura
    ) {

        return new UnidadSaludDetalle(
                entity.getId(),
                entity.getClues(),
                entity.getNombre(),
                entity.getMunicipio().getId(),
                entity.getMunicipio().getNombre(),
                entity.getMunicipio().getEstado().getId(),
                entity.getMunicipio().getEstado().getNombre(),
                entity.getInstitucion().getNombre(),
                entity.getTipoEstablecimiento().getNombre(),
                entity.getTipoTipologia().getNombre(),
                entity.getNivelAtencion(),
                personal,
                infraestructura
        );
    }
}
