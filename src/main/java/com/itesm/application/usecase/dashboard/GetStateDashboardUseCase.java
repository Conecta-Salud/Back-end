package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.Uploader.Establecimiento.State;
import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;
import com.itesm.domain.models.period.Period;
import com.itesm.domain.repository.PeriodRepository;
import com.itesm.domain.repository.Upload.Establecimiento.StateRepository;
import com.itesm.domain.repository.Upload.Indicadores.TerritoryIndicatorValuesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;

@ApplicationScoped
public class GetStateDashboardUseCase {

    private final TerritoryIndicatorValuesRepository territoryIndicatorValuesRepository;
    private final StateRepository stateRepository;
    private final PeriodRepository periodRepository;

    @Inject
    public GetStateDashboardUseCase(
            TerritoryIndicatorValuesRepository territoryIndicatorValuesRepository,
            StateRepository stateRepository,
            PeriodRepository periodRepository
    ) {
        this.territoryIndicatorValuesRepository = territoryIndicatorValuesRepository;
        this.stateRepository = stateRepository;
        this.periodRepository = periodRepository;
    }

    public IndicatorsResponseDto execute(Integer stateId, Integer periodId) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        Period period = periodRepository.findPeriodById(periodId)
                .orElseThrow(() -> new NotFoundException("No se encontró el periodo solicitado"));

        Short analysisYear = period.getPeriodYear().shortValue();
        State state = stateRepository.findStateById(stateId).orElse(null);

        TerritoryIndicatorValues totalPopulation = territoryIndicatorValuesRepository
                .findStateIndicatorByCode("total_population", stateId, analysisYear)
                .orElse(null);

        TerritoryIndicatorValues percentageOver60 = territoryIndicatorValuesRepository
                .findStateIndicatorByCode("percentage_over_60", stateId, analysisYear)
                .orElse(null);

        TerritoryIndicatorValues healthcareAccessDeficiency = territoryIndicatorValuesRepository
                .findStateIndicatorByCode("healthcare_access_deficiency", stateId, analysisYear)
                .orElse(null);

        TerritoryIndicatorValues totalPovertyPopulation = territoryIndicatorValuesRepository
                .findStateIndicatorByCode("total_poverty_population", stateId, analysisYear)
                .orElse(null);

        if (totalPopulation == null
                && percentageOver60 == null
                && healthcareAccessDeficiency == null
                && totalPovertyPopulation == null) {
            throw new NotFoundException("No se encontraron indicadores para el estado y periodo solicitados");
        }

        return new IndicatorsResponseDto(
                new TerritoryDto(
                        stateId,
                        state != null ? state.getName() : null,
                        "state"
                ),
                new PeriodDto(
                        periodId,
                        period.getPeriodYear()
                ),
                new DashboardIndicatorsDto(
                        toBigInteger(totalPopulation),
                        toBigDecimal(percentageOver60),
                        toBigInteger(healthcareAccessDeficiency),
                        toBigInteger(totalPovertyPopulation)
                )
        );
    }

    private BigInteger toBigInteger(TerritoryIndicatorValues values) {
        return values == null || values.getValue() == null ? null : values.getValue().toBigInteger();
    }

    private BigDecimal toBigDecimal(TerritoryIndicatorValues values) {
        return values == null ? null : values.getValue();
    }
}
