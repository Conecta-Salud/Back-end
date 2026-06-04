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
import com.itesm.domain.models.indicator.TerritoryIndicatorValueDto;
import com.itesm.domain.repository.DashboardSummaryRepository;
import com.itesm.domain.repository.TerritoryIndicatorQueryRepository;
import com.itesm.domain.service.DataAvailabilityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class DashboardSummaryRepositoryImpl implements DashboardSummaryRepository {

    private static final String TOTAL_POPULATION = "total_population";
    private static final String PERCENTAGE_OVER_60 = "percentage_over_60";
    private static final String MEDICAL_COVERAGE = "doctors_per_1000";
    private static final String HOSPITAL_BEDS = "beds_per_1000";
    private static final String HEALTHCARE_ACCESS_DEFICIENCY = "healthcare_access_deficiency";
    private static final String TOTAL_POVERTY_POPULATION = "total_poverty_population";
    private static final String HEALTH_ESTABLISHMENTS = "health_establishments";
    private static final String TOTAL_DOCTORS = "total_doctors";
    private static final String HOSPITAL_BEDS_TOTAL = "hospital_beds";
    private static final String CONSULTING_ROOMS = "consulting_rooms";

    private final EntityManager em;
    private final TerritoryIndicatorQueryRepository indicatorRepository;
    private final DataAvailabilityService dataAvailabilityService;

    public DashboardSummaryRepositoryImpl(
            EntityManager em,
            TerritoryIndicatorQueryRepository indicatorRepository,
            DataAvailabilityService dataAvailabilityService
    ) {
        this.em = em;
        this.indicatorRepository = indicatorRepository;
        this.dataAvailabilityService = dataAvailabilityService;
    }

    @Override
    public boolean existsPeriodById(Integer periodId) {
        return period(periodId).isPresent();
    }

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
    public Optional<CountryMedicalCoverageMetrics> findCountryMedicalCoverageMetrics(Integer periodId) {
        return period(periodId).map(period -> {
            Integer year = period.getPeriodYear();
            return new CountryMedicalCoverageMetrics(
                    period,
                    bigIntegerValue("country", null, null, year, TOTAL_POPULATION),
                    longValue("country", null, null, year, TOTAL_DOCTORS),
                    decimalValue("country", null, null, year, MEDICAL_COVERAGE),
                    countStateValuesBelow(MEDICAL_COVERAGE, year, BigDecimal.ONE),
                    averageValue(indicatorRepository.findStateValues(MEDICAL_COVERAGE, year))
            );
        });
    }

    @Override
    public List<DashboardRankingRow> findCountryMedicalCoverageRanking(Integer periodId, Integer limit) {
        return stateRanking(periodId, MEDICAL_COVERAGE, limit, true);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryMedicalCoverageMainChart(Integer periodId) {
        return stateChart(periodId, MEDICAL_COVERAGE, true);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryMedicalCoverageSecondaryChart(Integer periodId) {
        return stateChart(periodId, PERCENTAGE_OVER_60, false);
    }

    @Override
    public List<DashboardChartDataPoint> findCountrySpecialtiesDistribution(Integer periodId) {
        return chartRows(em.createNativeQuery("""
                SELECT
                    sp.name AS label,
                    NULL AS code,
                    SUM(huss.quantity) AS value
                FROM health_unit_staff_specialties huss
                JOIN specialties sp ON sp.id = huss.specialty_id
                JOIN health_unit_staff hus ON hus.id = huss.health_unit_staff_id
                WHERE hus.period_id = :periodId
                GROUP BY sp.name
                ORDER BY value DESC
                """)
                .setParameter("periodId", periodId)
                .getResultList());
    }

    @Override
    public Optional<StateMedicalCoverageMetrics> findStateMedicalCoverageMetrics(Integer stateId, Integer periodId) {
        return period(periodId).flatMap(period -> stateTerritory(stateId).map(territory -> {
            Integer year = period.getPeriodYear();
            String stateCode = territory.getCode();
            return new StateMedicalCoverageMetrics(
                    territory,
                    period,
                    bigIntegerValue("state", stateId, null, year, TOTAL_POPULATION),
                    longValue("state", stateId, null, year, TOTAL_DOCTORS),
                    decimalValue("state", stateId, null, year, MEDICAL_COVERAGE),
                    countMunicipalityValuesBelow(MEDICAL_COVERAGE, year, stateCode, BigDecimal.ONE),
                    averageValue(indicatorRepository.findMunicipalityValuesByState(MEDICAL_COVERAGE, year, stateCode))
            );
        }));
    }

    @Override
    public List<DashboardRankingRow> findStateMedicalCoverageRanking(Integer stateId, Integer periodId, Integer limit) {
        return municipalityRankingByState(stateId, periodId, MEDICAL_COVERAGE, limit, true);
    }

    @Override
    public List<DashboardChartDataPoint> findStateMedicalCoverageMainChart(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, MEDICAL_COVERAGE, true);
    }

    @Override
    public List<DashboardChartDataPoint> findStateMedicalCoverageSecondaryChart(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, PERCENTAGE_OVER_60, false);
    }

    @Override
    public List<DashboardChartDataPoint> findStateSpecialtiesDistribution(Integer stateId, Integer periodId) {
        return chartRows(em.createNativeQuery("""
                SELECT
                    sp.name AS label,
                    NULL AS code,
                    SUM(huss.quantity) AS value
                FROM health_unit_staff_specialties huss
                JOIN specialties sp ON sp.id = huss.specialty_id
                JOIN health_unit_staff hus ON hus.id = huss.health_unit_staff_id
                JOIN health_units hu ON hu.id = hus.health_unit_id
                JOIN municipalities m ON m.id = hu.municipality_id
                WHERE hus.period_id = :periodId
                  AND m.state_id = :stateId
                GROUP BY sp.name
                ORDER BY value DESC
                """)
                .setParameter("periodId", periodId)
                .setParameter("stateId", stateId)
                .getResultList());
    }

    @Override
    public Optional<MunicipalityMedicalCoverageMetrics> findMunicipalityMedicalCoverageMetrics(Integer municipalityId, Integer periodId) {
        return period(periodId).flatMap(period -> municipalityTerritory(municipalityId).map(territory -> {
            Integer year = period.getPeriodYear();
            return new MunicipalityMedicalCoverageMetrics(
                    territory,
                    period,
                    bigIntegerValue("municipality", null, municipalityId, year, TOTAL_POPULATION),
                    longValue("municipality", null, municipalityId, year, TOTAL_DOCTORS),
                    decimalValue("municipality", null, municipalityId, year, MEDICAL_COVERAGE),
                    longValue("municipality", null, municipalityId, year, CONSULTING_ROOMS),
                    countHospitals(null, municipalityId)
            );
        }));
    }

    @Override
    public List<DashboardRankingRow> findMunicipalityMedicalCoverageRanking(Integer municipalityId, Integer periodId, Integer limit) {
        return healthUnitDoctorRanking(municipalityId, periodId, limit);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityMedicalCoverageMainChart(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, MEDICAL_COVERAGE, true);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityMedicalCoverageSecondaryChart(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, PERCENTAGE_OVER_60, false);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalitySpecialtiesDistribution(Integer municipalityId, Integer periodId) {
        return chartRows(em.createNativeQuery("""
                SELECT
                    sp.name AS label,
                    NULL AS code,
                    SUM(huss.quantity) AS value
                FROM health_unit_staff_specialties huss
                JOIN specialties sp ON sp.id = huss.specialty_id
                JOIN health_unit_staff hus ON hus.id = huss.health_unit_staff_id
                JOIN health_units hu ON hu.id = hus.health_unit_id
                WHERE hus.period_id = :periodId
                  AND hu.municipality_id = :municipalityId
                GROUP BY sp.name
                ORDER BY value DESC
                """)
                .setParameter("periodId", periodId)
                .setParameter("municipalityId", municipalityId)
                .getResultList());
    }

    @Override
    public Optional<CountryHospitalBedsMetrics> findCountryHospitalBedsMetrics(Integer periodId) {
        return period(periodId).map(period -> {
            Integer year = period.getPeriodYear();
            Long hospitals = countHospitals(null, null);
            Long beds = longValue("country", null, null, year, HOSPITAL_BEDS_TOTAL);
            return new CountryHospitalBedsMetrics(
                    period,
                    bigIntegerValue("country", null, null, year, TOTAL_POPULATION),
                    beds,
                    decimalValue("country", null, null, year, HOSPITAL_BEDS),
                    countStateValuesBelow(HOSPITAL_BEDS, year, BigDecimal.ONE),
                    hospitals,
                    averagePerUnit(beds, hospitals)
            );
        });
    }

    @Override
    public List<DashboardRankingRow> findCountryHospitalBedsRanking(Integer periodId, Integer limit) {
        return stateRanking(periodId, HOSPITAL_BEDS, limit, true);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHospitalBedsMainChart(Integer periodId) {
        return stateChart(periodId, HOSPITAL_BEDS, true);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHospitalBedsSecondaryChart(Integer periodId) {
        return stateChart(periodId, TOTAL_POPULATION, false);
    }

    @Override
    public Optional<StateHospitalBedsMetrics> findStateHospitalBedsMetrics(Integer stateId, Integer periodId) {
        return period(periodId).flatMap(period -> stateTerritory(stateId).map(territory -> {
            Integer year = period.getPeriodYear();
            String stateCode = territory.getCode();
            return new StateHospitalBedsMetrics(
                    territory,
                    period,
                    bigIntegerValue("state", stateId, null, year, TOTAL_POPULATION),
                    longValue("state", stateId, null, year, HOSPITAL_BEDS_TOTAL),
                    decimalValue("state", stateId, null, year, HOSPITAL_BEDS),
                    countMunicipalityValuesBelow(HOSPITAL_BEDS, year, stateCode, BigDecimal.ONE),
                    countHospitals(stateId, null),
                    longValue("state", stateId, null, year, CONSULTING_ROOMS)
            );
        }));
    }

    @Override
    public List<DashboardRankingRow> findStateHospitalBedsRanking(Integer stateId, Integer periodId, Integer limit) {
        return municipalityRankingByState(stateId, periodId, HOSPITAL_BEDS, limit, true);
    }

    @Override
    public List<DashboardChartDataPoint> findStateHospitalBedsMainChart(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, HOSPITAL_BEDS, true);
    }

    @Override
    public List<DashboardChartDataPoint> findStateHospitalBedsSecondaryChart(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, TOTAL_POPULATION, false);
    }

    @Override
    public List<DashboardChartDataPoint> findStateInfrastructureDistribution(Integer stateId, Integer periodId) {
        return infrastructureDistribution(periodId, stateId, null);
    }

    @Override
    public Optional<MunicipalityHospitalBedsMetrics> findMunicipalityHospitalBedsMetrics(Integer municipalityId, Integer periodId) {
        return period(periodId).flatMap(period -> municipalityTerritory(municipalityId).map(territory -> new MunicipalityHospitalBedsMetrics(
                territory,
                period,
                countHospitals(null, municipalityId),
                longValue("municipality", null, municipalityId, period.getPeriodYear(), CONSULTING_ROOMS),
                longValue("municipality", null, municipalityId, period.getPeriodYear(), HOSPITAL_BEDS_TOTAL),
                predominantCareLevel(municipalityId)
        )));
    }

    @Override
    public List<DashboardRankingRow> findMunicipalityHospitalBedsRanking(Integer municipalityId, Integer periodId, Integer limit) {
        return healthUnitInfrastructureRanking(municipalityId, periodId, limit);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHospitalBedsMainChart(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, HOSPITAL_BEDS, true);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHospitalBedsSecondaryChart(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, TOTAL_POPULATION, false);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryInfrastructureDistribution(Integer periodId) {
        return infrastructureDistribution(periodId, null, null);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityInfrastructureDistribution(Integer municipalityId, Integer periodId) {
        return infrastructureDistribution(periodId, null, municipalityId);
    }

    @Override
    public Optional<CountryHealthcareAccessDeficiencyMetrics> findCountryHealthcareAccessDeficiencyMetrics(Integer periodId) {
        return period(periodId).map(period -> {
            Integer year = period.getPeriodYear();
            return new CountryHealthcareAccessDeficiencyMetrics(
                    period,
                    bigIntegerValue("country", null, null, year, TOTAL_POPULATION),
                    bigIntegerValue("country", null, null, year, HEALTHCARE_ACCESS_DEFICIENCY),
                    countStateValuesAbove(HEALTHCARE_ACCESS_DEFICIENCY, year, BigDecimal.ZERO),
                    decimalValue("country", null, null, year, MEDICAL_COVERAGE)
            );
        });
    }

    @Override
    public List<DashboardRankingRow> findCountryHealthcareAccessDeficiencyRanking(Integer periodId, Integer limit) {
        return stateRanking(periodId, HEALTHCARE_ACCESS_DEFICIENCY, limit, false);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHealthcareAccessDeficiencyMainChart(Integer periodId) {
        return stateChart(periodId, HEALTHCARE_ACCESS_DEFICIENCY, false);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHealthcareAccessDeficiencySecondaryChart(Integer periodId) {
        return stateChart(periodId, TOTAL_POVERTY_POPULATION, false);
    }

    @Override
    public List<DashboardChartDataPoint> findCountryHealthcareAccessDistribution(Integer periodId) {
        return stateChart(periodId, HEALTHCARE_ACCESS_DEFICIENCY, false);
    }

    @Override
    public Optional<StateHealthcareAccessDeficiencyMetrics> findStateHealthcareAccessDeficiencyMetrics(Integer stateId, Integer periodId) {
        return period(periodId).flatMap(period -> stateTerritory(stateId).map(territory -> {
            Integer year = period.getPeriodYear();
            return new StateHealthcareAccessDeficiencyMetrics(
                    territory,
                    period,
                    bigIntegerValue("state", stateId, null, year, TOTAL_POPULATION),
                    countMunicipalityValuesAbove(HEALTHCARE_ACCESS_DEFICIENCY, year, territory.getCode(), BigDecimal.ZERO),
                    decimalValue("state", stateId, null, year, MEDICAL_COVERAGE),
                    longValue("state", stateId, null, year, HEALTH_ESTABLISHMENTS)
            );
        }));
    }

    @Override
    public List<DashboardRankingRow> findStateHealthcareAccessDeficiencyRanking(Integer stateId, Integer periodId, Integer limit) {
        return municipalityRankingByState(stateId, periodId, HEALTHCARE_ACCESS_DEFICIENCY, limit, false);
    }

    @Override
    public List<DashboardChartDataPoint> findStateHealthcareAccessDeficiencyMainChart(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, HEALTHCARE_ACCESS_DEFICIENCY, false);
    }

    @Override
    public List<DashboardChartDataPoint> findStateHealthcareAccessDeficiencySecondaryChart(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, MEDICAL_COVERAGE, true);
    }

    @Override
    public List<DashboardChartDataPoint> findStateHealthcareAccessDistribution(Integer stateId, Integer periodId) {
        return municipalityChartByState(stateId, periodId, HEALTHCARE_ACCESS_DEFICIENCY, false);
    }

    @Override
    public Optional<MunicipalityHealthcareAccessDeficiencyMetrics> findMunicipalityHealthcareAccessDeficiencyMetrics(Integer municipalityId, Integer periodId) {
        return period(periodId).flatMap(period -> municipalityTerritory(municipalityId).map(territory -> {
            Integer year = period.getPeriodYear();
            return new MunicipalityHealthcareAccessDeficiencyMetrics(
                    territory,
                    period,
                    bigIntegerValue("municipality", null, municipalityId, year, TOTAL_POPULATION),
                    longValue("municipality", null, municipalityId, year, TOTAL_DOCTORS),
                    longValue("municipality", null, municipalityId, year, HEALTH_ESTABLISHMENTS),
                    decimalValue("municipality", null, municipalityId, year, MEDICAL_COVERAGE)
            );
        }));
    }

    @Override
    public List<DashboardRankingRow> findMunicipalityHealthcareAccessDeficiencyRanking(Integer municipalityId, Integer periodId, Integer limit) {
        return healthUnitDoctorRanking(municipalityId, periodId, limit);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDeficiencyMainChart(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, HEALTHCARE_ACCESS_DEFICIENCY, false);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDeficiencySecondaryChart(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, MEDICAL_COVERAGE, true);
    }

    @Override
    public List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDistribution(Integer municipalityId, Integer periodId) {
        return municipalityChartBySiblingState(municipalityId, periodId, HEALTHCARE_ACCESS_DEFICIENCY, false);
    }

    private List<DashboardRankingRow> stateRanking(Integer periodId, String indicatorCode, Integer limit, boolean higherIsBetter) {
        Integer year = analysisYear(periodId).orElse(null);
        if (year == null || !dataAvailabilityService.isIndicatorAvailable(indicatorCode, "state", year)) {
            return List.of();
        }

        boolean sortHigherIsBetter = higherIsBetter(indicatorCode, higherIsBetter);
        Map<String, TerritoryIndicatorValueDto> population = indicatorRepository.findStateValues(TOTAL_POPULATION, year)
                .stream()
                .collect(Collectors.toMap(TerritoryIndicatorValueDto::getTerritoryCode, Function.identity(), (left, right) -> left));
        Map<String, TerritoryIndicatorValueDto> coverage = rankingNeedsCoverageIndex(indicatorCode)
                ? indicatorRepository.findStateValues(MEDICAL_COVERAGE, year)
                        .stream()
                        .collect(Collectors.toMap(TerritoryIndicatorValueDto::getTerritoryCode, Function.identity(), (left, right) -> left))
                : Map.of();
        Map<String, Long> doctors = rankingNeedsDoctors(indicatorCode) ? indicatorLongTotalsByState(TOTAL_DOCTORS, year) : Map.of();
        Map<String, Long> hospitalBeds = rankingNeedsHospitalBeds(indicatorCode)
                ? indicatorLongTotalsByState(HOSPITAL_BEDS_TOTAL, year)
                : Map.of();
        Map<String, Long> consultingRooms = rankingNeedsHospitalBeds(indicatorCode)
                ? indicatorLongTotalsByState(CONSULTING_ROOMS, year)
                : Map.of();

        AtomicInteger rank = new AtomicInteger(1);
        return indicatorRepository.findStateValues(indicatorCode, year)
                .stream()
                .filter(value -> value.getValue() != null)
                .sorted((a, b) -> compareValues(a.getValue(), b.getValue(), sortHigherIsBetter))
                .limit(normalizeLimit(limit))
                .map(value -> rankingRow(
                        value,
                        population.get(value.getTerritoryCode()),
                        doctors.get(value.getTerritoryCode()),
                        hospitalBeds.get(value.getTerritoryCode()),
                        consultingRooms.get(value.getTerritoryCode()),
                        coverage.get(value.getTerritoryCode()),
                        rank.getAndIncrement(),
                        sortHigherIsBetter
                ))
                .toList();
    }

    private List<DashboardRankingRow> municipalityRankingByState(Integer stateId, Integer periodId, String indicatorCode, Integer limit, boolean higherIsBetter) {
        return stateCode(stateId)
                .map(code -> municipalityRanking(code, periodId, indicatorCode, limit, higherIsBetter))
                .orElse(List.of());
    }

    private List<DashboardRankingRow> municipalityRankingBySiblingState(Integer municipalityId, Integer periodId, String indicatorCode, Integer limit, boolean higherIsBetter) {
        return stateCodeByMunicipality(municipalityId)
                .map(code -> municipalityRanking(code, periodId, indicatorCode, limit, higherIsBetter))
                .orElse(List.of());
    }

    private List<DashboardRankingRow> municipalityRanking(String stateCode, Integer periodId, String indicatorCode, Integer limit, boolean higherIsBetter) {
        Integer year = analysisYear(periodId).orElse(null);
        if (year == null || !dataAvailabilityService.isIndicatorAvailable(indicatorCode, "municipality", year)) {
            return List.of();
        }

        boolean sortHigherIsBetter = higherIsBetter(indicatorCode, higherIsBetter);
        Map<String, TerritoryIndicatorValueDto> population = indicatorRepository.findMunicipalityValuesByState(TOTAL_POPULATION, year, stateCode)
                .stream()
                .collect(Collectors.toMap(TerritoryIndicatorValueDto::getTerritoryCode, Function.identity(), (left, right) -> left));
        Map<String, TerritoryIndicatorValueDto> coverage = rankingNeedsCoverageIndex(indicatorCode)
                ? indicatorRepository.findMunicipalityValuesByState(MEDICAL_COVERAGE, year, stateCode)
                        .stream()
                        .collect(Collectors.toMap(TerritoryIndicatorValueDto::getTerritoryCode, Function.identity(), (left, right) -> left))
                : Map.of();
        Map<String, Long> doctors = rankingNeedsDoctors(indicatorCode) ? indicatorLongTotalsByMunicipality(TOTAL_DOCTORS, year, stateCode) : Map.of();
        Map<String, Long> hospitalBeds = rankingNeedsHospitalBeds(indicatorCode)
                ? indicatorLongTotalsByMunicipality(HOSPITAL_BEDS_TOTAL, year, stateCode)
                : Map.of();
        Map<String, Long> consultingRooms = rankingNeedsHospitalBeds(indicatorCode)
                ? indicatorLongTotalsByMunicipality(CONSULTING_ROOMS, year, stateCode)
                : Map.of();

        AtomicInteger rank = new AtomicInteger(1);
        return indicatorRepository.findMunicipalityValuesByState(indicatorCode, year, stateCode)
                .stream()
                .filter(value -> value.getValue() != null)
                .sorted((a, b) -> compareValues(a.getValue(), b.getValue(), sortHigherIsBetter))
                .limit(normalizeLimit(limit))
                .map(value -> rankingRow(
                        value,
                        population.get(value.getTerritoryCode()),
                        doctors.get(value.getTerritoryCode()),
                        hospitalBeds.get(value.getTerritoryCode()),
                        consultingRooms.get(value.getTerritoryCode()),
                        coverage.get(value.getTerritoryCode()),
                        rank.getAndIncrement(),
                        sortHigherIsBetter
                ))
                .toList();
    }

    private List<DashboardRankingRow> healthUnitDoctorRanking(Integer municipalityId, Integer periodId, Integer limit) {
        List<?> rows = em.createNativeQuery("""
                SELECT
                    hu.id,
                    hu.clues,
                    hu.name,
                    COALESCE(hus.total_doctors, 0) AS total_doctors,
                    mut.name AS unit_type,
                    hu.care_level
                FROM health_units hu
                LEFT JOIN health_unit_staff hus
                    ON hus.health_unit_id = hu.id
                   AND hus.period_id = :periodId
                LEFT JOIN medical_unit_types mut ON mut.id = hu.medical_unit_type_id
                WHERE hu.municipality_id = :municipalityId
                ORDER BY total_doctors DESC, hu.name ASC
                """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .setMaxResults((int) normalizeLimit(limit))
                .getResultList();

        AtomicInteger rank = new AtomicInteger(1);
        return rows.stream()
                .map(row -> {
                    Object[] values = (Object[]) row;
                    Long doctors = toLong(values[3]);
                    String unitType = toString(values[4]);
                    String careLevel = toString(values[5]);
                    return new DashboardRankingRow(
                            toString(values[0]),
                            rank.getAndIncrement(),
                            toString(values[1]),
                            toString(values[2]),
                            null,
                            doctors,
                            null,
                            null,
                            null,
                            unitType,
                            careLevel,
                            BigDecimal.valueOf(doctors),
                            null,
                            "neutral",
                            extra(
                                    "unitType", unitType,
                                    "careLevel", careLevel
                            )
                    );
                })
                .toList();
    }

    private List<DashboardRankingRow> healthUnitInfrastructureRanking(Integer municipalityId, Integer periodId, Integer limit) {
        List<?> rows = em.createNativeQuery("""
                SELECT
                    hu.id,
                    hu.clues,
                    hu.name,
                    COALESCE(SUM(CASE WHEN it.code = 'total_camas_hospitalizacion' THEN huid.quantity ELSE 0 END), 0) AS hospital_beds,
                    COALESCE(SUM(CASE WHEN it.code = 'total_consultorios' THEN huid.quantity ELSE 0 END), 0) AS consulting_rooms,
                    mut.name AS unit_type,
                    hu.care_level
                FROM health_units hu
                LEFT JOIN health_unit_infrastructure hui
                    ON hui.health_unit_id = hu.id
                   AND hui.period_id = :periodId
                LEFT JOIN health_unit_infrastructure_details huid
                    ON huid.health_unit_infrastructure_id = hui.id
                LEFT JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                LEFT JOIN medical_unit_types mut ON mut.id = hu.medical_unit_type_id
                WHERE hu.municipality_id = :municipalityId
                GROUP BY hu.id, hu.clues, hu.name, mut.name, hu.care_level
                ORDER BY hospital_beds DESC, consulting_rooms DESC, hu.name ASC
                """)
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .setMaxResults((int) normalizeLimit(limit))
                .getResultList();

        AtomicInteger rank = new AtomicInteger(1);
        return rows.stream()
                .map(row -> {
                    Object[] values = (Object[]) row;
                    Long hospitalBeds = toLong(values[3]);
                    Long consultingRooms = toLong(values[4]);
                    String unitType = toString(values[5]);
                    String careLevel = toString(values[6]);
                    return new DashboardRankingRow(
                            toString(values[0]),
                            rank.getAndIncrement(),
                            toString(values[1]),
                            toString(values[2]),
                            null,
                            null,
                            hospitalBeds,
                            consultingRooms,
                            null,
                            unitType,
                            careLevel,
                            BigDecimal.valueOf(hospitalBeds),
                            null,
                            "neutral",
                            extra(
                                    "unitType", unitType,
                                    "careLevel", careLevel
                            )
                    );
                })
                .toList();
    }

    private List<DashboardChartDataPoint> stateChart(Integer periodId, String indicatorCode, boolean higherIsBetter) {
        Integer year = analysisYear(periodId).orElse(null);
        if (year == null || !dataAvailabilityService.isIndicatorAvailable(indicatorCode, "state", year)) {
            return List.of();
        }

        return indicatorRepository.findStateValues(indicatorCode, year)
                .stream()
                .map(value -> chartPoint(value, higherIsBetter))
                .toList();
    }

    private List<DashboardChartDataPoint> municipalityChartByState(Integer stateId, Integer periodId, String indicatorCode, boolean higherIsBetter) {
        return stateCode(stateId)
                .map(code -> municipalityChart(code, periodId, indicatorCode, higherIsBetter))
                .orElse(List.of());
    }

    private List<DashboardChartDataPoint> municipalityChartBySiblingState(Integer municipalityId, Integer periodId, String indicatorCode, boolean higherIsBetter) {
        return stateCodeByMunicipality(municipalityId)
                .map(code -> municipalityChart(code, periodId, indicatorCode, higherIsBetter))
                .orElse(List.of());
    }

    private List<DashboardChartDataPoint> municipalityChart(String stateCode, Integer periodId, String indicatorCode, boolean higherIsBetter) {
        Integer year = analysisYear(periodId).orElse(null);
        if (year == null || !dataAvailabilityService.isIndicatorAvailable(indicatorCode, "municipality", year)) {
            return List.of();
        }

        return indicatorRepository.findMunicipalityValuesByState(indicatorCode, year, stateCode)
                .stream()
                .map(value -> chartPoint(value, higherIsBetter))
                .toList();
    }

    private DashboardRankingRow rankingRow(
            TerritoryIndicatorValueDto value,
            TerritoryIndicatorValueDto population,
            Long doctors,
            Long hospitalBeds,
            Long consultingRooms,
            TerritoryIndicatorValueDto coverage,
            Integer rank,
            boolean higherIsBetter
    ) {
        BigDecimal coverageIndex = coverage == null ? null : coverage.getValue();
        return new DashboardRankingRow(
                String.valueOf(value.getTerritoryId()),
                rank,
                value.getTerritoryCode(),
                value.getTerritoryName(),
                population == null || population.getValue() == null ? null : population.getValue().toBigInteger(),
                doctors,
                hospitalBeds,
                consultingRooms,
                coverageIndex,
                null,
                null,
                value.getValue(),
                level(value.getValue(), higherIsBetter),
                colorToken(value.getValue(), higherIsBetter),
                metadata(value)
        );
    }

    private boolean rankingNeedsDoctors(String indicatorCode) {
        return MEDICAL_COVERAGE.equals(indicatorCode) || HEALTHCARE_ACCESS_DEFICIENCY.equals(indicatorCode);
    }

    private boolean rankingNeedsHospitalBeds(String indicatorCode) {
        return HOSPITAL_BEDS.equals(indicatorCode);
    }

    private boolean rankingNeedsCoverageIndex(String indicatorCode) {
        return HEALTHCARE_ACCESS_DEFICIENCY.equals(indicatorCode);
    }

    private DashboardChartDataPoint chartPoint(TerritoryIndicatorValueDto value, boolean higherIsBetter) {
        return new DashboardChartDataPoint(
                value.getTerritoryName(),
                value.getTerritoryCode(),
                value.getValue(),
                null,
                null,
                null,
                null,
                value.getValue(),
                level(value.getValue(), higherIsBetter),
                colorToken(value.getValue(), higherIsBetter),
                metadata(value)
        );
    }

    private Map<String, Object> metadata(TerritoryIndicatorValueDto value) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("sourceYear", value.getSourceYear());
        extra.put("unit", value.getUnit());
        extra.put("availabilityStatus", value.getAvailabilityStatus());
        extra.put("methodologyNote", value.getMethodologyNote());
        extra.put("dataSourceName", value.getDataSourceName());
        return extra;
    }

    private Map<String, Object> extra(Object... keyValues) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();

        for (int i = 0; i < keyValues.length; i += 2) {
            String key = String.valueOf(keyValues[i]);
            Object value = keyValues[i + 1];

            if (value != null) {
                map.put(key, value);
            }
        }

        return map;
    }

    private Optional<DashboardPeriod> period(Integer periodId) {
        List<?> rows = em.createNativeQuery("""
                SELECT id, period_year
                FROM periods
                WHERE id = :periodId
                """)
                .setParameter("periodId", periodId)
                .setMaxResults(1)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = (Object[]) rows.get(0);
        return Optional.of(new DashboardPeriod(toInteger(row[0]), toInteger(row[1])));
    }

    private Optional<Integer> analysisYear(Integer periodId) {
        return indicatorRepository.findAnalysisYearByPeriodId(periodId);
    }

    private Optional<DashboardTerritory> stateTerritory(Integer stateId) {
        List<?> rows = em.createNativeQuery("""
                SELECT id, inegi_code, name
                FROM states
                WHERE id = :stateId
                """)
                .setParameter("stateId", stateId)
                .setMaxResults(1)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = (Object[]) rows.get(0);
        return Optional.of(new DashboardTerritory(
                toInteger(row[0]),
                toString(row[1]),
                toString(row[2]),
                "state"
        ));
    }

    private Optional<DashboardTerritory> municipalityTerritory(Integer municipalityId) {
        List<?> rows = em.createNativeQuery("""
                SELECT id, inegi_code, name
                FROM municipalities
                WHERE id = :municipalityId
                """)
                .setParameter("municipalityId", municipalityId)
                .setMaxResults(1)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = (Object[]) rows.get(0);
        return Optional.of(new DashboardTerritory(
                toInteger(row[0]),
                toString(row[1]),
                toString(row[2]),
                "municipality"
        ));
    }

    private BigInteger bigIntegerValue(String level, Integer stateId, Integer municipalityId, Integer year, String indicatorCode) {
        return indicatorRepository.findOne(level, stateId, municipalityId, year, indicatorCode)
                .map(TerritoryIndicatorValueDto::getValue)
                .map(BigDecimal::toBigInteger)
                .orElse(null);
    }

    private BigDecimal decimalValue(String level, Integer stateId, Integer municipalityId, Integer year, String indicatorCode) {
        return indicatorRepository.findOne(level, stateId, municipalityId, year, indicatorCode)
                .map(TerritoryIndicatorValueDto::getValue)
                .orElse(null);
    }

    private Long longValue(String level, Integer stateId, Integer municipalityId, Integer year, String indicatorCode) {
        return indicatorRepository.findOne(level, stateId, municipalityId, year, indicatorCode)
                .map(TerritoryIndicatorValueDto::getValue)
                .map(BigDecimal::longValue)
                .orElse(null);
    }

    private Map<String, Long> indicatorLongTotalsByState(String indicatorCode, Integer year) {
        return indicatorRepository.findStateValues(indicatorCode, year)
                .stream()
                .filter(value -> value.getValue() != null)
                .collect(Collectors.toMap(
                        TerritoryIndicatorValueDto::getTerritoryCode,
                        value -> value.getValue().longValue(),
                        (left, right) -> left
                ));
    }

    private Map<String, Long> indicatorLongTotalsByMunicipality(String indicatorCode, Integer year, String stateCode) {
        return indicatorRepository.findMunicipalityValuesByState(indicatorCode, year, stateCode)
                .stream()
                .filter(value -> value.getValue() != null)
                .collect(Collectors.toMap(
                        TerritoryIndicatorValueDto::getTerritoryCode,
                        value -> value.getValue().longValue(),
                        (left, right) -> left
                ));
    }

    private boolean higherIsBetter(String indicatorCode, boolean fallback) {
        List<?> rows = em.createNativeQuery("""
                SELECT higher_is_better
                FROM indicators
                WHERE code = :indicatorCode
                """)
                .setParameter("indicatorCode", indicatorCode)
                .setMaxResults(1)
                .getResultList();

        return rows.isEmpty() ? fallback : toBoolean(rows.get(0));
    }

    private BigDecimal averageValue(List<TerritoryIndicatorValueDto> values) {
        List<BigDecimal> present = values.stream()
                .map(TerritoryIndicatorValueDto::getValue)
                .filter(value -> value != null)
                .toList();

        if (present.isEmpty()) {
            return null;
        }

        BigDecimal sum = present.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(present.size()), 2, RoundingMode.HALF_UP);
    }

    private Long countStateValuesBelow(String indicatorCode, Integer year, BigDecimal threshold) {
        return indicatorRepository.findStateValues(indicatorCode, year)
                .stream()
                .filter(value -> value.getValue() != null && value.getValue().compareTo(threshold) < 0)
                .count();
    }

    private Long countStateValuesAbove(String indicatorCode, Integer year, BigDecimal threshold) {
        return indicatorRepository.findStateValues(indicatorCode, year)
                .stream()
                .filter(value -> value.getValue() != null && value.getValue().compareTo(threshold) > 0)
                .count();
    }

    private Long countMunicipalityValuesBelow(String indicatorCode, Integer year, String stateCode, BigDecimal threshold) {
        if (!dataAvailabilityService.isIndicatorAvailable(indicatorCode, "municipality", year)) {
            return 0L;
        }

        return indicatorRepository.findMunicipalityValuesByState(indicatorCode, year, stateCode)
                .stream()
                .filter(value -> value.getValue() != null && value.getValue().compareTo(threshold) < 0)
                .count();
    }

    private Long countMunicipalityValuesAbove(String indicatorCode, Integer year, String stateCode, BigDecimal threshold) {
        if (!dataAvailabilityService.isIndicatorAvailable(indicatorCode, "municipality", year)) {
            return 0L;
        }

        return indicatorRepository.findMunicipalityValuesByState(indicatorCode, year, stateCode)
                .stream()
                .filter(value -> value.getValue() != null && value.getValue().compareTo(threshold) > 0)
                .count();
    }

    private Long countHospitals(Integer stateId, Integer municipalityId) {
        String sql = healthUnitFilter("""
                SELECT COUNT(DISTINCT hu.id)
                FROM health_units hu
                JOIN municipalities m ON m.id = hu.municipality_id
                JOIN establishment_types et ON et.id = hu.establishment_type_id
                JOIN medical_unit_types mut ON mut.id = hu.medical_unit_type_id
                WHERE (
                    et.name = 'DE HOSPITALIZACION'
                    OR UPPER(mut.name) LIKE '%HOSPITAL%'
                )
                """, stateId, municipalityId);

        return singleLong(sql, stateId, municipalityId);
    }

    private List<DashboardChartDataPoint> infrastructureDistribution(Integer periodId, Integer stateId, Integer municipalityId) {
        String sql = healthUnitFilter("""
                SELECT
                    it.name AS label,
                    it.code AS code,
                    COALESCE(SUM(huid.quantity), 0) AS value
                FROM health_units hu
                JOIN municipalities m ON m.id = hu.municipality_id
                JOIN health_unit_infrastructure hui ON hui.health_unit_id = hu.id
                JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                JOIN infrastructure_types it ON it.id = huid.infrastructure_type_id
                WHERE hui.period_id = :periodId
                """, stateId, municipalityId) + """
                GROUP BY it.name, it.code
                ORDER BY value DESC
                """;

        return chartRows(em.createNativeQuery(sql)
                .setParameter("periodId", periodId)
                .setParameter("stateId", stateId)
                .setParameter("municipalityId", municipalityId)
                .getResultList());
    }

    private List<DashboardChartDataPoint> chartRows(List<?> rows) {
        return rows.stream()
                .map(row -> {
                    Object[] values = (Object[]) row;
                    BigDecimal value = toBigDecimalNullable(values[2]);
                    return new DashboardChartDataPoint(
                            toString(values[0]),
                            toString(values[1]),
                            value,
                            null,
                            null,
                            null,
                            null,
                            value,
                            null,
                            null,
                            Map.of()
                    );
                })
                .toList();
    }

    private String healthUnitFilter(String baseSql, Integer stateId, Integer municipalityId) {
        return baseSql + """
                AND (:stateId IS NULL OR m.state_id = :stateId)
                AND (:municipalityId IS NULL OR hu.municipality_id = :municipalityId)
                """;
    }

    private Long singleLong(String sql, Integer stateId, Integer municipalityId) {
        return toLong(em.createNativeQuery(sql)
                .setParameter("stateId", stateId)
                .setParameter("municipalityId", municipalityId)
                .getSingleResult());
    }

    private Optional<String> stateCode(Integer stateId) {
        List<?> rows = em.createNativeQuery("""
                SELECT inegi_code
                FROM states
                WHERE id = :stateId
                """)
                .setParameter("stateId", stateId)
                .setMaxResults(1)
                .getResultList();

        return rows.isEmpty() ? Optional.empty() : Optional.of(toString(rows.get(0)));
    }

    private Optional<String> stateCodeByMunicipality(Integer municipalityId) {
        List<?> rows = em.createNativeQuery("""
                SELECT s.inegi_code
                FROM municipalities m
                JOIN states s ON s.id = m.state_id
                WHERE m.id = :municipalityId
                """)
                .setParameter("municipalityId", municipalityId)
                .setMaxResults(1)
                .getResultList();

        return rows.isEmpty() ? Optional.empty() : Optional.of(toString(rows.get(0)));
    }

    private String predominantCareLevel(Integer municipalityId) {
        List<?> rows = em.createNativeQuery("""
                SELECT hu.care_level
                FROM health_units hu
                WHERE hu.municipality_id = :municipalityId
                GROUP BY hu.care_level
                ORDER BY COUNT(*) DESC
                """)
                .setParameter("municipalityId", municipalityId)
                .setMaxResults(1)
                .getResultList();

        return rows.isEmpty() ? null : toString(rows.get(0));
    }

    private BigDecimal averagePerUnit(Long numerator, Long denominator) {
        if (numerator == null || denominator == null || denominator == 0L) {
            return null;
        }

        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private int compareValues(BigDecimal left, BigDecimal right, boolean higherIsBetter) {
        return higherIsBetter ? right.compareTo(left) : left.compareTo(right);
    }

    private long normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 10L;
        }

        return Math.min(limit, 100);
    }

    private String level(BigDecimal value, boolean higherIsBetter) {
        if (value == null) {
            return "no_data";
        }

        if (higherIsBetter) {
            if (value.compareTo(BigDecimal.valueOf(2)) >= 0) {
                return "good";
            }
            if (value.compareTo(BigDecimal.ONE) >= 0) {
                return "risk";
            }
            return "critical";
        }

        if (value.compareTo(BigDecimal.valueOf(20)) <= 0) {
            return "good";
        }
        if (value.compareTo(BigDecimal.valueOf(40)) < 0) {
            return "risk";
        }
        return "critical";
    }

    private String colorToken(BigDecimal value, boolean higherIsBetter) {
        return switch (level(value, higherIsBetter)) {
            case "good" -> "green";
            case "risk" -> "yellow";
            case "critical" -> "red";
            default -> "neutral";
        };
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

    private String toString(Object value) {
        return value == null ? null : value.toString();
    }

    private boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }
}
