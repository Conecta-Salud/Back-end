package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.dashboard.CountryIndicatorsDashboard;
import com.itesm.domain.models.dashboard.HealthDashboard;
import com.itesm.domain.models.indicator.TerritoryIndicatorValueDto;
import com.itesm.domain.repository.CountryDashboardRepository;
import com.itesm.domain.repository.TerritoryIndicatorQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class CountryDashboardRepositoryImpl implements CountryDashboardRepository {

    private final EntityManager em;
    private final TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository;

    public CountryDashboardRepositoryImpl(
            EntityManager em,
            TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository
    ) {
        this.em = em;
        this.territoryIndicatorQueryRepository = territoryIndicatorQueryRepository;
    }

    @Override
    public Optional<CountryIndicatorsDashboard> findIndicatorsByPeriod(Integer periodId) {
        Optional<Integer> analysisYear = territoryIndicatorQueryRepository.findAnalysisYearByPeriodId(periodId);

        if (analysisYear.isEmpty()) {
            return Optional.empty();
        }

        List<TerritoryIndicatorValueDto> values = territoryIndicatorQueryRepository.findByTerritoryAndYear(
                "country",
                null,
                null,
                analysisYear.get()
        );

        if (values.isEmpty()) {
            return Optional.empty();
        }

        Map<String, TerritoryIndicatorValueDto> byCode = values.stream()
                .collect(Collectors.toMap(
                        TerritoryIndicatorValueDto::getIndicatorCode,
                        Function.identity(),
                        (left, right) -> left
                ));

        return Optional.of(new CountryIndicatorsDashboard(
                periodId,
                analysisYear.get(),
                bigIntegerValue(byCode.get("total_population")),
                decimalValue(byCode.get("percentage_over_60")),
                bigIntegerValue(byCode.get("healthcare_access_deficiency")),
                bigIntegerValue(byCode.get("total_poverty_population"))
        ));
    }

    @Override
    public Optional<HealthDashboard> findHealthByPeriod(Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
                SELECT
                    NULL AS territory_id,
                    'MEXICO' AS territory_name,
                    'country' AS territory_type,
                    p.id AS period_id,
                    p.period_year AS period_year,
                    %s AS total_health_units,
                    %s AS total_doctors,
                    %s AS total_nurses,
                    %s AS total_consulting_rooms,
                    %s AS total_hospital_beds
                FROM periods p
                LEFT JOIN territory_indicator_values tiv
                    ON tiv.territory_level = 'country'
                   AND tiv.state_id IS NULL
                   AND tiv.municipality_id IS NULL
                   AND tiv.analysis_year = p.period_year
                LEFT JOIN indicators i ON i.id = tiv.indicator_id
                LEFT JOIN data_availability da
                    ON da.indicator_id = i.id
                   AND da.territory_level = tiv.territory_level
                   AND da.analysis_year = tiv.analysis_year
                WHERE p.id = :periodId
                GROUP BY p.id, p.period_year
                """.formatted(
                indicatorValue("health_establishments"),
                indicatorValue("total_doctors"),
                indicatorValue("total_nurses"),
                indicatorValue("consulting_rooms"),
                indicatorValue("hospital_beds")
        ))
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToHealthDashboard(result.get(0)));
    }

    private String indicatorValue(String indicatorCode) {
        return ("MAX(CASE WHEN i.code = '%s' AND COALESCE(da.is_available, 1) = 1 "
                + "AND COALESCE(da.availability_status, tiv.availability_status) NOT IN ('not_available', 'not_applicable') "
                + "THEN tiv.value END)").formatted(indicatorCode);
    }

    private HealthDashboard mapToHealthDashboard(Object[] row) {
        return new HealthDashboard(
                null,
                (String) row[1],
                (String) row[2],
                toInteger(row[3]),
                toInteger(row[4]),
                toLong(row[5]),
                toLong(row[6]),
                toLong(row[7]),
                toLong(row[8]),
                toLong(row[9])
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

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
