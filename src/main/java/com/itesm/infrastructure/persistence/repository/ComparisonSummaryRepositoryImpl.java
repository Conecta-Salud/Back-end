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
                .setParameter("periodId", periodId)
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
                GROUP BY
                    s.id,
                    s.inegi_code,
                    s.name,
                    p.id,
                    p.period_year,
                    units.total_hospitals
                """.formatted(
                indicatorValue("total_population"),
                indicatorValue("percentage_over_60"),
                indicatorValue("total_poverty_population"),
                indicatorValue("health_establishments"),
                indicatorValue("total_doctors"),
                indicatorValue("hospital_beds"),
                indicatorValue("doctors_per_1000"),
                indicatorValue("beds_per_1000")
        );

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
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
                GROUP BY
                    m.id,
                    m.inegi_code,
                    m.name,
                    s.name,
                    p.id,
                    p.period_year,
                    units.total_hospitals
                """.formatted(
                indicatorValue("total_population"),
                indicatorValue("percentage_over_60"),
                indicatorValue("total_poverty_population"),
                indicatorValue("health_establishments"),
                indicatorValue("total_doctors"),
                indicatorValue("hospital_beds"),
                indicatorValue("doctors_per_1000"),
                indicatorValue("beds_per_1000")
        );

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("municipalityCodes", municipalityCodes);

        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapComparisonRawItem)
                .sorted((a, b) -> Integer.compare(
                        indexOfCode(municipalityCodes, a.getTerritory().getCode()),
                        indexOfCode(municipalityCodes, b.getTerritory().getCode())
                ))
                .toList();
    }

    private String indicatorValue(String indicatorCode) {
        // Devuelve null cuando el indicador no existe o no esta disponible; asi la
        // capa de aplicacion puede responder value=null sin convertirlo en error.
        return "MAX(CASE WHEN i.code = '%s' AND COALESCE(da.is_available, 1) = 1 "
                + "AND COALESCE(da.availability_status, tiv.availability_status) NOT IN ('not_available', 'not_applicable') "
                + "THEN tiv.value END)".formatted(indicatorCode);
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

    private Long toLongNullable(Object value) {
        if (value == null) {
            return null;
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
