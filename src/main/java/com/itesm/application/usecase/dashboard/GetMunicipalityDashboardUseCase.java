package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.Uploader.Establecimiento.Municipality;
import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;
import com.itesm.domain.models.period.Period;
import com.itesm.domain.repository.PeriodRepository;
import com.itesm.domain.repository.Upload.Establecimiento.MunicipalityRepository;
import com.itesm.domain.repository.Upload.Indicadores.TerritoryIndicatorValuesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;

@ApplicationScoped
public class GetMunicipalityDashboardUseCase {

    private final TerritoryIndicatorValuesRepository territoryIndicatorValuesRepository;
    private final MunicipalityRepository municipalityRepository;
    private final PeriodRepository periodRepository;

    @Inject
    public GetMunicipalityDashboardUseCase(
            TerritoryIndicatorValuesRepository territoryIndicatorValuesRepository,
            MunicipalityRepository municipalityRepository,
            PeriodRepository periodRepository
    ) {
        this.territoryIndicatorValuesRepository = territoryIndicatorValuesRepository;
        this.municipalityRepository = municipalityRepository;
        this.periodRepository = periodRepository;
    }

    public IndicatorsResponseDto execute(Integer municipalityId, Integer periodId) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        Period period = periodRepository.findPeriodById(periodId)
                .orElseThrow(() -> new NotFoundException("No se encontró el periodo solicitado"));

        Short analysisYear = period.getPeriodYear().shortValue();
        Municipality municipality = municipalityRepository.findMunicipalityById(municipalityId).orElse(null);

        TerritoryIndicatorValues totalPopulation = territoryIndicatorValuesRepository
                .findMunicipalityIndicatorByCode("total_population", municipalityId, analysisYear)
                .orElse(null);

        TerritoryIndicatorValues percentageOver60 = territoryIndicatorValuesRepository
                .findMunicipalityIndicatorByCode("percentage_over_60", municipalityId, analysisYear)
                .orElse(null);

        TerritoryIndicatorValues healthcareAccessDeficiency = territoryIndicatorValuesRepository
                .findMunicipalityIndicatorByCode("healthcare_access_deficiency", municipalityId, analysisYear)
                .orElse(null);

        TerritoryIndicatorValues totalPovertyPopulation = territoryIndicatorValuesRepository
                .findMunicipalityIndicatorByCode("total_poverty_population", municipalityId, analysisYear)
                .orElse(null);

        if (totalPopulation == null
                && percentageOver60 == null
                && healthcareAccessDeficiency == null
                && totalPovertyPopulation == null) {
            throw new NotFoundException("No se encontraron indicadores para el municipio y periodo solicitados");
        }

        return new IndicatorsResponseDto(
                new TerritoryDto(
                        municipalityId,
                        municipality != null ? municipality.getName() : null,
                        "municipality"
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
