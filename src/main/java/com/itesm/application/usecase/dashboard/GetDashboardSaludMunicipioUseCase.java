package com.itesm.application.usecase.dashboard;

import jakarta.ws.rs.NotFoundException;
import com.itesm.application.dto.dashboard.DashboardSaludResponseDto;
import com.itesm.application.dto.dashboard.PeriodoDto;
import com.itesm.application.dto.dashboard.SaludDashboardDto;
import com.itesm.application.dto.dashboard.TerritorioDto;
import com.itesm.domain.models.dashboard.DashboardSalud;
import com.itesm.domain.repository.DashboardSaludRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetDashboardSaludMunicipioUseCase {

    private final DashboardSaludRepository dashboardSaludRepository;

    @Inject
    public GetDashboardSaludMunicipioUseCase(DashboardSaludRepository dashboardSaludRepository) {
        this.dashboardSaludRepository = dashboardSaludRepository;
    }

    public DashboardSaludResponseDto execute(Integer idMunicipio, Integer idPeriodo) {

        DashboardSalud dashboard = dashboardSaludRepository
                .findSaludByMunicipioAndPeriodo(idMunicipio, idPeriodo)
                .orElseThrow(() -> new NotFoundException("No se encontraron datos de salud para el municipio y periodo solicitados"));

        return new DashboardSaludResponseDto(
                new TerritorioDto(
                        dashboard.getIdTerritorio(),
                        dashboard.getNombreTerritorio(),
                        dashboard.getTipoTerritorio()
                ),
                new PeriodoDto(
                        dashboard.getIdPeriodo(),
                        dashboard.getAnio()
                ),
                new SaludDashboardDto(
                        dashboard.getTotalUnidades(),
                        dashboard.getTotalMedicos(),
                        dashboard.getTotalEnfermeras(),
                        dashboard.getTotalConsultorios(),
                        dashboard.getTotalCamasHospitalizacion()
                )
        );
    }
}
