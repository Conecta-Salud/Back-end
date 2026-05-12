package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.indicator.StateIndicator;
import com.itesm.domain.repository.StateIndicatorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetStateDashboardUseCase {

    private final StateIndicatorRepository stateIndicatorRepository;

    @Inject
    public GetStateDashboardUseCase(StateIndicatorRepository stateIndicatorRepository) {
        this.stateIndicatorRepository = stateIndicatorRepository;
    }

    public IndicatorsResponseDto execute(Integer stateId, Integer periodId) {
        StateIndicator indicator = stateIndicatorRepository
                .findByStateIdAndPeriodId(stateId, periodId)
                .orElseThrow(() -> new NotFoundException("No se encontraron indicadores para el estado y periodo solicitados"));

        TerritoryDto territory = new TerritoryDto(
                indicator.getStateId(),
                indicator.getStateName(),
                "state"
        );

        PeriodDto period = new PeriodDto(
                indicator.getPeriodId(),
                indicator.getPeriodYear()
        );

        DashboardIndicatorsDto indicators = new DashboardIndicatorsDto(
                indicator.getTotalPopulation(),
                indicator.getPercentageOver60(),
                indicator.getHealthcareAccessDeficiency(),
                indicator.getTotalPovertyPopulation()
        );

        return new IndicatorsResponseDto(territory, period, indicators);
    }
}