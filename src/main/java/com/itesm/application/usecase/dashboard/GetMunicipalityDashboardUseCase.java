package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.indicator.MunicipalityIndicator;
import com.itesm.domain.repository.MunicipalityIndicatorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetMunicipalityDashboardUseCase {

    private final MunicipalityIndicatorRepository municipalityIndicatorRepository;

    @Inject
    public GetMunicipalityDashboardUseCase(MunicipalityIndicatorRepository municipalityIndicatorRepository) {
        this.municipalityIndicatorRepository = municipalityIndicatorRepository;
    }

    public IndicatorsResponseDto execute(Integer municipalityId, Integer periodId) {
        MunicipalityIndicator indicator = municipalityIndicatorRepository
                .findByMunicipalityIdAndPeriodId(municipalityId, periodId)
                .orElseThrow(() -> new NotFoundException("No se encontraron indicadores para el municipio y periodo solicitados"));

        TerritoryDto territory = new TerritoryDto(
                indicator.getMunicipalityId(),
                indicator.getMunicipalityName(),
                "municipality"
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