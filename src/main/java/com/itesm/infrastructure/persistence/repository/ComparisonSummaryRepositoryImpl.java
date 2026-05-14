package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.comparison.summary.ComparisonPeriod;
import com.itesm.domain.models.comparison.summary.ComparisonRawItem;
import com.itesm.domain.models.comparison.summary.ComparisonTerritory;
import com.itesm.domain.repository.ComparisonSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class ComparisonSummaryRepositoryImpl implements ComparisonSummaryRepository {

    @Inject
    EntityManager em;

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

    private int indexOfCode(List<String> requestedCodes, String code) {
        int index = requestedCodes.indexOf(code);
        return index >= 0 ? index : Integer.MAX_VALUE;
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
                    si.total_population AS total_population,
                    si.percentage_over_60 AS percentage_over_60,
                    si.total_poverty_population AS total_poverty_population,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(units.total_hospitals, 0) AS total_hospitals,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
                FROM states s
                JOIN periods p
                    ON p.id = :periodId
                JOIN state_indicators si
                    ON si.state_id = s.id
                   AND si.period_id = p.id
                
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        COUNT(DISTINCT hu.id) AS total_health_units,
                        COUNT(DISTINCT CASE
                            WHEN et.name = 'DE HOSPITALIZACION'
                              OR UPPER(mut.name) LIKE '%HOSPITAL%'
                            THEN hu.id
                            ELSE NULL
                        END) AS total_hospitals
                    FROM municipalities m
                    JOIN health_units hu
                        ON hu.municipality_id = m.id
                    JOIN establishment_types et
                        ON et.id = hu.establishment_type_id
                    JOIN medical_unit_types mut
                        ON mut.id = hu.medical_unit_type_id
                    GROUP BY m.state_id
                ) units
                    ON units.state_id = s.id
                
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        SUM(hus.total_doctors) AS total_doctors
                    FROM municipalities m
                    JOIN health_units hu
                        ON hu.municipality_id = m.id
                    JOIN health_unit_staff hus
                        ON hus.health_unit_id = hu.id
                       AND hus.period_id = :periodId
                    GROUP BY m.state_id
                ) staff
                    ON staff.state_id = s.id
                
                LEFT JOIN (
                    SELECT
                        m.state_id,
                        SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END
                        ) AS total_hospital_beds
                    FROM municipalities m
                    JOIN health_units hu
                        ON hu.municipality_id = m.id
                    JOIN health_unit_infrastructure hui
                        ON hui.health_unit_id = hu.id
                       AND hui.period_id = :periodId
                    JOIN health_unit_infrastructure_details huid
                        ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it
                        ON it.id = huid.infrastructure_type_id
                    GROUP BY m.state_id
                ) infra
                    ON infra.state_id = s.id
                
                WHERE s.inegi_code IN (:stateCodes)
                """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("stateCodes", stateCodes);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
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
                    mi.total_population AS total_population,
                    mi.percentage_over_60 AS percentage_over_60,
                    mi.total_poverty_population AS total_poverty_population,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(units.total_hospitals, 0) AS total_hospitals,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
                FROM municipalities m
                JOIN states s
                    ON s.id = m.state_id
                JOIN periods p
                    ON p.id = :periodId
                JOIN municipality_indicators mi
                    ON mi.municipality_id = m.id
                   AND mi.period_id = p.id
                
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        COUNT(DISTINCT hu.id) AS total_health_units,
                        COUNT(DISTINCT CASE
                            WHEN et.name = 'DE HOSPITALIZACION'
                              OR UPPER(mut.name) LIKE '%HOSPITAL%'
                            THEN hu.id
                            ELSE NULL
                        END) AS total_hospitals
                    FROM health_units hu
                    JOIN establishment_types et
                        ON et.id = hu.establishment_type_id
                    JOIN medical_unit_types mut
                        ON mut.id = hu.medical_unit_type_id
                    GROUP BY hu.municipality_id
                ) units
                    ON units.municipality_id = m.id
                
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        SUM(hus.total_doctors) AS total_doctors
                    FROM health_units hu
                    JOIN health_unit_staff hus
                        ON hus.health_unit_id = hu.id
                       AND hus.period_id = :periodId
                    GROUP BY hu.municipality_id
                ) staff
                    ON staff.municipality_id = m.id
                
                LEFT JOIN (
                    SELECT
                        hu.municipality_id,
                        SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END
                        ) AS total_hospital_beds
                    FROM health_units hu
                    JOIN health_unit_infrastructure hui
                        ON hui.health_unit_id = hu.id
                       AND hui.period_id = :periodId
                    JOIN health_unit_infrastructure_details huid
                        ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it
                        ON it.id = huid.infrastructure_type_id
                    GROUP BY hu.municipality_id
                ) infra
                    ON infra.municipality_id = m.id
                
                WHERE m.inegi_code IN (:municipalityCodes)
                """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("municipalityCodes", municipalityCodes);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapComparisonRawItem)
                .sorted((a, b) -> Integer.compare(
                        indexOfCode(municipalityCodes, a.getTerritory().getCode()),
                        indexOfCode(municipalityCodes, b.getTerritory().getCode())
                ))
                .toList();
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
                toBigInteger(row[7]),
                toBigDecimal(row[8]),
                toBigInteger(row[9]),
                toLong(row[10]),
                toLong(row[11]),
                toLong(row[12]),
                toLong(row[13])
        );
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Short) return ((Short) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
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
