package com.itesm.application.usecase.dashboard;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.TerritoryDto;
import com.itesm.domain.models.indicator.TerritoryIndicatorValueDto;
import com.itesm.domain.repository.TerritoryIndicatorQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetStateDashboardUseCase {

    private final TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository;

    @Inject
    public GetStateDashboardUseCase(TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository) {
        this.territoryIndicatorQueryRepository = territoryIndicatorQueryRepository;
    }

    public IndicatorsResponseDto execute(Integer stateId, Integer periodId) {
        Integer analysisYear = territoryIndicatorQueryRepository.findAnalysisYearByPeriodId(periodId)
                .orElseThrow(() -> new NotFoundException("No existe periodo para el id solicitado"));

        List<TerritoryIndicatorValueDto> values = territoryIndicatorQueryRepository.findByTerritoryAndYear(
                "state",
                stateId,
                null,
                analysisYear
        );

        if (values.isEmpty()) {
            throw new NotFoundException("No se encontraron indicadores para el estado y periodo solicitados");
        }

        TerritoryIndicatorValueDto first = values.get(0);
        Map<String, TerritoryIndicatorValueDto> byCode = values.stream()
                .collect(Collectors.toMap(
                        TerritoryIndicatorValueDto::getIndicatorCode,
                        Function.identity(),
                        (left, right) -> left
                ));

        return new IndicatorsResponseDto(
                new TerritoryDto(
                        first.getStateId(),
                        first.getStateName(),
                        "state"
                ),
                new PeriodDto(
                        periodId,
                        analysisYear
                ),
                new DashboardIndicatorsDto(
                        bigIntegerValue(byCode.get("total_population")),
                        decimalValue(byCode.get("percentage_over_60")),
                        bigIntegerValue(byCode.get("healthcare_access_deficiency")),
                        bigIntegerValue(byCode.get("total_poverty_population"))
                )
        );
    }

    private BigInteger bigIntegerValue(TerritoryIndicatorValueDto value) {
        if (value == null || value.getValue() == null) {
            return null;
        }

        return value.getValue().toBigInteger();
    }

    private BigDecimal decimalValue(TerritoryIndicatorValueDto value) {
        return value == null ? null : value.getValue();
    }
}
