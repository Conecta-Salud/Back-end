package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.dashboard.HealthDashboard;
import com.itesm.domain.repository.HealthDashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HealthDashboardRepositoryImpl implements HealthDashboardRepository {

    @Inject
    EntityManager em;

    @Override
    public Optional<HealthDashboard> findHealthByStateAndPeriod(Integer stateId, Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
                SELECT 
                    s.id AS territory_id,
                    s.name AS territory_name,
                    'state' AS territory_type,
                    p.id AS period_id,
                    p.period_year AS period_year,
                    %s AS total_health_units,
                    %s AS total_doctors,
                    %s AS total_nurses,
                    %s AS total_consulting_rooms,
                    %s AS total_hospital_beds
                FROM states s
                JOIN periods p ON p.id = :periodId
                LEFT JOIN territory_indicator_values tiv
                    ON tiv.state_id = s.id
                   AND tiv.territory_level = 'state'
                   AND tiv.analysis_year = p.period_year
                LEFT JOIN indicators i ON i.id = tiv.indicator_id
                LEFT JOIN data_availability da
                    ON da.indicator_id = i.id
                   AND da.territory_level = tiv.territory_level
                   AND da.analysis_year = tiv.analysis_year
                WHERE s.id = :stateId
                GROUP BY s.id, s.name, p.id, p.period_year
                """.formatted(
                indicatorValue("health_establishments"),
                indicatorValue("total_doctors"),
                indicatorValue("total_nurses"),
                indicatorValue("consulting_rooms"),
                indicatorValue("hospital_beds")
        ))
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToHealthDashboard(result.get(0)));
    }

    @Override
    public Optional<HealthDashboard> findHealthByMunicipalityAndPeriod(Integer municipalityId, Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
                SELECT 
                    m.id AS territory_id,
                    m.name AS territory_name,
                    'municipality' AS territory_type,
                    p.id AS period_id,
                    p.period_year AS period_year,
                    %s AS total_health_units,
                    %s AS total_doctors,
                    %s AS total_nurses,
                    %s AS total_consulting_rooms,
                    %s AS total_hospital_beds
                FROM municipalities m
                JOIN periods p ON p.id = :periodId
                LEFT JOIN territory_indicator_values tiv
                    ON tiv.municipality_id = m.id
                   AND tiv.territory_level = 'municipality'
                   AND tiv.analysis_year = p.period_year
                LEFT JOIN indicators i ON i.id = tiv.indicator_id
                LEFT JOIN data_availability da
                    ON da.indicator_id = i.id
                   AND da.territory_level = tiv.territory_level
                   AND da.analysis_year = tiv.analysis_year
                WHERE m.id = :municipalityId
                GROUP BY m.id, m.name, p.id, p.period_year
                """.formatted(
                indicatorValue("health_establishments"),
                indicatorValue("total_doctors"),
                indicatorValue("total_nurses"),
                indicatorValue("consulting_rooms"),
                indicatorValue("hospital_beds")
        ))
                .setParameter("municipalityId", municipalityId)
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
                toInteger(row[0]),
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
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        return Long.valueOf(value.toString());
    }
}
