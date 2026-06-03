package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.comparison.TerritoryComparison;
import com.itesm.domain.repository.ComparisonRepository;
import com.itesm.domain.repository.TerritoryIndicatorQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class ComparisonRepositoryImpl implements ComparisonRepository {

    private final EntityManager em;
    private final TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository;

    public ComparisonRepositoryImpl(
            EntityManager em,
            TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository
    ) {
        this.em = em;
        this.territoryIndicatorQueryRepository = territoryIndicatorQueryRepository;
    }

    @Override
    public List<TerritoryComparison> compareStates(Integer periodId, List<Integer> stateIds) {
        return compareStatesByFilter(periodId, "s.id IN (:values)", "values", stateIds);
    }

    @Override
    public List<TerritoryComparison> compareStatesByCodes(Integer periodId, List<String> stateCodes) {
        return compareStatesByFilter(periodId, "s.inegi_code IN (:values)", "values", stateCodes);
    }

    @Override
    public List<TerritoryComparison> compareMunicipalities(Integer periodId, List<Integer> municipalityIds) {
        return compareMunicipalitiesByFilter(periodId, "m.id IN (:values)", "values", municipalityIds);
    }

    @Override
    public List<TerritoryComparison> compareMunicipalitiesByCodes(Integer periodId, List<String> municipalityCodes) {
        return compareMunicipalitiesByFilter(periodId, "m.inegi_code IN (:values)", "values", municipalityCodes);
    }

    private List<TerritoryComparison> compareStatesByFilter(
            Integer periodId,
            String filter,
            String parameterName,
            List<?> values
    ) {
        Integer analysisYear = territoryIndicatorQueryRepository.findAnalysisYearByPeriodId(periodId)
                .orElse(null);

        if (analysisYear == null) {
            return List.of();
        }

        String sql = """
                SELECT
                    s.id,
                    s.name,
                    'state' AS type,
                    p.id AS period_id,
                    p.period_year,
                    %s AS total_population,
                    %s AS percentage_over_60,
                    %s AS healthcare_access_deficiency,
                    %s AS total_poverty_population,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(staff.total_nurses, 0) AS total_nurses,
                    COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
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
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        COUNT(DISTINCT hu.id) AS total_health_units
                    FROM municipalities m
                    JOIN health_units hu ON hu.municipality_id = m.id
                    GROUP BY m.state_id
                ) units ON units.state_id = s.id
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        SUM(hus.total_doctors) AS total_doctors,
                        SUM(hus.total_nurses) AS total_nurses
                    FROM municipalities m
                    JOIN health_units hu ON hu.municipality_id = m.id
                    JOIN health_unit_staff hus ON hus.health_unit_id = hu.id
                    WHERE hus.period_id = :periodId
                    GROUP BY m.state_id
                ) staff ON staff.state_id = s.id
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        SUM(CASE WHEN it.code = 'total_consultorios' THEN huid.quantity ELSE 0 END) AS total_consulting_rooms,
                        SUM(CASE WHEN it.code = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END) AS total_hospital_beds
                    FROM municipalities m
                    JOIN health_units hu ON hu.municipality_id = m.id
                    JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                    JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                    WHERE hui.period_id = :periodId
                    GROUP BY m.state_id
                ) infra ON infra.state_id = s.id
                WHERE %s
                GROUP BY
                    s.id,
                    s.name,
                    p.id,
                    p.period_year,
                    units.total_health_units,
                    staff.total_doctors,
                    staff.total_nurses,
                    infra.total_consulting_rooms,
                    infra.total_hospital_beds
                ORDER BY s.name ASC
                """.formatted(
                indicatorValue("total_population"),
                indicatorValue("percentage_over_60"),
                indicatorValue("healthcare_access_deficiency"),
                indicatorValue("total_poverty_population"),
                filter
        );

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList(parameterName, values);

        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapRowToTerritoryComparison)
                .toList();
    }

    private List<TerritoryComparison> compareMunicipalitiesByFilter(
            Integer periodId,
            String filter,
            String parameterName,
            List<?> values
    ) {
        Integer analysisYear = territoryIndicatorQueryRepository.findAnalysisYearByPeriodId(periodId)
                .orElse(null);

        if (analysisYear == null) {
            return List.of();
        }

        String sql = """
                SELECT
                    m.id,
                    m.name,
                    'municipality' AS type,
                    p.id AS period_id,
                    p.period_year,
                    %s AS total_population,
                    %s AS percentage_over_60,
                    %s AS healthcare_access_deficiency,
                    %s AS total_poverty_population,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(staff.total_nurses, 0) AS total_nurses,
                    COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
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
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        COUNT(DISTINCT hu.id) AS total_health_units
                    FROM health_units hu
                    GROUP BY hu.municipality_id
                ) units ON units.municipality_id = m.id
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        SUM(hus.total_doctors) AS total_doctors,
                        SUM(hus.total_nurses) AS total_nurses
                    FROM health_units hu
                    JOIN health_unit_staff hus ON hus.health_unit_id = hu.id
                    WHERE hus.period_id = :periodId
                    GROUP BY hu.municipality_id
                ) staff ON staff.municipality_id = m.id
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        SUM(CASE WHEN it.code = 'total_consultorios' THEN huid.quantity ELSE 0 END) AS total_consulting_rooms,
                        SUM(CASE WHEN it.code = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END) AS total_hospital_beds
                    FROM health_units hu
                    JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                    JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                    WHERE hui.period_id = :periodId
                    GROUP BY hu.municipality_id
                ) infra ON infra.municipality_id = m.id
                WHERE %s
                GROUP BY
                    m.id,
                    m.name,
                    p.id,
                    p.period_year,
                    units.total_health_units,
                    staff.total_doctors,
                    staff.total_nurses,
                    infra.total_consulting_rooms,
                    infra.total_hospital_beds
                ORDER BY m.name ASC
                """.formatted(
                indicatorValue("total_population"),
                indicatorValue("percentage_over_60"),
                indicatorValue("healthcare_access_deficiency"),
                indicatorValue("total_poverty_population"),
                filter
        );

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList(parameterName, values);

        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapRowToTerritoryComparison)
                .toList();
    }

    private String indicatorValue(String indicatorCode) {
        return "MAX(CASE WHEN i.code = '%s' AND da.is_available = 1 "
                + "AND da.availability_status NOT IN ('not_available', 'not_applicable') "
                + "THEN tiv.value END)".formatted(indicatorCode);
    }

    private TerritoryComparison mapRowToTerritoryComparison(Object[] row) {
        return new TerritoryComparison(
                toInteger(row[0]),
                (String) row[1],
                (String) row[2],
                toInteger(row[3]),
                toInteger(row[4]),
                toBigIntegerNullable(row[5]),
                toBigDecimalNullable(row[6]),
                toBigIntegerNullable(row[7]),
                toBigIntegerNullable(row[8]),
                toLong(row[9]),
                toLong(row[10]),
                toLong(row[11]),
                toLong(row[12]),
                toLong(row[13])
        );
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

    private BigInteger toBigIntegerNullable(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigInteger integer) {
            return integer;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toBigInteger();
        }
        if (value instanceof Number number) {
            return BigInteger.valueOf(number.longValue());
        }
        return new BigInteger(value.toString());
    }

    private BigDecimal toBigDecimalNullable(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof BigInteger integer) {
            return new BigDecimal(integer);
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }
}
