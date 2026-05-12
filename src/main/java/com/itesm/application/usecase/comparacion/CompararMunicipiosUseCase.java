package com.itesm.application.usecase.comparacion;

import com.itesm.application.dto.comparacion.ComparacionTerritorioDto;
import com.itesm.application.dto.dashboard.DashboardIndicadoresDto;
import com.itesm.application.dto.dashboard.PeriodoDto;
import com.itesm.application.dto.dashboard.SaludDashboardDto;
import com.itesm.domain.repository.ComparacionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CompararMunicipiosUseCase {

    private final ComparacionRepository comparacionRepository;

    @Inject
    public CompararMunicipiosUseCase(ComparacionRepository comparacionRepository) {
        this.comparacionRepository = comparacionRepository;
    }

    public List<ComparacionTerritorioDto> execute(Integer periodoId, List<Integer> idsMunicipios) {

        if (periodoId == null) {
            throw new BadRequestException("El periodoId es obligatorio");
        }

        if (idsMunicipios == null || idsMunicipios.isEmpty()) {
            throw new BadRequestException("Debes enviar al menos un municipio para comparar");
        }

        if (idsMunicipios.size() > 5) {
            throw new BadRequestException("Solo puedes comparar hasta 5 municipios");
        }

        return comparacionRepository.compararMunicipios(periodoId, idsMunicipios)
                .stream()
                .map(item -> new ComparacionTerritorioDto(
                        item.getId(),
                        item.getNombre(),
                        item.getTipo(),
                        new PeriodoDto(
                                item.getIdPeriodo(),
                                item.getAnio()
                        ),
                        new DashboardIndicadoresDto(
                                item.getPoblacionTotal(),
                                item.getPorcentaje60mas(),
                                item.getCarenciaAccesoSalud(),
                                item.getSituacionPobrezaTotal()
                        ),
                        new SaludDashboardDto(
                                item.getTotalUnidades(),
                                item.getTotalMedicos(),
                                item.getTotalEnfermeras(),
                                item.getTotalConsultorios(),
                                item.getTotalCamasHospitalizacion()
                        )
                ))
                .collect(Collectors.toList());
    }

}
