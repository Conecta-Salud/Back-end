package com.itesm.application.usecase.unidadSalud;

import com.itesm.application.dto.unidadSalud.*;
import com.itesm.domain.models.unidadSalud.UnidadSaludDetalle;
import com.itesm.domain.repository.UnidadSaludRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetUnidadSaludDetalleUseCase {

    private final UnidadSaludRepository unidadSaludRepository;

    @Inject
    public GetUnidadSaludDetalleUseCase(UnidadSaludRepository unidadSaludRepository) {
        this.unidadSaludRepository = unidadSaludRepository;
    }

    public UnidadSaludDetalleDto execute(Integer idUnidad, Integer periodoId) {

        if (periodoId == null) {
            throw new BadRequestException("El periodoId es obligatorio");
        }

        UnidadSaludDetalle unidad = unidadSaludRepository
                .findDetalleByIdAndPeriodoId(idUnidad, periodoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la unidad de salud solicitada"));

        return new UnidadSaludDetalleDto(
                unidad.getId(),
                unidad.getClues(),
                unidad.getNombre(),
                new TerritorioUnidadDto(
                        unidad.getIdMunicipio(),
                        unidad.getMunicipio(),
                        unidad.getIdEstado(),
                        unidad.getEstado()
                ),
                new ClasificacionUnidadDto(
                        unidad.getInstitucion(),
                        unidad.getTipoEstablecimiento(),
                        unidad.getTipologia(),
                        unidad.getNivelAtencion()
                ),
                new PersonalUnidadDto(
                        unidad.getPersonal().getTotalMedicos(),
                        unidad.getPersonal().getTotalEnfermeras()
                ),
                new InfraestructuraUnidadDto(
                        unidad.getInfraestructura().getTotalConsultorios(),
                        unidad.getInfraestructura().getTotalCamasHospitalizacion()
                )
        );
    }
}
