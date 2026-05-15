package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.HealthDashboardDto;
import com.itesm.application.dto.dashboard.HealthDashboardResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.dashboard.HealthDashboard;
import com.itesm.domain.repository.CountryDashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetCountryHealthDashboardUseCase {

    private final CountryDashboardRepository countryDashboardRepository;

    @Inject
    public GetCountryHealthDashboardUseCase(CountryDashboardRepository countryDashboardRepository) {
        this.countryDashboardRepository = countryDashboardRepository;
    }

    public HealthDashboardResponseDto execute(Integer periodId) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        HealthDashboard dashboard = countryDashboardRepository
                .findHealthByPeriod(periodId)
                .orElseThrow(() -> new NotFoundException("No country health data found for the requested period"));

        return new HealthDashboardResponseDto(
                new TerritoryDto(
                        null,
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
