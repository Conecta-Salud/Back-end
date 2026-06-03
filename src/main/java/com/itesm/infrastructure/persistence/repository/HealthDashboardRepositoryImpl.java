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
                    COUNT(DISTINCT hu.id) AS total_health_units,
                    COALESCE(SUM(hus.total_doctors), 0) AS total_doctors,
                    COALESCE(SUM(hus.total_nurses), 0) AS total_nurses,
                    COALESCE(SUM(CASE 
                        WHEN it.code = 'total_consultorios' 
                        THEN huid.quantity ELSE 0 END), 0) AS total_consulting_rooms,
                    COALESCE(SUM(CASE 
                        WHEN it.code = 'total_camas_hospitalizacion' 
                        THEN huid.quantity ELSE 0 END), 0) AS total_hospital_beds
                FROM states s
                JOIN municipalities m ON m.state_id = s.id
                JOIN health_units hu ON hu.municipality_id = m.id
                JOIN periods p ON p.id = :periodId
                LEFT JOIN health_unit_staff hus 
                    ON hus.health_unit_id = hu.id 
                    AND hus.period_id = p.id
                LEFT JOIN health_unit_infrastructure hui 
                    ON hui.health_unit_id = hu.id 
                    AND hui.period_id = p.id
                LEFT JOIN health_unit_infrastructure_details huid 
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it 
                    ON it.id = huid.infrastructure_type_id
                WHERE s.id = :stateId
                GROUP BY s.id, s.name, p.id, p.period_year
                """)
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
                    COUNT(DISTINCT hu.id) AS total_health_units,
                    COALESCE(SUM(hus.total_doctors), 0) AS total_doctors,
                    COALESCE(SUM(hus.total_nurses), 0) AS total_nurses,
                    COALESCE(SUM(CASE 
                        WHEN it.code = 'total_consultorios' 
                        THEN huid.quantity ELSE 0 END), 0) AS total_consulting_rooms,
                    COALESCE(SUM(CASE 
                        WHEN it.code = 'total_camas_hospitalizacion' 
                        THEN huid.quantity ELSE 0 END), 0) AS total_hospital_beds
                FROM municipalities m
                JOIN health_units hu ON hu.municipality_id = m.id
                JOIN periods p ON p.id = :periodId
                LEFT JOIN health_unit_staff hus 
                    ON hus.health_unit_id = hu.id 
                    AND hus.period_id = p.id
                LEFT JOIN health_unit_infrastructure hui 
                    ON hui.health_unit_id = hu.id 
                    AND hui.period_id = p.id
                LEFT JOIN health_unit_infrastructure_details huid 
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it 
                    ON it.id = huid.infrastructure_type_id
                WHERE m.id = :municipalityId
                GROUP BY m.id, m.name, p.id, p.period_year
                """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToHealthDashboard(result.get(0)));
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
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        return Long.valueOf(value.toString());
    }
}