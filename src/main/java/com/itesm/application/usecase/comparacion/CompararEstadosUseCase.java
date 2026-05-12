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
public class CompararEstadosUseCase {

    private final ComparacionRepository comparacionRepository;

    @Inject
    public CompararEstadosUseCase(ComparacionRepository comparacionRepository) {
        this.comparacionRepository = comparacionRepository;
    }

    public List<ComparacionTerritorioDto> execute(Integer periodoId, List<Integer> idsEstados) {

        if (periodoId == null) {
            throw new BadRequestException("El periodoId es obligatorio");
        }

        if (idsEstados == null || idsEstados.isEmpty()) {
            throw new BadRequestException("Debes enviar al menos un estado para comparar");
        }

        if (idsEstados.size() > 5) {
            throw new BadRequestException("Solo puedes comparar hasta 5 estados");
        }

        return comparacionRepository.compararEstados(periodoId, idsEstados)
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
