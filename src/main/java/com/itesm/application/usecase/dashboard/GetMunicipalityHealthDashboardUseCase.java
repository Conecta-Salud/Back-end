package com.itesm.application.usecase.dashboard;

import jakarta.ws.rs.NotFoundException;
import com.itesm.application.dto.dashboard.HealthDashboardResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.HealthDashboardDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.dashboard.HealthDashboard;
import com.itesm.domain.repository.HealthDashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMunicipalityHealthDashboardUseCase {

    private final HealthDashboardRepository healthDashboardRepository;

    @Inject
    public GetMunicipalityHealthDashboardUseCase(HealthDashboardRepository healthDashboardRepository) {
        this.healthDashboardRepository = healthDashboardRepository;
    }

    public HealthDashboardResponseDto execute(Integer municipalityId, Integer periodId) {
        HealthDashboard dashboard = healthDashboardRepository
                .findHealthByMunicipalityAndPeriod(municipalityId, periodId)
                .orElseThrow(() -> new NotFoundException("No se encontraron datos de salud para el municipio y periodo solicitados"));

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
