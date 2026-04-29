package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicadoresDto;
import com.itesm.application.dto.dashboard.IndicadoresResponseDto;
import com.itesm.application.dto.dashboard.PeriodoDto;
import com.itesm.application.dto.dashboard.TerritorioDto;
import com.itesm.domain.models.indicador.IndicadorMunicipio;
import com.itesm.domain.repository.IndicadorMunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetDashboardMunicipioUseCase {

    private final IndicadorMunicipioRepository indicadorMunicipioRepository;

    @Inject
    public GetDashboardMunicipioUseCase(IndicadorMunicipioRepository indicadorMunicipioRepository) {
        this.indicadorMunicipioRepository = indicadorMunicipioRepository;
    }

    public IndicadoresResponseDto execute(Integer idMunicipio, Integer idPeriodo) {

        IndicadorMunicipio indicador = indicadorMunicipioRepository
                .findByMunicipioIdAndPeriodoId(idMunicipio, idPeriodo)
                .orElseThrow(() -> new NotFoundException("No se encontraron indicadores para el municipio y periodo solicitados"));

        TerritorioDto territorio = new TerritorioDto(
                indicador.getIdMunicipio(),
                indicador.getNombreMunicipio(),
                "municipio"
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