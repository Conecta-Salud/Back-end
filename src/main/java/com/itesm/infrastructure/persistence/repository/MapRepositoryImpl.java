package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.map.MapIndicatorType;
import com.itesm.domain.models.map.MapIndicator;
import com.itesm.domain.repository.MapRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MapRepositoryImpl implements MapRepository {

    @Inject
    EntityManager em;

    @Override
    public boolean existsPeriodByYear(Integer year) {
        Long count = em.createQuery(
                        "SELECT COUNT(p) FROM PeriodEntity p WHERE p.periodYear = :year",
                        Long.class
                )
                .setParameter("year", year)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsStateByCode(String stateCode) {
        Long count = em.createQuery(
                        "SELECT COUNT(s) FROM StateEntity s WHERE s.inegiCode = :stateCode",
                        Long.class
                )
                .setParameter("stateCode", stateCode)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<MapIndicator> findStateIndicators(MapIndicatorType indicatorType, Integer year) {
        String sql = switch (indicatorType) {
            case MEDICAL_COVERAGE -> sqlStateMedicalCoverage();
            case HOSPITAL_BEDS -> sqlStateHospitalBeds();
            case HEALTHCARE_ACCESS_DEFICIENCY -> sqlStateHealthcareAccessDeficiency();
        };

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("year", year)
                .getResultList();

        return rows.stream()
                .map(row -> mapRow(row, indicatorType))
                .collect(Collectors.toList());
    }

    @Override
    public List<MapIndicator> findMunicipalityIndicators(String stateCode, MapIndicatorType indicatorType, Integer year) {
        String sql = switch (indicatorType) {
            case MEDICAL_COVERAGE -> sqlMunicipalityMedicalCoverage();
            case HOSPITAL_BEDS -> sqlMunicipalityHospitalBeds();
            case HEALTHCARE_ACCESS_DEFICIENCY -> sqlMunicipalityHealthcareAccessDeficiency();
        };

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("stateCode", stateCode)
                .setParameter("year", year)
                .getResultList();

        return rows.stream()
                .map(row -> mapRow(row, indicatorType))
                .collect(Collectors.toList());
    }

    private String sqlStateMedicalCoverage() {
        return """
                SELECT
                    s.inegi_code AS code,
                    s.name AS name,
                    ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000, 2) AS value
                FROM states s
                JOIN state_indicators si ON si.state_id = s.id
                JOIN periods p ON p.id = si.period_id
                LEFT JOIN municipalities m ON m.state_id = s.id
                LEFT JOIN health_units hu ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus
                    ON hus.health_unit_id = hu.id
                    AND hus.period_id = p.id
                WHERE p.period_year = :year
                GROUP BY s.id, s.inegi_code, s.name, si.total_population
                ORDER BY s.name ASC
                """;
    }

    private String sqlStateHospitalBeds() {
        return """
                SELECT
                    s.inegi_code AS code,
                    s.name AS name,
                    ROUND((COALESCE(SUM(CASE 
                        WHEN it.name = 'total_camas_hospitalizacion' 
                        THEN huid.quantity ELSE 0 END), 0) / NULLIF(si.total_population, 0)) * 1000, 2) AS value
                FROM states s
                JOIN state_indicators si ON si.state_id = s.id
                JOIN periods p ON p.id = si.period_id
                LEFT JOIN municipalities m ON m.state_id = s.id
                LEFT JOIN health_units hu ON hu.municipality_id = m.id
                LEFT JOIN health_unit_infrastructure hui
                    ON hui.health_unit_id = hu.id
                    AND hui.period_id = p.id
                LEFT JOIN health_unit_infrastructure_details huid
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it
                    ON it.id = huid.infrastructure_type_id
                WHERE p.period_year = :year
                GROUP BY s.id, s.inegi_code, s.name, si.total_population
                ORDER BY s.name ASC
                """;
    }

    private String sqlStateHealthcareAccessDeficiency() {
        return """
                SELECT
                    s.inegi_code AS code,
                    s.name AS name,
                    ROUND((si.healthcare_access_deficiency / NULLIF(si.total_population, 0)) * 100, 2) AS value
                FROM states s
                JOIN state_indicators si ON si.state_id = s.id
                JOIN periods p ON p.id = si.period_id
                WHERE p.period_year = :year
                ORDER BY s.name ASC
                """;
    }

    private String sqlMunicipalityMedicalCoverage() {
        return """
                SELECT
                    m.inegi_code AS code,
                    m.name AS name,
                    ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000, 2) AS value
                FROM municipalities m
                JOIN states s ON s.id = m.state_id
                JOIN municipality_indicators mi ON mi.municipality_id = m.id
                JOIN periods p ON p.id = mi.period_id
                LEFT JOIN health_units hu ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus
                    ON hus.health_unit_id = hu.id
                    AND hus.period_id = p.id
                WHERE s.inegi_code = :stateCode
                AND p.period_year = :year
                GROUP BY m.id, m.inegi_code, m.name, mi.total_population
                ORDER BY m.name ASC
                """;
    }

    private String sqlMunicipalityHospitalBeds() {
        return """
                SELECT
                    m.inegi_code AS code,
                    m.name AS name,
                    ROUND((COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0) / NULLIF(mi.total_population, 0)) * 1000, 2) AS value
                FROM municipalities m
                JOIN states s ON s.id = m.state_id
                JOIN municipality_indicators mi ON mi.municipality_id = m.id
                JOIN periods p ON p.id = mi.period_id
                LEFT JOIN health_units hu ON hu.municipality_id = m.id
                LEFT JOIN health_unit_infrastructure hui
                    ON hui.health_unit_id = hu.id
                    AND hui.period_id = p.id
                LEFT JOIN health_unit_infrastructure_details huid
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it
                    ON it.id = huid.infrastructure_type_id
                WHERE s.inegi_code = :stateCode
                AND p.period_year = :year
                GROUP BY m.id, m.inegi_code, m.name, mi.total_population
                ORDER BY m.name ASC
                """;
    }

    private String sqlMunicipalityHealthcareAccessDeficiency() {
        return """
                SELECT
                    m.inegi_code AS code,
                    m.name AS name,
                    ROUND((mi.healthcare_access_deficiency / NULLIF(mi.total_population, 0)) * 100, 2) AS value
                FROM municipalities m
                JOIN states s ON s.id = m.state_id
                JOIN municipality_indicators mi ON mi.municipality_id = m.id
                JOIN periods p ON p.id = mi.period_id
                WHERE s.inegi_code = :stateCode
                AND p.period_year = :year
                ORDER BY m.name ASC
                """;
    }

    private MapIndicator mapRow(Object[] row, MapIndicatorType indicatorType) {
        return new MapIndicator(
                (String) row[0],
                (String) row[1],
                toBigDecimal(row[2]),
                indicatorType
        );
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }
}