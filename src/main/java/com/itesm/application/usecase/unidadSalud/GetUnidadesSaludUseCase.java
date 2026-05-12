package com.itesm.application.usecase.unidadSalud;

import com.itesm.application.dto.unidadSalud.UnidadSaludResumenDto;
import com.itesm.domain.repository.UnidadSaludRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetUnidadesSaludUseCase {

    private final UnidadSaludRepository unidadSaludRepository;

    @Inject
    public GetUnidadesSaludUseCase(UnidadSaludRepository unidadSaludRepository) {
        this.unidadSaludRepository = unidadSaludRepository;
    }

    public List<UnidadSaludResumenDto> execute(Integer estadoId, Integer municipioId) {

        if (estadoId == null && municipioId == null) {
            throw new BadRequestException("Debes enviar estadoId o municipioId");
        }

        if (estadoId != null && municipioId != null) {
            throw new BadRequestException("Envía solo estadoId o municipioId, no ambos");
        }

        return (municipioId != null
                ? unidadSaludRepository.findResumenByMunicipioId(municipioId)
                : unidadSaludRepository.findResumenByEstadoId(estadoId)
        )
                .stream()
                .map(unidad -> new UnidadSaludResumenDto(
                        unidad.getId(),
                        unidad.getClues(),
                        unidad.getNombre(),
                        unidad.getIdMunicipio(),
                        unidad.getMunicipio(),
                        unidad.getIdEstado(),
                        unidad.getEstado(),
                        unidad.getInstitucion(),
                        unidad.getTipoEstablecimiento(),
                        unidad.getTipologia(),
                        unidad.getNivelAtencion()
                ))
                .collect(Collectors.toList());
    }


}
