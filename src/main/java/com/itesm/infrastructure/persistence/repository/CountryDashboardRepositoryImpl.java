package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.dashboard.CountryIndicatorsDashboard;
import com.itesm.domain.models.dashboard.HealthDashboard;
import com.itesm.domain.repository.CountryDashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CountryDashboardRepositoryImpl implements CountryDashboardRepository {

    @Inject
    EntityManager em;

    @Override
    public Optional<CountryIndicatorsDashboard> findIndicatorsByPeriod(Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
                SELECT
                    p.id AS period_id,
                    p.period_year AS period_year,
                    COALESCE(SUM(si.total_population), 0) AS total_population,
                    ROUND(
                        COALESCE(
                            SUM(si.total_population * si.percentage_over_60) / NULLIF(SUM(si.total_population), 0),
                            0
                        ),
                        2
                    ) AS percentage_over_60,
                    COALESCE(SUM(si.healthcare_access_deficiency), 0) AS healthcare_access_deficiency,
                    COALESCE(SUM(si.total_poverty_population), 0) AS total_poverty_population
                FROM periods p
                LEFT JOIN state_indicators si ON si.period_id = p.id
                WHERE p.id = :periodId
                GROUP BY p.id, p.period_year
                """)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToCountryIndicators(result.get(0)));
    }

    @Override
    public Optional<HealthDashboard> findHealthByPeriod(Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
                SELECT
                    NULL AS territory_id,
                    'MÉXICO' AS territory_name,
                    'country' AS territory_type,
                    p.id AS period_id,
                    p.period_year AS period_year,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(staff.total_nurses, 0) AS total_nurses,
                    COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
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

    private CountryIndicatorsDashboard mapToCountryIndicators(Object[] row) {
        return new CountryIndicatorsDashboard(
                toInteger(row[0]),
                toInteger(row[1]),
                toBigInteger(row[2]),
                toBigDecimal(row[3]),
                toBigInteger(row[4]),
                toBigInteger(row[5])
        );
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

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Short) return ((Short) value).intValue();
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).intValue();
        return Integer.valueOf(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        return Long.valueOf(value.toString());
    }

    private BigInteger toBigInteger(Object value) {
        if (value == null) return BigInteger.ZERO;
        if (value instanceof BigInteger) return (BigInteger) value;
        if (value instanceof BigDecimal) return ((BigDecimal) value).toBigInteger();
        if (value instanceof Long) return BigInteger.valueOf((Long) value);
        if (value instanceof Integer) return BigInteger.valueOf((Integer) value);
        return new BigInteger(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof BigInteger) return new BigDecimal((BigInteger) value);
        return new BigDecimal(value.toString());
    }
}
