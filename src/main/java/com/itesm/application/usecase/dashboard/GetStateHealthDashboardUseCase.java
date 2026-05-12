package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.*;
import com.itesm.domain.models.dashboard.HealthDashboard;
import com.itesm.domain.repository.HealthDashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetStateHealthDashboardUseCase {

    private final HealthDashboardRepository healthDashboardRepository;

    @Inject
    public GetStateHealthDashboardUseCase(HealthDashboardRepository healthDashboardRepository) {
        this.healthDashboardRepository = healthDashboardRepository;
    }

    public HealthDashboardResponseDto execute(Integer stateId, Integer periodId) {
        HealthDashboard dashboard = healthDashboardRepository
                .findHealthByStateAndPeriod(stateId, periodId)
                .orElseThrow(() -> new NotFoundException("No se encontraron datos de salud para el estado y periodo solicitados"));

        return new HealthDashboardResponseDto(
                new TerritoryDto(
                        dashboard.getTerritoryId(),
                        dashboard.getTerritoryName(),
                        dashboard.getTerritoryType()
                ),
                new PeriodDto(
                        dashboard.getPeriodId(),
                        dashboard.getPeriodYear()
                ),
                new HealthDashboardDto(
                        dashboard.getTotalHealthUnits(),
                        dashboard.getTotalDoctors(),
                        dashboard.getTotalNurses(),
                        dashboard.getTotalConsultingRooms(),
                        dashboard.getTotalHospitalBeds()
                )
        );
    }
}