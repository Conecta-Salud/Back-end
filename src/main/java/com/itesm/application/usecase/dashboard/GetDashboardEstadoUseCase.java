package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicadoresDto;
import com.itesm.application.dto.dashboard.IndicadoresResponseDto;
import com.itesm.application.dto.dashboard.PeriodoDto;
import com.itesm.application.dto.dashboard.TerritorioDto;
import com.itesm.domain.models.indicador.IndicadorEstado;
import com.itesm.domain.repository.IndicadorEstadoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetDashboardEstadoUseCase {

    private final IndicadorEstadoRepository indicadorEstadoRepository;

    @Inject
    public GetDashboardEstadoUseCase(IndicadorEstadoRepository indicadorEstadoRepository) {
        this.indicadorEstadoRepository = indicadorEstadoRepository;
    }

    public IndicadoresResponseDto execute(Integer idEstado, Integer idPeriodo) {

        IndicadorEstado indicador = indicadorEstadoRepository
                .findByEstadoIdAndPeriodoId(idEstado, idPeriodo)
                .orElseThrow(() -> new NotFoundException("No se encontraron indicadores para el estado y periodo solicitados"));

        TerritorioDto territorio = new TerritorioDto(
                indicador.getIdEstado(),
                indicador.getNombreEstado(),
                "estado"
        );

        PeriodoDto periodo = new PeriodoDto(
                indicador.getIdPeriodo(),
                indicador.getAnio()
        );

        DashboardIndicadoresDto indicadores = new DashboardIndicadoresDto(
                indicador.getPoblacionTotal(),
                indicador.getPorcentaje60mas(),
                indicador.getCarenciaAccesoSalud(),
                indicador.getSituacionPobrezaTotal()
        );

        return new IndicadoresResponseDto(territorio, periodo, indicadores);
    }
}