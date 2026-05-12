package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.comparison.TerritoryComparison;
import com.itesm.domain.repository.ComparisonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ComparisonRepositoryImpl implements ComparisonRepository {

    @Inject
    EntityManager em;

    @Override
    public List<TerritoryComparison> compareStates(Integer periodId, List<Integer> stateIds) {
        String sql = """
                SELECT
                    s.id,
                    s.name,
                    'state' AS type,
                    p.id AS period_id,
                    p.period_year,
                    si.total_population,
                    si.percentage_over_60,
                    si.healthcare_access_deficiency,
                    si.total_poverty_population,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(staff.total_nurses, 0) AS total_nurses,
                    COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
                FROM state_indicators si
                JOIN states s ON s.id = si.state_id
                JOIN periods p ON p.id = si.period_id

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
                        SUM(CASE WHEN it.name = 'total_consultorios' THEN huid.quantity ELSE 0 END) AS total_consulting_rooms,
                        SUM(CASE WHEN it.name = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END) AS total_hospital_beds
                    FROM municipalities m
                    JOIN health_units hu ON hu.municipality_id = m.id
                    JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                    JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                    WHERE hui.period_id = :periodId
                    GROUP BY m.state_id
                ) infra ON infra.state_id = s.id

                WHERE si.period_id = :periodId
                AND s.id IN (:stateIds)
                ORDER BY s.name ASC
                """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("stateIds", stateIds);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapRowToTerritoryComparison)
                .collect(Collectors.toList());
    }

    @Override
    public List<TerritoryComparison> compareMunicipalities(Integer periodId, List<Integer> municipalityIds) {
        String sql = """
                SELECT
                    m.id,
                    m.name,
                    'municipality' AS type,
                    p.id AS period_id,
                    p.period_year,
                    mi.total_population,
                    mi.percentage_over_60,
                    mi.healthcare_access_deficiency,
                    mi.total_poverty_population,
                    COALESCE(units.total_health_units, 0) AS total_health_units,
                    COALESCE(staff.total_doctors, 0) AS total_doctors,
                    COALESCE(staff.total_nurses, 0) AS total_nurses,
                    COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                    COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
                FROM municipality_indicators mi
                JOIN municipalities m ON m.id = mi.municipality_id
                JOIN periods p ON p.id = mi.period_id

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
                        SUM(CASE WHEN it.name = 'total_consultorios' THEN huid.quantity ELSE 0 END) AS total_consulting_rooms,
                        SUM(CASE WHEN it.name = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END) AS total_hospital_beds
                    FROM health_units hu
                    JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                    JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                    JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                    WHERE hui.period_id = :periodId
                    GROUP BY hu.municipality_id
                ) infra ON infra.municipality_id = m.id

                WHERE mi.period_id = :periodId
                AND m.id IN (:municipalityIds)
                ORDER BY m.name ASC
                """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("municipalityIds", municipalityIds);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapRowToTerritoryComparison)
                .collect(Collectors.toList());
    }

    @Override
    public List<TerritoryComparison> compareMunicipalitiesByCodes(Integer periodId, List<String> municipalityCodes) {
        String sql = """
            SELECT
                m.id,
                m.name,
                'municipality' AS type,
                p.id AS period_id,
                p.period_year,
                mi.total_population,
                mi.percentage_over_60,
                mi.healthcare_access_deficiency,
                mi.total_poverty_population,
                COALESCE(units.total_health_units, 0) AS total_health_units,
                COALESCE(staff.total_doctors, 0) AS total_doctors,
                COALESCE(staff.total_nurses, 0) AS total_nurses,
                COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
            FROM municipality_indicators mi
            JOIN municipalities m ON m.id = mi.municipality_id
            JOIN periods p ON p.id = mi.period_id

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
                    SUM(CASE WHEN it.name = 'total_consultorios' THEN huid.quantity ELSE 0 END) AS total_consulting_rooms,
                    SUM(CASE WHEN it.name = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END) AS total_hospital_beds
                FROM health_units hu
                JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                WHERE hui.period_id = :periodId
                GROUP BY hu.municipality_id
            ) infra ON infra.municipality_id = m.id

            WHERE mi.period_id = :periodId
            AND m.inegi_code IN (:municipalityCodes)
            ORDER BY m.name ASC
            """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("municipalityCodes", municipalityCodes);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapRowToTerritoryComparison)
                .collect(Collectors.toList());
    }

    @Override
    public List<TerritoryComparison> compareStatesByCodes(Integer periodId, List<String> stateCodes) {
        String sql = """
            SELECT
                s.id,
                s.name,
                'state' AS type,
                p.id AS period_id,
                p.period_year,
                si.total_population,
                si.percentage_over_60,
                si.healthcare_access_deficiency,
                si.total_poverty_population,
                COALESCE(units.total_health_units, 0) AS total_health_units,
                COALESCE(staff.total_doctors, 0) AS total_doctors,
                COALESCE(staff.total_nurses, 0) AS total_nurses,
                COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                COALESCE(infra.total_hospital_beds, 0) AS total_hospital_beds
            FROM state_indicators si
            JOIN states s ON s.id = si.state_id
            JOIN periods p ON p.id = si.period_id

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
                    SUM(CASE WHEN it.name = 'total_consultorios' THEN huid.quantity ELSE 0 END) AS total_consulting_rooms,
                    SUM(CASE WHEN it.name = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END) AS total_hospital_beds
                FROM municipalities m
                JOIN health_units hu ON hu.municipality_id = m.id
                JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                WHERE hui.period_id = :periodId
                GROUP BY m.state_id
            ) infra ON infra.state_id = s.id

            WHERE si.period_id = :periodId
            AND s.inegi_code IN (:stateCodes)
            ORDER BY s.name ASC
            """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodId", periodId);
        query.setParameterList("stateCodes", stateCodes);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapRowToTerritoryComparison)
                .collect(Collectors.toList());
    }

    private TerritoryComparison mapRowToTerritoryComparison(Object[] row) {
        return new TerritoryComparison(
                toInteger(row[0]),
                (String) row[1],
                (String) row[2],
                toInteger(row[3]),
                toInteger(row[4]),
                toBigInteger(row[5]),
                toBigDecimal(row[6]),
                toBigInteger(row[7]),
                toBigInteger(row[8]),
                toLong(row[9]),
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