package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.dashboard.CountryIndicatorsDashboard;
import com.itesm.domain.repository.CountryDashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetCountryIndicatorsDashboardUseCase {

    private final CountryDashboardRepository countryDashboardRepository;

    @Inject
    public GetCountryIndicatorsDashboardUseCase(CountryDashboardRepository countryDashboardRepository) {
        this.countryDashboardRepository = countryDashboardRepository;
    }

    public IndicatorsResponseDto execute(Integer periodId) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        CountryIndicatorsDashboard dashboard = countryDashboardRepository
                .findIndicatorsByPeriod(periodId)
                .orElseThrow(() -> new NotFoundException("No country indicators found for the requested period"));

        return new IndicatorsResponseDto(
                new TerritoryDto(
                        null,
                        "MÉXICO",
                        "country"
                ),
                new PeriodDto(
                        dashboard.getPeriodId(),
                        dashboard.getPeriodYear()
                ),
                new DashboardIndicatorsDto(
                        dashboard.getTotalPopulation(),
                        dashboard.getPercentageOver60(),
                        dashboard.getHealthcareAccessDeficiency(),
                        dashboard.getTotalPovertyPopulation()
                )
        );
    }
}
