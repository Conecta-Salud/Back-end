package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.*;
import com.itesm.domain.models.dashboard.DashboardSalud;
import com.itesm.domain.repository.DashboardSaludRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetDashboardSaludEstadoUseCase {

    private final DashboardSaludRepository dashboardSaludRepository;

    @Inject
    public GetDashboardSaludEstadoUseCase(DashboardSaludRepository dashboardSaludRepository) {
        this.dashboardSaludRepository = dashboardSaludRepository;
    }

    public DashboardSaludResponseDto execute(Integer idEstado, Integer idPeriodo) {

        DashboardSalud dashboard = dashboardSaludRepository
                .findSaludByEstadoAndPeriodo(idEstado, idPeriodo)
                .orElseThrow(() -> new NotFoundException("No se encontraron datos de salud para el estado y periodo solicitados"));

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