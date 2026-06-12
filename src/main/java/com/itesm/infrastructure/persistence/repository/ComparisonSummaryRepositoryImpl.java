package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.comparison.summary.ComparisonPeriod;
import com.itesm.domain.models.comparison.summary.ComparisonRawItem;
import com.itesm.domain.models.comparison.summary.ComparisonTerritory;
import com.itesm.domain.repository.ComparisonSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class ComparisonSummaryRepositoryImpl implements ComparisonSummaryRepository {

    private static final String PERIOD_ID_PARAMETER = "periodId";

    // Arma la base de datos para comparacion avanzada. Los indicadores agregados
    // vienen de territory_indicator_values; hospitales se calcula operativo porque
    // no existe como indicador materializado oficial.
    private final EntityManager em;

    public ComparisonSummaryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean existsPeriodById(Integer periodId) {
        Long count = em.createQuery(
                        "SELECT COUNT(p) FROM PeriodEntity p WHERE p.id = :periodId",
                        Long.class
                )
                .setParameter(PERIOD_ID_PARAMETER, periodId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<ComparisonRawItem> findStateComparisonItemsByCodes(
            Integer periodId,
            List<String> stateCodes
    ) {
        String sql = """
                SELECT
                    s.id AS territory_id,
                    s.inegi_code AS code,
                    s.name AS name,
                    NULL AS parent_name,
                    'state' AS type,
                    p.id AS period_id,
                    p.period_year AS period_year,
                    %s AS total_population,
                    %s AS percentage_over_60,
                    %s AS total_poverty_population,
                    %s AS total_health_units,
                    COALESCE(units.total_hospitals, 0) AS total_hospitals,
                    %s AS total_doctors,
                    %s AS total_hospital_beds,
                    %s AS doctors_per_1000,
                    %s AS beds_per_1000
                FROM states s
                JOIN periods p ON p.id = :periodId
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        COUNT(DISTINCT CASE
                            WHEN et.name = 'DE HOSPITALIZACION'
                              OR UPPER(mut.name) LIKE '%%HOSPITAL%%'
                            THEN hu.id
                            ELSE NULL
                        END) AS total_hospitals
                    FROM municipalities m
                    JOIN health_units hu ON hu.municipality_id = m.id
                    JOIN establishment_types et ON et.id = hu.establishment_type_id
                    JOIN medical_unit_types mut ON mut.id = hu.medical_unit_type_id
                    GROUP BY m.state_id
                ) units ON units.state_id = s.id
                WHERE s.inegi_code IN (:stateCodes)
                """.formatted(
                stateIndicatorValue("total_population"),
                stateIndicatorValue("percentage_over_60"),
                stateIndicatorValue("total_poverty_population"),
                stateIndicatorValue("health_establishments"),
                stateIndicatorValue("total_doctors"),
                stateIndicatorValue("hospital_beds"),
                stateIndicatorValue("doctors_per_1000"),
                stateIndicatorValue("beds_per_1000")
        );

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter(PERIOD_ID_PARAMETER, periodId);
        query.setParameterList("stateCodes", stateCodes);

        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapComparisonRawItem)
                .sorted((a, b) -> Integer.compare(
                        indexOfCode(stateCodes, a.getTerritory().getCode()),
                        indexOfCode(stateCodes, b.getTerritory().getCode())
                ))
                .toList();
    }

    @Override
    public List<ComparisonRawItem> findMunicipalityComparisonItemsByCodes(
            Integer periodId,
            List<String> municipalityCodes
    ) {
        String sql = """
                SELECT
                    m.id AS territory_id,
                    m.inegi_code AS code,
                    m.name AS name,
                    s.name AS parent_name,
                    'municipality' AS type,
                    p.id AS period_id,
                    p.period_year AS period_year,
                    %s AS total_population,
                    %s AS percentage_over_60,
                    %s AS total_poverty_population,
                    %s AS total_health_units,
                    COALESCE(units.total_hospitals, 0) AS total_hospitals,
                    %s AS total_doctors,
                    %s AS total_hospital_beds,
                    %s AS doctors_per_1000,
                    %s AS beds_per_1000
                FROM municipalities m
                JOIN states s ON s.id = m.state_id
                JOIN periods p ON p.id = :periodId
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        COUNT(DISTINCT CASE
                            WHEN et.name = 'DE HOSPITALIZACION'
                              OR UPPER(mut.name) LIKE '%%HOSPITAL%%'
                            THEN hu.id
                            ELSE NULL
                        END) AS total_hospitals
                    FROM health_units hu
                    JOIN establishment_types et ON et.id = hu.establishment_type_id
                    JOIN medical_unit_types mut ON mut.id = hu.medical_unit_type_id
                    GROUP BY hu.municipality_id
                ) units ON units.municipality_id = m.id
                WHERE m.inegi_code IN (:municipalityCodes)
                """.formatted(
                municipalityIndicatorValue("total_population"),
                municipalityIndicatorValue("percentage_over_60"),
                municipalityIndicatorValue("total_poverty_population"),
                municipalityIndicatorValue("health_establishments"),
                municipalityIndicatorValue("total_doctors"),
                municipalityIndicatorValue("hospital_beds"),
                municipalityIndicatorValue("doctors_per_1000"),
                municipalityIndicatorValue("beds_per_1000")
        );

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter(PERIOD_ID_PARAMETER, periodId);
        query.setParameterList("municipalityCodes", municipalityCodes);

        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapComparisonRawItem)
                .sorted((a, b) -> Integer.compare(
                        indexOfCode(municipalityCodes, a.getTerritory().getCode()),
                        indexOfCode(municipalityCodes, b.getTerritory().getCode())
                ))
                .toList();
    }

    private String stateIndicatorValue(String indicatorCode) {
        return """
            (
                SELECT tiv.value
                FROM territory_indicator_values tiv
                JOIN indicators ind ON ind.id = tiv.indicator_id
                WHERE ind.code = '%s'
                  AND tiv.territory_level = 'state'
                  AND tiv.state_id = s.id
                  AND tiv.analysis_year = p.period_year
                  AND tiv.availability_status NOT IN ('not_available', 'not_applicable')
                LIMIT 1
            )
            """.formatted(indicatorCode);
    }

    private String municipalityIndicatorValue(String indicatorCode) {
        return """
            (
                SELECT tiv.value
                FROM territory_indicator_values tiv
                JOIN indicators ind ON ind.id = tiv.indicator_id
                WHERE ind.code = '%s'
                  AND tiv.territory_level = 'municipality'
                  AND tiv.municipality_id = m.id
                  AND tiv.analysis_year = p.period_year
                  AND tiv.availability_status NOT IN ('not_available', 'not_applicable')
                LIMIT 1
            )
            """.formatted(indicatorCode);
    }

    private int indexOfCode(List<String> requestedCodes, String code) {
        int index = requestedCodes.indexOf(code);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }

    private ComparisonRawItem mapComparisonRawItem(Object[] row) {
        return new ComparisonRawItem(
                new ComparisonTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        row[3] != null ? row[3].toString() : null,
                        (String) row[4]
                ),
                new ComparisonPeriod(
                        toInteger(row[5]),
                        toInteger(row[6])
                ),
                toBigIntegerNullable(row[7]),
                toBigDecimalNullable(row[8]),
                toBigIntegerNullable(row[9]),
                toLongNullable(row[10]),
                toLong(row[11]),
                toLongNullable(row[12]),
                toLongNullable(row[13]),
                toBigDecimalNullable(row[14]),
                toBigDecimalNullable(row[15])
        );
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private Long toLongNullable(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
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
