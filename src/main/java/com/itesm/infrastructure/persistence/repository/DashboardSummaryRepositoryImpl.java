package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.country.CountryHealthcareAccessDeficiencyMetrics;
import com.itesm.domain.models.dashboard.summary.country.CountryHospitalBedsMetrics;
import com.itesm.domain.models.dashboard.summary.country.CountryMedicalCoverageMetrics;
import com.itesm.domain.models.dashboard.summary.municipality.MunicipalityHealthcareAccessDeficiencyMetrics;
import com.itesm.domain.models.dashboard.summary.municipality.MunicipalityHospitalBedsMetrics;
import com.itesm.domain.models.dashboard.summary.municipality.MunicipalityMedicalCoverageMetrics;
import com.itesm.domain.models.dashboard.summary.state.StateHealthcareAccessDeficiencyMetrics;
import com.itesm.domain.models.dashboard.summary.state.StateHospitalBedsMetrics;
import com.itesm.domain.models.dashboard.summary.state.StateMedicalCoverageMetrics;
import com.itesm.domain.repository.DashboardSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class DashboardSummaryRepositoryImpl implements DashboardSummaryRepository {

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

    private DashboardChartDataPoint mapSimplePiePoint(Object[] row) {
        return new DashboardChartDataPoint(
                row[0] != null ? prettifyLabel(row[0].toString()) : "Not specified",
                null,
                toBigDecimal(row[1]),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Map.of()
        );
    }

    private String prettifyLabel(String value) {
        if (value == null || value.isBlank()) {
            return "Not specified";
        }

        return switch (value) {
            case "total_camas_hospitalizacion" -> "Hospital beds";
            case "total_consultorios" -> "Consulting rooms";
            case "medicos_generales" -> "General practitioners";
            case "pediatras" -> "Pediatricians";
            case "ginecoobstetras" -> "Gynecologists and obstetricians";
            case "cirujanos" -> "Surgeons";
            case "geriatras" -> "Geriatricians";
            case "oftalmologos" -> "Ophthalmologists";
            case "traumatologos" -> "Traumatologists";
            case "dermatologos" -> "Dermatologists";
            case "odontologos" -> "Dentists";
            case "cardiologos" -> "Cardiologists";
            case "urgenciologos" -> "Emergency physicians";
            case "internistas" -> "Internists";
            case "anestesiologos" -> "Anesthesiologists";
            default -> value
                    .replace("_", " ")
                    .trim();
        };
    }

    // =========================== COBERTURA MÉDICA ===========================
    // =========================== País ===========================
    @Override
    public Optional<CountryMedicalCoverageMetrics> findCountryMedicalCoverageMetrics(Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
                WITH state_coverage AS (
                    SELECT
                        s.id AS state_id,
                        s.name AS state_name,
                        si.total_population AS population,
                        COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                        ROUND(
                            (COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000,
                            2
                        ) AS doctors_per_1000
                    FROM states s
                    JOIN state_indicators si 
                        ON si.state_id = s.id
                       AND si.period_id = :periodId
                    LEFT JOIN municipalities m 
                        ON m.state_id = s.id
                    LEFT JOIN health_units hu 
                        ON hu.municipality_id = m.id
                    LEFT JOIN health_unit_staff hus 
                        ON hus.health_unit_id = hu.id
                       AND hus.period_id = :periodId
                    GROUP BY s.id, s.name, si.total_population
                )
                SELECT
                    p.id AS period_id,
                    p.period_year AS period_year,
                    COALESCE(SUM(sc.population), 0) AS total_population,
                    COALESCE(SUM(sc.doctors), 0) AS total_doctors,
                    ROUND(
                        (COALESCE(SUM(sc.doctors), 0) / NULLIF(SUM(sc.population), 0)) * 1000,
                        2
                    ) AS doctors_per_1000,
                    COALESCE(SUM(CASE WHEN sc.doctors_per_1000 < 1.0 THEN 1 ELSE 0 END), 0) AS critical_states,
                    ROUND(COALESCE(AVG(sc.doctors_per_1000), 0), 2) AS average_state_medical_coverage
                FROM periods p
                LEFT JOIN state_coverage sc ON 1 = 1
                WHERE p.id = :periodId
                GROUP BY p.id, p.period_year
                """)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapCountryMedicalCoverageMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findCountryMedicalCoverageRanking(Integer periodId, Integer limit) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT
                    ROW_NUMBER() OVER (
                        ORDER BY ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000, 2) ASC
                    ) AS ranking_position,
                    s.id AS state_id,
                    s.inegi_code AS code,
                    s.name AS name,
                    si.total_population AS population,
                    COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                    ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000, 2) AS doctors_per_1000
                FROM states s
                JOIN state_indicators si 
                    ON si.state_id = s.id
                   AND si.period_id = :periodId
                LEFT JOIN municipalities m 
                    ON m.state_id = s.id
                LEFT JOIN health_units hu 
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus 
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                GROUP BY s.id, s.inegi_code, s.name, si.total_population
                ORDER BY doctors_per_1000 ASC
                LIMIT :limit
                """)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapRankingRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardChartDataPoint> findCountryMedicalCoverageMainChart(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT
                    s.name AS label,
                    s.inegi_code AS code,
                    ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000, 2) AS value,
                    si.total_population AS population,
                    COALESCE(SUM(hus.total_doctors), 0) AS doctors
                FROM states s
                JOIN state_indicators si 
                    ON si.state_id = s.id
                   AND si.period_id = :periodId
                LEFT JOIN municipalities m 
                    ON m.state_id = s.id
                LEFT JOIN health_units hu 
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus 
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                GROUP BY s.id, s.inegi_code, s.name, si.total_population
                ORDER BY value ASC
                """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapChartDataPoint)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardChartDataPoint> findCountryMedicalCoverageSecondaryChart(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT
                    s.name AS label,
                    s.inegi_code AS code,
                    COALESCE(SUM(hus.total_doctors), 0) AS value
                FROM states s
                JOIN state_indicators si 
                    ON si.state_id = s.id
                   AND si.period_id = :periodId
                LEFT JOIN municipalities m 
                    ON m.state_id = s.id
                LEFT JOIN health_units hu 
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus 
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                GROUP BY s.id, s.inegi_code, s.name
                ORDER BY value DESC
                """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        (String) row[0],
                        (String) row[1],
                        toBigDecimal(row[2]),
                        null,
                        toLong(row[2]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardChartDataPoint> findCountrySpecialtiesDistribution(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                sp.name AS label,
                COALESCE(SUM(huss.quantity), 0) AS value
            FROM health_unit_staff_specialties huss
            JOIN specialties sp
                ON sp.id = huss.specialty_id
            JOIN health_unit_staff hus
                ON hus.id = huss.health_unit_staff_id
               AND hus.period_id = :periodId
            GROUP BY sp.id, sp.name
            HAVING value > 0
            ORDER BY value DESC
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapSimplePiePoint)
                .toList();
    }

    private CountryMedicalCoverageMetrics mapCountryMedicalCoverageMetrics(Object[] row) {
        return new CountryMedicalCoverageMetrics(
                new DashboardPeriod(
                        toInteger(row[0]),
                        toInteger(row[1])
                ),
                toBigInteger(row[2]),
                toLong(row[3]),
                toBigDecimal(row[4]),
                toLong(row[5]),
                toBigDecimal(row[6])
        );
    }

    private DashboardRankingRow mapRankingRow(Object[] row) {
        BigDecimal value = toBigDecimal(row[6]);
        String[] classification = classifyMedicalCoverage(value);

        return new DashboardRankingRow(
                String.valueOf(toInteger(row[1])),
                toInteger(row[0]),
                (String) row[2],
                (String) row[3],
                toBigInteger(row[4]),
                toLong(row[5]),
                null,
                null,
                value,
                classification[0],
                classification[1],
                Map.of()
        );
    }

    private DashboardChartDataPoint mapChartDataPoint(Object[] row) {
        BigDecimal value = toBigDecimal(row[2]);
        String[] classification = classifyMedicalCoverage(value);

        return new DashboardChartDataPoint(
                (String) row[0],
                (String) row[1],
                value,
                toBigInteger(row[3]),
                toLong(row[4]),
                null,
                null,
                value,
                classification[0],
                classification[1],
                Map.of()
        );
    }

    private String[] classifyMedicalCoverage(BigDecimal value) {
        if (value == null) {
            return new String[]{"no_data", "neutral"};
        }

        double number = value.doubleValue();

        if (number >= 2.7) {
            return new String[]{"good", "green"};
        }

        if (number >= 1.0) {
            return new String[]{"risk", "yellow"};
        }

        return new String[]{"critical", "red"};
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
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof BigInteger) return new BigDecimal((BigInteger) value);
        return new BigDecimal(value.toString());
    }

    // =========================== Estado ===========================
    @Override
    public boolean existsStateById(Integer stateId) {
        Long count = em.createQuery(
                        "SELECT COUNT(s) FROM StateEntity s WHERE s.id = :stateId",
                        Long.class
                )
                .setParameter("stateId", stateId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public Optional<StateMedicalCoverageMetrics> findStateMedicalCoverageMetrics(Integer stateId, Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
            WITH municipality_coverage AS (
                SELECT
                    m.id AS municipality_id,
                    m.name AS municipality_name,
                    mi.total_population AS population,
                    COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                    ROUND(
                        (COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000,
                        2
                    ) AS doctors_per_1000
                FROM municipalities m
                JOIN municipality_indicators mi
                    ON mi.municipality_id = m.id
                   AND mi.period_id = :periodId
                LEFT JOIN health_units hu
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                WHERE m.state_id = :stateId
                GROUP BY m.id, m.name, mi.total_population
            )
            SELECT
                s.id AS state_id,
                s.inegi_code AS state_code,
                s.name AS state_name,
                p.id AS period_id,
                p.period_year AS period_year,
                si.total_population AS total_population,
                COALESCE(SUM(mc.doctors), 0) AS total_doctors,
                ROUND(
                    (COALESCE(SUM(mc.doctors), 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS doctors_per_1000,
                COALESCE(SUM(CASE WHEN mc.doctors_per_1000 < 1.0 THEN 1 ELSE 0 END), 0) AS critical_municipalities,
                ROUND(COALESCE(AVG(mc.doctors_per_1000), 0), 2) AS average_municipal_coverage
            FROM states s
            JOIN periods p
                ON p.id = :periodId
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = p.id
            LEFT JOIN municipality_coverage mc
                ON 1 = 1
            WHERE s.id = :stateId
            GROUP BY s.id, s.inegi_code, s.name, p.id, p.period_year, si.total_population
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapStateMedicalCoverageMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findStateMedicalCoverageRanking(Integer stateId, Integer periodId, Integer limit) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000, 2) ASC
                ) AS ranking_position,
                m.id AS municipality_id,
                m.inegi_code AS code,
                m.name AS name,
                mi.total_population AS population,
                COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000, 2) AS doctors_per_1000
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE m.state_id = :stateId
            GROUP BY m.id, m.inegi_code, m.name, mi.total_population
            ORDER BY doctors_per_1000 ASC
            LIMIT :limit
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateMedicalCoverageMainChart(Integer stateId, Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                m.name AS label,
                m.inegi_code AS code,
                ROUND((COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000, 2) AS value,
                mi.total_population AS population,
                COALESCE(SUM(hus.total_doctors), 0) AS doctors
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE m.state_id = :stateId
            GROUP BY m.id, m.inegi_code, m.name, mi.total_population
            ORDER BY value ASC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapChartDataPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateMedicalCoverageSecondaryChart(Integer stateId, Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                m.name AS label,
                m.inegi_code AS code,
                COALESCE(SUM(hus.total_doctors), 0) AS value
            FROM municipalities m
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE m.state_id = :stateId
            GROUP BY m.id, m.inegi_code, m.name
            ORDER BY value DESC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        (String) row[0],
                        (String) row[1],
                        toBigDecimal(row[2]),
                        null,
                        toLong(row[2]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateSpecialtiesDistribution(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                sp.name AS label,
                COALESCE(SUM(huss.quantity), 0) AS value
            FROM health_unit_staff_specialties huss
            JOIN specialties sp
                ON sp.id = huss.specialty_id
            JOIN health_unit_staff hus
                ON hus.id = huss.health_unit_staff_id
               AND hus.period_id = :periodId
            JOIN health_units hu
                ON hu.id = hus.health_unit_id
            JOIN municipalities m
                ON m.id = hu.municipality_id
            WHERE m.state_id = :stateId
            GROUP BY sp.id, sp.name
            HAVING value > 0
            ORDER BY value DESC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapSimplePiePoint)
                .toList();
    }

    private StateMedicalCoverageMetrics mapStateMedicalCoverageMetrics(Object[] row) {
        return new StateMedicalCoverageMetrics(
                new DashboardTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        "state"
                ),
                new DashboardPeriod(
                        toInteger(row[3]),
                        toInteger(row[4])
                ),
                toBigInteger(row[5]),
                toLong(row[6]),
                toBigDecimal(row[7]),
                toLong(row[8]),
                toBigDecimal(row[9])
        );
    }

    // =========================== Municipio ===========================

    @Override
    public boolean existsMunicipalityById(Integer municipalityId) {
        Long count = em.createQuery(
                        "SELECT COUNT(m) FROM MunicipalityEntity m WHERE m.id = :municipalityId",
                        Long.class
                )
                .setParameter("municipalityId", municipalityId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public Optional<MunicipalityMedicalCoverageMetrics> findMunicipalityMedicalCoverageMetrics(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> result = em.createNativeQuery("""
            SELECT
                m.id AS municipality_id,
                m.inegi_code AS municipality_code,
                m.name AS municipality_name,
                p.id AS period_id,
                p.period_year AS period_year,
                mi.total_population AS total_population,
                COALESCE(staff.total_doctors, 0) AS total_doctors,
                ROUND(
                    (COALESCE(staff.total_doctors, 0) / NULLIF(mi.total_population, 0)) * 1000,
                    2
                ) AS doctors_per_1000,
                COALESCE(infra.total_consulting_rooms, 0) AS total_consulting_rooms,
                COALESCE(hospitals.total_hospitals, 0) AS total_hospitals
            FROM municipalities m
            JOIN periods p
                ON p.id = :periodId
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = p.id
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
                        WHEN it.name = 'total_consultorios'
                        THEN huid.quantity ELSE 0 END
                    ) AS total_consulting_rooms
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
            LEFT JOIN (
                SELECT
                    hu.municipality_id,
                    COUNT(DISTINCT hu.id) AS total_hospitals
                FROM health_units hu
                JOIN establishment_types et
                    ON et.id = hu.establishment_type_id
                JOIN medical_unit_types mut
                    ON mut.id = hu.medical_unit_type_id
                WHERE et.name = 'DE HOSPITALIZACION'
                   OR UPPER(mut.name) LIKE '%HOSPITAL%'
                GROUP BY hu.municipality_id
            ) hospitals
                ON hospitals.municipality_id = m.id
            WHERE m.id = :municipalityId
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapMunicipalityMedicalCoverageMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findMunicipalityMedicalCoverageRanking(
            Integer municipalityId,
            Integer periodId,
            Integer limit
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY COALESCE(hus.total_doctors, 0) ASC
                ) AS ranking_position,
                hu.id AS unit_id,
                hu.clues AS code,
                hu.name AS name,
                COALESCE(hus.total_doctors, 0) AS doctors,
                mut.name AS unit_type,
                hu.care_level AS care_level
            FROM health_units hu
            JOIN medical_unit_types mut
                ON mut.id = hu.medical_unit_type_id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE hu.municipality_id = :municipalityId
            ORDER BY doctors ASC, hu.name ASC
            LIMIT :limit
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapMunicipalityUnitRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityMedicalCoverageMainChart(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                hu.name AS label,
                hu.clues AS code,
                COALESCE(hus.total_doctors, 0) AS doctors,
                mut.name AS unit_type,
                hu.care_level AS care_level
            FROM health_units hu
            JOIN medical_unit_types mut
                ON mut.id = hu.medical_unit_type_id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE hu.municipality_id = :municipalityId
            ORDER BY doctors DESC, hu.name ASC
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        (String) row[0],
                        (String) row[1],
                        toBigDecimal(row[2]),
                        null,
                        toLong(row[2]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of(
                                "unitType", row[3] != null ? row[3].toString() : null,
                                "careLevel", row[4] != null ? row[4].toString() : null
                        )
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityMedicalCoverageSecondaryChart(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                hu.care_level AS label,
                COUNT(*) AS total_units
            FROM health_units hu
            WHERE hu.municipality_id = :municipalityId
            GROUP BY hu.care_level
            ORDER BY total_units DESC
            """)
                .setParameter("municipalityId", municipalityId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        row[0] != null ? row[0].toString() : "not_specified",
                        null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    private MunicipalityMedicalCoverageMetrics mapMunicipalityMedicalCoverageMetrics(Object[] row) {
        return new MunicipalityMedicalCoverageMetrics(
                new DashboardTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        "municipality"
                ),
                new DashboardPeriod(
                        toInteger(row[3]),
                        toInteger(row[4])
                ),
                toBigInteger(row[5]),
                toLong(row[6]),
                toBigDecimal(row[7]),
                toLong(row[8]),
                toLong(row[9])
        );
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalitySpecialtiesDistribution(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                sp.name AS label,
                COALESCE(SUM(huss.quantity), 0) AS value
            FROM health_unit_staff_specialties huss
            JOIN specialties sp
                ON sp.id = huss.specialty_id
            JOIN health_unit_staff hus
                ON hus.id = huss.health_unit_staff_id
               AND hus.period_id = :periodId
            JOIN health_units hu
                ON hu.id = hus.health_unit_id
            WHERE hu.municipality_id = :municipalityId
            GROUP BY sp.id, sp.name
            HAVING value > 0
            ORDER BY value DESC
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapSimplePiePoint)
                .toList();
    }

    private DashboardRankingRow mapMunicipalityUnitRankingRow(Object[] row) {
        return new DashboardRankingRow(
                String.valueOf(toInteger(row[1])),
                toInteger(row[0]),
                (String) row[2],
                (String) row[3],
                null,
                toLong(row[4]),
                null,
                null,
                toBigDecimal(row[4]),
                null,
                null,
                Map.of(
                        "unitType", row[5] != null ? row[5].toString() : null,
                        "careLevel", row[6] != null ? row[6].toString() : null
                )
        );
    }



    // =========================== INFRAESTRUCTURA HOSPITALARIA ===========================
    // =========================== País ===========================
    @Override
    public Optional<CountryHospitalBedsMetrics> findCountryHospitalBedsMetrics(Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
            WITH state_beds AS (
                SELECT
                    s.id AS state_id,
                    s.name AS state_name,
                    si.total_population AS population,
                    COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0
                    ) AS hospital_beds,
                    ROUND(
                        (COALESCE(SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END), 0) / NULLIF(si.total_population, 0)) * 1000,
                        2
                    ) AS beds_per_1000
                FROM states s
                JOIN state_indicators si
                    ON si.state_id = s.id
                   AND si.period_id = :periodId
                LEFT JOIN municipalities m
                    ON m.state_id = s.id
                LEFT JOIN health_units hu
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_infrastructure hui
                    ON hui.health_unit_id = hu.id
                   AND hui.period_id = :periodId
                LEFT JOIN health_unit_infrastructure_details huid
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it
                    ON it.id = huid.infrastructure_type_id
                GROUP BY s.id, s.name, si.total_population
            ),
            hospitals AS (
                SELECT
                    COUNT(DISTINCT hu.id) AS total_hospitals
                FROM health_units hu
                JOIN establishment_types et
                    ON et.id = hu.establishment_type_id
                JOIN medical_unit_types mut
                    ON mut.id = hu.medical_unit_type_id
                WHERE et.name = 'DE HOSPITALIZACION'
                   OR UPPER(mut.name) LIKE '%HOSPITAL%'
            )
            SELECT
                p.id AS period_id,
                p.period_year AS period_year,
                COALESCE(SUM(sb.population), 0) AS total_population,
                COALESCE(SUM(sb.hospital_beds), 0) AS total_hospital_beds,
                ROUND(
                    (COALESCE(SUM(sb.hospital_beds), 0) / NULLIF(SUM(sb.population), 0)) * 1000,
                    2
                ) AS hospital_beds_per_1000,
                COALESCE(SUM(CASE WHEN sb.beds_per_1000 < 3.0 THEN 1 ELSE 0 END), 0) AS states_with_hospital_deficit,
                COALESCE(h.total_hospitals, 0) AS total_hospitals,
                ROUND(
                    COALESCE(SUM(sb.hospital_beds), 0) / NULLIF(COALESCE(h.total_hospitals, 0), 0),
                    2
                ) AS average_beds_per_hospital
            FROM periods p
            LEFT JOIN state_beds sb ON 1 = 1
            LEFT JOIN hospitals h ON 1 = 1
            WHERE p.id = :periodId
            GROUP BY p.id, p.period_year, h.total_hospitals
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapCountryHospitalBedsMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findCountryHospitalBedsRanking(Integer periodId, Integer limit) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY ROUND(
                        (COALESCE(SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END), 0) / NULLIF(si.total_population, 0)) * 1000,
                        2
                    ) ASC
                ) AS ranking_position,
                s.id AS state_id,
                s.inegi_code AS code,
                s.name AS name,
                si.total_population AS population,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS hospital_beds,
                ROUND(
                    (COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS beds_per_1000
            FROM states s
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = :periodId
            LEFT JOIN municipalities m
                ON m.state_id = s.id
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            GROUP BY s.id, s.inegi_code, s.name, si.total_population
            ORDER BY beds_per_1000 ASC
            LIMIT :limit
            """)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapHospitalBedsRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHospitalBedsMainChart(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                s.name AS label,
                s.inegi_code AS code,
                ROUND(
                    (COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS value,
                si.total_population AS population,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS hospital_beds
            FROM states s
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = :periodId
            LEFT JOIN municipalities m
                ON m.state_id = s.id
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            GROUP BY s.id, s.inegi_code, s.name, si.total_population
            ORDER BY value ASC
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapHospitalBedsChartDataPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHospitalBedsSecondaryChart(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                et.name AS label,
                COUNT(hu.id) AS total_units
            FROM health_units hu
            JOIN establishment_types et
                ON et.id = hu.establishment_type_id
            GROUP BY et.id, et.name
            ORDER BY total_units DESC
            """)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        row[0] != null ? row[0].toString() : "not_specified",
                        null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    private CountryHospitalBedsMetrics mapCountryHospitalBedsMetrics(Object[] row) {
        return new CountryHospitalBedsMetrics(
                new DashboardPeriod(
                        toInteger(row[0]),
                        toInteger(row[1])
                ),
                toBigInteger(row[2]),
                toLong(row[3]),
                toBigDecimal(row[4]),
                toLong(row[5]),
                toLong(row[6]),
                toBigDecimal(row[7])
        );
    }

    private DashboardRankingRow mapHospitalBedsRankingRow(Object[] row) {
        BigDecimal value = toBigDecimal(row[6]);
        String[] classification = classifyHospitalBeds(value);

        return new DashboardRankingRow(
                String.valueOf(toInteger(row[1])),
                toInteger(row[0]),
                (String) row[2],
                (String) row[3],
                toBigInteger(row[4]),
                null,
                toLong(row[5]),
                null,
                value,
                classification[0],
                classification[1],
                Map.of()
        );
    }

    private DashboardChartDataPoint mapHospitalBedsChartDataPoint(Object[] row) {
        BigDecimal value = toBigDecimal(row[2]);
        String[] classification = classifyHospitalBeds(value);

        return new DashboardChartDataPoint(
                (String) row[0],
                (String) row[1],
                value,
                toBigInteger(row[3]),
                null,
                toLong(row[4]),
                null,
                null,
                classification[0],
                classification[1],
                Map.of()
        );
    }

    private String[] classifyHospitalBeds(BigDecimal value) {
        if (value == null) {
            return new String[]{"no_data", "neutral"};
        }

        double number = value.doubleValue();

        if (number >= 3.0) {
            return new String[]{"good", "green"};
        }

        if (number >= 1.0) {
            return new String[]{"risk", "yellow"};
        }

        return new String[]{"critical", "red"};
    }

    // =========================== Estado ===========================
    @Override
    public Optional<StateHospitalBedsMetrics> findStateHospitalBedsMetrics(Integer stateId, Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
            WITH municipality_beds AS (
                SELECT
                    m.id AS municipality_id,
                    m.name AS municipality_name,
                    mi.total_population AS population,
                    COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0
                    ) AS hospital_beds,
                    ROUND(
                        (COALESCE(SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END), 0) / NULLIF(mi.total_population, 0)) * 1000,
                        2
                    ) AS beds_per_1000
                FROM municipalities m
                JOIN municipality_indicators mi
                    ON mi.municipality_id = m.id
                   AND mi.period_id = :periodId
                LEFT JOIN health_units hu
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_infrastructure hui
                    ON hui.health_unit_id = hu.id
                   AND hui.period_id = :periodId
                LEFT JOIN health_unit_infrastructure_details huid
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it
                    ON it.id = huid.infrastructure_type_id
                WHERE m.state_id = :stateId
                GROUP BY m.id, m.name, mi.total_population
            ),
            state_infra AS (
                SELECT
                    m.state_id,
                    COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0
                    ) AS total_hospital_beds,
                    COALESCE(SUM(CASE
                        WHEN it.name = 'total_consultorios'
                        THEN huid.quantity ELSE 0 END), 0
                    ) AS total_consulting_rooms
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
                WHERE m.state_id = :stateId
                GROUP BY m.state_id
            ),
            state_hospitals AS (
                SELECT
                    m.state_id,
                    COUNT(DISTINCT hu.id) AS total_hospitals
                FROM municipalities m
                JOIN health_units hu
                    ON hu.municipality_id = m.id
                JOIN establishment_types et
                    ON et.id = hu.establishment_type_id
                JOIN medical_unit_types mut
                    ON mut.id = hu.medical_unit_type_id
                WHERE m.state_id = :stateId
                  AND (
                        et.name = 'DE HOSPITALIZACION'
                        OR UPPER(mut.name) LIKE '%HOSPITAL%'
                  )
                GROUP BY m.state_id
            )
            SELECT
                s.id AS state_id,
                s.inegi_code AS state_code,
                s.name AS state_name,
                p.id AS period_id,
                p.period_year AS period_year,
                si.total_population AS total_population,
                COALESCE(si2.total_hospital_beds, 0) AS total_hospital_beds,
                ROUND(
                    (COALESCE(si2.total_hospital_beds, 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS hospital_beds_per_1000,
                COALESCE(SUM(CASE WHEN mb.beds_per_1000 < 3.0 THEN 1 ELSE 0 END), 0) AS municipalities_with_hospital_deficit,
                COALESCE(sh.total_hospitals, 0) AS total_hospitals,
                COALESCE(si2.total_consulting_rooms, 0) AS total_consulting_rooms
            FROM states s
            JOIN periods p
                ON p.id = :periodId
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = p.id
            LEFT JOIN municipality_beds mb
                ON 1 = 1
            LEFT JOIN state_infra si2
                ON si2.state_id = s.id
            LEFT JOIN state_hospitals sh
                ON sh.state_id = s.id
            WHERE s.id = :stateId
            GROUP BY
                s.id,
                s.inegi_code,
                s.name,
                p.id,
                p.period_year,
                si.total_population,
                si2.total_hospital_beds,
                si2.total_consulting_rooms,
                sh.total_hospitals
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapStateHospitalBedsMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findStateHospitalBedsRanking(
            Integer stateId,
            Integer periodId,
            Integer limit
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY ROUND(
                        (COALESCE(SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END), 0) / NULLIF(mi.total_population, 0)) * 1000,
                        2
                    ) ASC
                ) AS ranking_position,
                m.id AS municipality_id,
                m.inegi_code AS code,
                m.name AS name,
                mi.total_population AS population,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS hospital_beds,
                ROUND(
                    (COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0) / NULLIF(mi.total_population, 0)) * 1000,
                    2
                ) AS beds_per_1000
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE m.state_id = :stateId
            GROUP BY m.id, m.inegi_code, m.name, mi.total_population
            ORDER BY beds_per_1000 ASC
            LIMIT :limit
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapHospitalBedsRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateHospitalBedsMainChart(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                m.name AS label,
                m.inegi_code AS code,
                ROUND(
                    (COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0) / NULLIF(mi.total_population, 0)) * 1000,
                    2
                ) AS value,
                mi.total_population AS population,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS hospital_beds
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE m.state_id = :stateId
            GROUP BY m.id, m.inegi_code, m.name, mi.total_population
            ORDER BY value ASC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapHospitalBedsChartDataPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateHospitalBedsSecondaryChart(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                et.name AS label,
                COUNT(hu.id) AS total_units
            FROM municipalities m
            JOIN health_units hu
                ON hu.municipality_id = m.id
            JOIN establishment_types et
                ON et.id = hu.establishment_type_id
            WHERE m.state_id = :stateId
            GROUP BY et.id, et.name
            ORDER BY total_units DESC
            """)
                .setParameter("stateId", stateId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        row[0] != null ? row[0].toString() : "not_specified",
                        null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateInfrastructureDistribution(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                it.name AS label,
                COALESCE(SUM(huid.quantity), 0) AS value
            FROM health_unit_infrastructure hui
            JOIN health_units hu
                ON hu.id = hui.health_unit_id
            JOIN municipalities m
                ON m.id = hu.municipality_id
            JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE hui.period_id = :periodId
              AND m.state_id = :stateId
              AND it.name IN ('total_camas_hospitalizacion', 'total_consultorios')
            GROUP BY it.id, it.name
            HAVING value > 0
            ORDER BY value DESC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        mapInfrastructureLabel(row[0] != null ? row[0].toString() : null),
                        row[0] != null ? row[0].toString() : null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        row[0] != null && row[0].toString().equals("total_camas_hospitalizacion")
                                ? toLong(row[1])
                                : null,
                        row[0] != null && row[0].toString().equals("total_consultorios")
                                ? toLong(row[1])
                                : null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    private StateHospitalBedsMetrics mapStateHospitalBedsMetrics(Object[] row) {
        return new StateHospitalBedsMetrics(
                new DashboardTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        "state"
                ),
                new DashboardPeriod(
                        toInteger(row[3]),
                        toInteger(row[4])
                ),
                toBigInteger(row[5]),
                toLong(row[6]),
                toBigDecimal(row[7]),
                toLong(row[8]),
                toLong(row[9]),
                toLong(row[10])
        );
    }

    // =========================== Municipio ===========================
    @Override
    public Optional<MunicipalityHospitalBedsMetrics> findMunicipalityHospitalBedsMetrics(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> result = em.createNativeQuery("""
            WITH municipality_infra AS (
                SELECT
                    hu.municipality_id,
                    COALESCE(SUM(CASE
                        WHEN it.name = 'total_camas_hospitalizacion'
                        THEN huid.quantity ELSE 0 END), 0
                    ) AS total_hospital_beds,
                    COALESCE(SUM(CASE
                        WHEN it.name = 'total_consultorios'
                        THEN huid.quantity ELSE 0 END), 0
                    ) AS total_consulting_rooms
                FROM health_units hu
                JOIN health_unit_infrastructure hui
                    ON hui.health_unit_id = hu.id
                   AND hui.period_id = :periodId
                JOIN health_unit_infrastructure_details huid
                    ON huid.health_unit_infrastructure_id = hui.id
                JOIN infrastructure_types it
                    ON it.id = huid.infrastructure_type_id
                WHERE hu.municipality_id = :municipalityId
                GROUP BY hu.municipality_id
            ),
            municipality_hospitals AS (
                SELECT
                    hu.municipality_id,
                    COUNT(DISTINCT hu.id) AS total_hospitals
                FROM health_units hu
                JOIN establishment_types et
                    ON et.id = hu.establishment_type_id
                JOIN medical_unit_types mut
                    ON mut.id = hu.medical_unit_type_id
                WHERE hu.municipality_id = :municipalityId
                  AND (
                        et.name = 'DE HOSPITALIZACION'
                        OR UPPER(mut.name) LIKE '%HOSPITAL%'
                  )
                GROUP BY hu.municipality_id
            ),
            predominant_care AS (
                SELECT
                    ranked.care_level
                FROM (
                    SELECT
                        hu.care_level,
                        COUNT(*) AS total_units,
                        ROW_NUMBER() OVER (
                            ORDER BY COUNT(*) DESC, hu.care_level ASC
                        ) AS rn
                    FROM health_units hu
                    WHERE hu.municipality_id = :municipalityId
                    GROUP BY hu.care_level
                ) ranked
                WHERE ranked.rn = 1
            )
            SELECT
                m.id AS municipality_id,
                m.inegi_code AS municipality_code,
                m.name AS municipality_name,
                p.id AS period_id,
                p.period_year AS period_year,
                COALESCE(mh.total_hospitals, 0) AS total_hospitals,
                COALESCE(mi.total_consulting_rooms, 0) AS total_consulting_rooms,
                COALESCE(mi.total_hospital_beds, 0) AS total_hospital_beds,
                COALESCE(pc.care_level, 'not_specified') AS predominant_care_level
            FROM municipalities m
            JOIN periods p
                ON p.id = :periodId
            LEFT JOIN municipality_infra mi
                ON mi.municipality_id = m.id
            LEFT JOIN municipality_hospitals mh
                ON mh.municipality_id = m.id
            LEFT JOIN predominant_care pc
                ON 1 = 1
            WHERE m.id = :municipalityId
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapMunicipalityHospitalBedsMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findMunicipalityHospitalBedsRanking(
            Integer municipalityId,
            Integer periodId,
            Integer limit
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY
                        COALESCE(SUM(CASE
                            WHEN it.name = 'total_camas_hospitalizacion'
                            THEN huid.quantity ELSE 0 END), 0) ASC,
                        hu.name ASC
                ) AS ranking_position,
                hu.id AS unit_id,
                hu.clues AS code,
                hu.name AS name,
                mut.name AS unit_type,
                hu.care_level AS care_level,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS hospital_beds,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_consultorios'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS consulting_rooms
            FROM health_units hu
            JOIN medical_unit_types mut
                ON mut.id = hu.medical_unit_type_id
            LEFT JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE hu.municipality_id = :municipalityId
            GROUP BY hu.id, hu.clues, hu.name, mut.name, hu.care_level
            ORDER BY hospital_beds ASC, hu.name ASC
            LIMIT :limit
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapMunicipalityHospitalBedsUnitRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHospitalBedsMainChart(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                hu.name AS label,
                hu.clues AS code,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS hospital_beds,
                COALESCE(SUM(CASE
                    WHEN it.name = 'total_consultorios'
                    THEN huid.quantity ELSE 0 END), 0
                ) AS consulting_rooms,
                mut.name AS unit_type,
                hu.care_level AS care_level
            FROM health_units hu
            JOIN medical_unit_types mut
                ON mut.id = hu.medical_unit_type_id
            LEFT JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE hu.municipality_id = :municipalityId
            GROUP BY hu.id, hu.clues, hu.name, mut.name, hu.care_level
            ORDER BY hospital_beds DESC, hu.name ASC
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapMunicipalityHospitalBedsChartDataPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHospitalBedsSecondaryChart(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                it.name AS infrastructure_type,
                COALESCE(SUM(huid.quantity), 0) AS total_quantity
            FROM health_units hu
            JOIN health_unit_infrastructure hui
                ON hui.health_unit_id = hu.id
               AND hui.period_id = :periodId
            JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE hu.municipality_id = :municipalityId
              AND it.name IN ('total_camas_hospitalizacion', 'total_consultorios')
            GROUP BY it.id, it.name
            ORDER BY total_quantity DESC
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        mapInfrastructureLabel(row[0] != null ? row[0].toString() : null),
                        row[0] != null ? row[0].toString() : null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        row[0] != null && row[0].toString().equals("total_camas_hospitalizacion")
                                ? toLong(row[1])
                                : null,
                        row[0] != null && row[0].toString().equals("total_consultorios")
                                ? toLong(row[1])
                                : null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findCountryInfrastructureDistribution(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                it.name AS label,
                COALESCE(SUM(huid.quantity), 0) AS value
            FROM health_unit_infrastructure hui
            JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE hui.period_id = :periodId
              AND it.name IN ('total_camas_hospitalizacion', 'total_consultorios')
            GROUP BY it.id, it.name
            HAVING value > 0
            ORDER BY value DESC
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        mapInfrastructureLabel(row[0] != null ? row[0].toString() : null),
                        row[0] != null ? row[0].toString() : null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        row[0] != null && row[0].toString().equals("total_camas_hospitalizacion")
                                ? toLong(row[1])
                                : null,
                        row[0] != null && row[0].toString().equals("total_consultorios")
                                ? toLong(row[1])
                                : null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityInfrastructureDistribution(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                it.name AS label,
                COALESCE(SUM(huid.quantity), 0) AS value
            FROM health_unit_infrastructure hui
            JOIN health_units hu
                ON hu.id = hui.health_unit_id
            JOIN health_unit_infrastructure_details huid
                ON huid.health_unit_infrastructure_id = hui.id
            JOIN infrastructure_types it
                ON it.id = huid.infrastructure_type_id
            WHERE hui.period_id = :periodId
              AND hu.municipality_id = :municipalityId
              AND it.name IN ('total_camas_hospitalizacion', 'total_consultorios')
            GROUP BY it.id, it.name
            HAVING value > 0
            ORDER BY value DESC
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        mapInfrastructureLabel(row[0] != null ? row[0].toString() : null),
                        row[0] != null ? row[0].toString() : null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        row[0] != null && row[0].toString().equals("total_camas_hospitalizacion")
                                ? toLong(row[1])
                                : null,
                        row[0] != null && row[0].toString().equals("total_consultorios")
                                ? toLong(row[1])
                                : null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    private MunicipalityHospitalBedsMetrics mapMunicipalityHospitalBedsMetrics(Object[] row) {
        return new MunicipalityHospitalBedsMetrics(
                new DashboardTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        "municipality"
                ),
                new DashboardPeriod(
                        toInteger(row[3]),
                        toInteger(row[4])
                ),
                toLong(row[5]),
                toLong(row[6]),
                toLong(row[7]),
                row[8] != null ? row[8].toString() : "not_specified"
        );
    }

    private DashboardRankingRow mapMunicipalityHospitalBedsUnitRankingRow(Object[] row) {
        return new DashboardRankingRow(
                String.valueOf(toInteger(row[1])),
                toInteger(row[0]),
                (String) row[2],
                (String) row[3],
                null,
                null,
                toLong(row[6]),
                toLong(row[7]),
                toBigDecimal(row[6]),
                null,
                null,
                Map.of(
                        "unitType", row[4] != null ? row[4].toString() : null,
                        "careLevel", row[5] != null ? row[5].toString() : null
                )
        );
    }

    private DashboardChartDataPoint mapMunicipalityHospitalBedsChartDataPoint(Object[] row) {
        return new DashboardChartDataPoint(
                (String) row[0],
                (String) row[1],
                toBigDecimal(row[2]),
                null,
                null,
                toLong(row[2]),
                toLong(row[3]),
                null,
                null,
                null,
                Map.of(
                        "unitType", row[4] != null ? row[4].toString() : null,
                        "careLevel", row[5] != null ? row[5].toString() : null
                )
        );
    }

    private String mapInfrastructureLabel(String infrastructureType) {
        if (infrastructureType == null) {
            return "Not specified";
        }

        return switch (infrastructureType) {
            case "total_camas_hospitalizacion" -> "Hospital beds";
            case "total_consultorios" -> "Consulting rooms";
            default -> infrastructureType;
        };
    }


    // =========================== POBLACION VULNERABLE ===========================
    // =========================== Pais ===========================
    @Override
    public Optional<CountryHealthcareAccessDeficiencyMetrics> findCountryHealthcareAccessDeficiencyMetrics(Integer periodId) {
        List<Object[]> result = em.createNativeQuery("""
            WITH state_data AS (
                SELECT
                    s.id AS state_id,
                    si.total_population AS population,
                    si.healthcare_access_deficiency AS vulnerable_population,
                    ROUND(
                        (si.healthcare_access_deficiency / NULLIF(si.total_population, 0)) * 100,
                        2
                    ) AS deficiency_rate,
                    COALESCE(SUM(hus.total_doctors), 0) AS doctors
                FROM states s
                JOIN state_indicators si
                    ON si.state_id = s.id
                   AND si.period_id = :periodId
                LEFT JOIN municipalities m
                    ON m.state_id = s.id
                LEFT JOIN health_units hu
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                GROUP BY
                    s.id,
                    si.total_population,
                    si.healthcare_access_deficiency
            )
            SELECT
                p.id AS period_id,
                p.period_year AS period_year,
                COALESCE(SUM(sd.population), 0) AS total_population,
                COALESCE(SUM(sd.vulnerable_population), 0) AS vulnerable_population,
                COALESCE(SUM(CASE WHEN sd.deficiency_rate >= 40 THEN 1 ELSE 0 END), 0) AS priority_states,
                ROUND(
                    (COALESCE(SUM(sd.doctors), 0) / NULLIF(SUM(sd.population), 0)) * 1000,
                    2
                ) AS medical_coverage_index
            FROM periods p
            LEFT JOIN state_data sd ON 1 = 1
            WHERE p.id = :periodId
            GROUP BY p.id, p.period_year
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapCountryHealthcareAccessDeficiencyMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findCountryHealthcareAccessDeficiencyRanking(Integer periodId, Integer limit) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY ROUND((si.healthcare_access_deficiency / NULLIF(si.total_population, 0)) * 100, 2) DESC
                ) AS ranking_position,
                s.id AS state_id,
                s.inegi_code AS code,
                s.name AS name,
                si.total_population AS population,
                COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                ROUND(
                    (COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS coverage_index,
                si.healthcare_access_deficiency AS vulnerable_population,
                ROUND(
                    (si.healthcare_access_deficiency / NULLIF(si.total_population, 0)) * 100,
                    2
                ) AS deficiency_rate
            FROM states s
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = :periodId
            LEFT JOIN municipalities m
                ON m.state_id = s.id
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            GROUP BY
                s.id,
                s.inegi_code,
                s.name,
                si.total_population,
                si.healthcare_access_deficiency
            ORDER BY deficiency_rate DESC
            LIMIT :limit
            """)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapHealthcareAccessDeficiencyRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHealthcareAccessDeficiencyMainChart(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                s.name AS label,
                s.inegi_code AS code,
                si.total_population AS population,
                COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                ROUND(
                    (COALESCE(SUM(hus.total_doctors), 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS coverage_index,
                ROUND(
                    (si.healthcare_access_deficiency / NULLIF(si.total_population, 0)) * 100,
                    2
                ) AS deficiency_rate,
                si.healthcare_access_deficiency AS vulnerable_population
            FROM states s
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = :periodId
            LEFT JOIN municipalities m
                ON m.state_id = s.id
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            GROUP BY
                s.id,
                s.inegi_code,
                s.name,
                si.total_population,
                si.healthcare_access_deficiency
            ORDER BY si.total_population DESC
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapHealthcareAccessDeficiencyScatterPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHealthcareAccessDeficiencySecondaryChart(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                s.name AS label,
                s.inegi_code AS code,
                si.healthcare_access_deficiency AS vulnerable_population,
                ROUND(
                    (si.healthcare_access_deficiency / NULLIF(si.total_population, 0)) * 100,
                    2
                ) AS deficiency_rate,
                si.total_population AS population
            FROM states s
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = :periodId
            ORDER BY si.healthcare_access_deficiency DESC
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapHealthcareAccessDeficiencyBarPoint)
                .toList();
    }

    private CountryHealthcareAccessDeficiencyMetrics mapCountryHealthcareAccessDeficiencyMetrics(Object[] row) {
        return new CountryHealthcareAccessDeficiencyMetrics(
                new DashboardPeriod(
                        toInteger(row[0]),
                        toInteger(row[1])
                ),
                toBigInteger(row[2]),
                toBigInteger(row[3]),
                toLong(row[4]),
                toBigDecimal(row[5])
        );
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHealthcareAccessDistribution(Integer periodId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                'Population with healthcare access deficiency' AS label,
                COALESCE(SUM(si.healthcare_access_deficiency), 0) AS value
            FROM state_indicators si
            WHERE si.period_id = :periodId

            UNION ALL

            SELECT
                'Population without healthcare access deficiency' AS label,
                GREATEST(
                    COALESCE(SUM(si.total_population), 0) - COALESCE(SUM(si.healthcare_access_deficiency), 0),
                    0
                ) AS value
            FROM state_indicators si
            WHERE si.period_id = :periodId
            """)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapSimplePiePoint)
                .toList();
    }

    private DashboardRankingRow mapHealthcareAccessDeficiencyRankingRow(Object[] row) {
        BigDecimal deficiencyRate = toBigDecimal(row[8]);
        String[] classification = classifyHealthcareAccessDeficiency(deficiencyRate);

        return new DashboardRankingRow(
                String.valueOf(toInteger(row[1])),
                toInteger(row[0]),
                (String) row[2],
                (String) row[3],
                toBigInteger(row[4]),
                toLong(row[5]),
                null,
                null,
                deficiencyRate,
                classification[0],
                classification[1],
                Map.of(
                        "coverageIndex", toBigDecimal(row[6]),
                        "vulnerablePopulation", toBigInteger(row[7])
                )
        );
    }

    private DashboardChartDataPoint mapHealthcareAccessDeficiencyScatterPoint(Object[] row) {
        BigDecimal deficiencyRate = toBigDecimal(row[5]);
        String[] classification = classifyHealthcareAccessDeficiency(deficiencyRate);

        return new DashboardChartDataPoint(
                (String) row[0],
                (String) row[1],
                deficiencyRate,
                toBigInteger(row[2]),
                toLong(row[3]),
                null,
                null,
                toBigDecimal(row[4]),
                classification[0],
                classification[1],
                Map.of(
                        "vulnerablePopulation", toBigInteger(row[6]),
                        "deficiencyRate", deficiencyRate
                )
        );
    }

    private DashboardChartDataPoint mapHealthcareAccessDeficiencyBarPoint(Object[] row) {
        BigDecimal deficiencyRate = toBigDecimal(row[3]);
        String[] classification = classifyHealthcareAccessDeficiency(deficiencyRate);

        return new DashboardChartDataPoint(
                (String) row[0],
                (String) row[1],
                toBigDecimal(row[2]),
                toBigInteger(row[4]),
                null,
                null,
                null,
                null,
                classification[0],
                classification[1],
                Map.of(
                        "deficiencyRate", deficiencyRate,
                        "vulnerablePopulation", toBigInteger(row[2])
                )
        );
    }

    private String[] classifyHealthcareAccessDeficiency(BigDecimal value) {
        if (value == null) {
            return new String[]{"no_data", "neutral"};
        }

        double number = value.doubleValue();

        if (number <= 20) {
            return new String[]{"good", "green"};
        }

        if (number < 40) {
            return new String[]{"risk", "yellow"};
        }

        return new String[]{"critical", "red"};
    }

    // =========================== Estado ===========================
    @Override
    public Optional<StateHealthcareAccessDeficiencyMetrics> findStateHealthcareAccessDeficiencyMetrics(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> result = em.createNativeQuery("""
            WITH municipality_data AS (
                SELECT
                    m.id AS municipality_id,
                    mi.total_population AS population,
                    mi.healthcare_access_deficiency AS vulnerable_population,
                    ROUND(
                        (mi.healthcare_access_deficiency / NULLIF(mi.total_population, 0)) * 100,
                        2
                    ) AS deficiency_rate,
                    COALESCE(SUM(hus.total_doctors), 0) AS doctors
                FROM municipalities m
                JOIN municipality_indicators mi
                    ON mi.municipality_id = m.id
                   AND mi.period_id = :periodId
                LEFT JOIN health_units hu
                    ON hu.municipality_id = m.id
                LEFT JOIN health_unit_staff hus
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                WHERE m.state_id = :stateId
                GROUP BY
                    m.id,
                    mi.total_population,
                    mi.healthcare_access_deficiency
            ),
            state_units AS (
                SELECT
                    m.state_id,
                    COUNT(DISTINCT hu.id) AS total_health_units
                FROM municipalities m
                LEFT JOIN health_units hu
                    ON hu.municipality_id = m.id
                WHERE m.state_id = :stateId
                GROUP BY m.state_id
            )
            SELECT
                s.id AS state_id,
                s.inegi_code AS state_code,
                s.name AS state_name,
                p.id AS period_id,
                p.period_year AS period_year,
                si.total_population AS total_population,
                COALESCE(SUM(CASE WHEN md.deficiency_rate >= 40 THEN 1 ELSE 0 END), 0) AS priority_municipalities,
                ROUND(
                    (COALESCE(SUM(md.doctors), 0) / NULLIF(si.total_population, 0)) * 1000,
                    2
                ) AS medical_coverage_index,
                COALESCE(su.total_health_units, 0) AS available_infrastructure
            FROM states s
            JOIN periods p
                ON p.id = :periodId
            JOIN state_indicators si
                ON si.state_id = s.id
               AND si.period_id = p.id
            LEFT JOIN municipality_data md
                ON 1 = 1
            LEFT JOIN state_units su
                ON su.state_id = s.id
            WHERE s.id = :stateId
            GROUP BY
                s.id,
                s.inegi_code,
                s.name,
                p.id,
                p.period_year,
                si.total_population,
                su.total_health_units
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapStateHealthcareAccessDeficiencyMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findStateHealthcareAccessDeficiencyRanking(
            Integer stateId,
            Integer periodId,
            Integer limit
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY ROUND((mi.healthcare_access_deficiency / NULLIF(mi.total_population, 0)) * 100, 2) DESC
                ) AS ranking_position,
                m.id AS municipality_id,
                m.inegi_code AS code,
                m.name AS name,
                mi.total_population AS population,
                COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                ROUND(
                    (COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000,
                    2
                ) AS coverage_index,
                mi.healthcare_access_deficiency AS vulnerable_population,
                ROUND(
                    (mi.healthcare_access_deficiency / NULLIF(mi.total_population, 0)) * 100,
                    2
                ) AS deficiency_rate
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE m.state_id = :stateId
            GROUP BY
                m.id,
                m.inegi_code,
                m.name,
                mi.total_population,
                mi.healthcare_access_deficiency
            ORDER BY deficiency_rate DESC
            LIMIT :limit
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapHealthcareAccessDeficiencyRankingRow)
                .toList();
    }
    @Override
    public List<DashboardChartDataPoint> findStateHealthcareAccessDeficiencyMainChart(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                m.name AS label,
                m.inegi_code AS code,
                mi.total_population AS population,
                COALESCE(SUM(hus.total_doctors), 0) AS doctors,
                ROUND(
                    (COALESCE(SUM(hus.total_doctors), 0) / NULLIF(mi.total_population, 0)) * 1000,
                    2
                ) AS coverage_index,
                ROUND(
                    (mi.healthcare_access_deficiency / NULLIF(mi.total_population, 0)) * 100,
                    2
                ) AS deficiency_rate,
                mi.healthcare_access_deficiency AS vulnerable_population
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            LEFT JOIN health_units hu
                ON hu.municipality_id = m.id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE m.state_id = :stateId
            GROUP BY
                m.id,
                m.inegi_code,
                m.name,
                mi.total_population,
                mi.healthcare_access_deficiency
            ORDER BY mi.total_population DESC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapHealthcareAccessDeficiencyScatterPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateHealthcareAccessDeficiencySecondaryChart(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                m.name AS label,
                m.inegi_code AS code,
                mi.healthcare_access_deficiency AS vulnerable_population,
                ROUND(
                    (mi.healthcare_access_deficiency / NULLIF(mi.total_population, 0)) * 100,
                    2
                ) AS deficiency_rate,
                mi.total_population AS population
            FROM municipalities m
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = :periodId
            WHERE m.state_id = :stateId
            ORDER BY mi.healthcare_access_deficiency DESC
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapHealthcareAccessDeficiencyBarPoint)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findStateHealthcareAccessDistribution(
            Integer stateId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                'Population with healthcare access deficiency' AS label,
                COALESCE(si.healthcare_access_deficiency, 0) AS value
            FROM state_indicators si
            WHERE si.period_id = :periodId
              AND si.state_id = :stateId

            UNION ALL

            SELECT
                'Population without healthcare access deficiency' AS label,
                GREATEST(
                    COALESCE(si.total_population, 0) - COALESCE(si.healthcare_access_deficiency, 0),
                    0
                ) AS value
            FROM state_indicators si
            WHERE si.period_id = :periodId
              AND si.state_id = :stateId
            """)
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapSimplePiePoint)
                .toList();
    }

    private StateHealthcareAccessDeficiencyMetrics mapStateHealthcareAccessDeficiencyMetrics(Object[] row) {
        return new StateHealthcareAccessDeficiencyMetrics(
                new DashboardTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        "state"
                ),
                new DashboardPeriod(
                        toInteger(row[3]),
                        toInteger(row[4])
                ),
                toBigInteger(row[5]),
                toLong(row[6]),
                toBigDecimal(row[7]),
                toLong(row[8])
        );
    }

    // =========================== Municipio ===========================
    @Override
    public Optional<MunicipalityHealthcareAccessDeficiencyMetrics> findMunicipalityHealthcareAccessDeficiencyMetrics(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> result = em.createNativeQuery("""
            SELECT
                m.id AS municipality_id,
                m.inegi_code AS municipality_code,
                m.name AS municipality_name,
                p.id AS period_id,
                p.period_year AS period_year,
                mi.total_population AS total_population,
                COALESCE(staff.available_doctors, 0) AS available_doctors,
                COALESCE(units.health_centers, 0) AS health_centers,
                ROUND(
                    (COALESCE(staff.available_doctors, 0) / NULLIF(mi.total_population, 0)) * 1000,
                    2
                ) AS coverage_index
            FROM municipalities m
            JOIN periods p
                ON p.id = :periodId
            JOIN municipality_indicators mi
                ON mi.municipality_id = m.id
               AND mi.period_id = p.id
            LEFT JOIN (
                SELECT
                    hu.municipality_id,
                    SUM(hus.total_doctors) AS available_doctors
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
                    COUNT(DISTINCT hu.id) AS health_centers
                FROM health_units hu
                GROUP BY hu.municipality_id
            ) units
                ON units.municipality_id = m.id
            WHERE m.id = :municipalityId
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapMunicipalityHealthcareAccessDeficiencyMetrics(result.get(0)));
    }

    @Override
    public List<DashboardRankingRow> findMunicipalityHealthcareAccessDeficiencyRanking(
            Integer municipalityId,
            Integer periodId,
            Integer limit
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                ROW_NUMBER() OVER (
                    ORDER BY COALESCE(hus.total_doctors, 0) DESC, hu.name ASC
                ) AS ranking_position,
                hu.id AS unit_id,
                hu.clues AS code,
                hu.name AS name,
                COALESCE(hus.total_doctors, 0) AS doctors,
                mut.name AS unit_type,
                hu.care_level AS care_level
            FROM health_units hu
            JOIN medical_unit_types mut
                ON mut.id = hu.medical_unit_type_id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE hu.municipality_id = :municipalityId
            ORDER BY doctors DESC, hu.name ASC
            LIMIT :limit
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(this::mapMunicipalityHealthcareAccessDeficiencyUnitRankingRow)
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDeficiencyMainChart(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                hu.name AS label,
                hu.clues AS code,
                COALESCE(hus.total_doctors, 0) AS doctors,
                mut.name AS unit_type,
                hu.care_level AS care_level
            FROM health_units hu
            JOIN medical_unit_types mut
                ON mut.id = hu.medical_unit_type_id
            LEFT JOIN health_unit_staff hus
                ON hus.health_unit_id = hu.id
               AND hus.period_id = :periodId
            WHERE hu.municipality_id = :municipalityId
            ORDER BY doctors DESC, hu.name ASC
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        (String) row[0],
                        (String) row[1],
                        toBigDecimal(row[2]),
                        null,
                        toLong(row[2]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of(
                                "unitType", row[3] != null ? row[3].toString() : null,
                                "careLevel", row[4] != null ? row[4].toString() : null
                        )
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDeficiencySecondaryChart(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                hu.care_level AS label,
                COUNT(*) AS total_units
            FROM health_units hu
            WHERE hu.municipality_id = :municipalityId
            GROUP BY hu.care_level
            ORDER BY total_units DESC
            """)
                .setParameter("municipalityId", municipalityId)
                .getResultList();

        return rows.stream()
                .map(row -> new DashboardChartDataPoint(
                        row[0] != null ? row[0].toString() : "not_specified",
                        null,
                        toBigDecimal(row[1]),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Map.of()
                ))
                .toList();
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDistribution(
            Integer municipalityId,
            Integer periodId
    ) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT
                'Population with healthcare access deficiency' AS label,
                COALESCE(mi.healthcare_access_deficiency, 0) AS value
            FROM municipality_indicators mi
            WHERE mi.period_id = :periodId
              AND mi.municipality_id = :municipalityId

            UNION ALL

            SELECT
                'Population without healthcare access deficiency' AS label,
                GREATEST(
                    COALESCE(mi.total_population, 0) - COALESCE(mi.healthcare_access_deficiency, 0),
                    0
                ) AS value
            FROM municipality_indicators mi
            WHERE mi.period_id = :periodId
              AND mi.municipality_id = :municipalityId
            """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .getResultList();

        return rows.stream()
                .map(this::mapSimplePiePoint)
                .toList();
    }

    private MunicipalityHealthcareAccessDeficiencyMetrics mapMunicipalityHealthcareAccessDeficiencyMetrics(Object[] row) {
        return new MunicipalityHealthcareAccessDeficiencyMetrics(
                new DashboardTerritory(
                        toInteger(row[0]),
                        (String) row[1],
                        (String) row[2],
                        "municipality"
                ),
                new DashboardPeriod(
                        toInteger(row[3]),
                        toInteger(row[4])
                ),
                toBigInteger(row[5]),
                toLong(row[6]),
                toLong(row[7]),
                toBigDecimal(row[8])
        );
    }

    private DashboardRankingRow mapMunicipalityHealthcareAccessDeficiencyUnitRankingRow(Object[] row) {
        return new DashboardRankingRow(
                String.valueOf(toInteger(row[1])),
                toInteger(row[0]),
                (String) row[2],
                (String) row[3],
                null,
                toLong(row[4]),
                null,
                null,
                toBigDecimal(row[4]),
                null,
                null,
                Map.of(
                        "unitType", row[5] != null ? row[5].toString() : null,
                        "careLevel", row[6] != null ? row[6].toString() : null
                )
        );
    }
}
