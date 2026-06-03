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
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(staff.total_nurses, 0) AS total_nurses,
                    COALESCE(infra.total_consulting_rooms, 0) AS total_consing_rooms,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
                FROM periods p
                LEFT JOIN (
                    SELECT
                        COUNT(DISTINCT hu.id) AS total_health_units
                    FROM health_units hu
                ) units ON 1 = 1
                LEFT JOIN (
                    SELECT
                        SUM(hus.total_doctors) AS total_doctors,
                        SUM(hus.total_nurses) AS total_nurses
                    FROM health_unit_staff hus
                    WHERE hus.period_id = :periodId
                ) staff ON 1 = 1
                LEFT JOIN (
                    SELECT
                        SUM(CASE
                            WHEN it.name = 'total_consultorios'
                            THEN huid.quantity ELSE 0 END
                        ) AS total_consulting_rooms,
                        SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END
                        ) AS total_hospital_beds
                    FROM health_unit_infrastructure hui
                    JOIN health_unit_infrastructure_details huid
                        ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it
                        ON it.id = huid.infrastructure_type_id
                    WHERE hui.period_id = :periodId
                ) infra ON 1 = 1
                WHERE p.id = :periodId
                """)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToHealthDashboard(result.get(0)));
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
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
