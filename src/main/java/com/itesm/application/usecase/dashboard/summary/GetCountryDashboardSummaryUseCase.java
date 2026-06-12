package com.itesm.application.usecase.dashboard.summary;

import com.itesm.application.dto.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.country.CountryHealthcareAccessDeficiencyMetrics;
import com.itesm.domain.models.dashboard.summary.country.CountryHospitalBedsMetrics;
import com.itesm.domain.models.dashboard.summary.country.CountryMedicalCoverageMetrics;
import com.itesm.domain.repository.DashboardSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class GetCountryDashboardSummaryUseCase {

    private static final String COUNTRY_NAME = "MÉXICO";
    private static final String COUNTRY_TYPE = "country";
    private static final String DEFAULT_VARIANT = "default";
    private static final String COUNT_UNIT = "count";
    private static final String GREEN = "green";
    private static final String YELLOW = "yellow";
    private static final String NEUTRAL = "neutral";
    private static final String DOCTORS_PER_1000 = "doctors_per_1000";
    private static final String STATE_LABEL = "State";
    private static final String POPULATION_KEY = "population";
    private static final String POPULATION_LABEL = "Population";
    private static final String DOCTORS_KEY = "doctors";
    private static final String VALUE_KEY = "value";

    private final DashboardSummaryRepository dashboardSummaryRepository;

    @Inject
    public GetCountryDashboardSummaryUseCase(DashboardSummaryRepository dashboardSummaryRepository) {
        this.dashboardSummaryRepository = dashboardSummaryRepository;
    }

    public DashboardSummaryDto execute(Integer periodId, String categoryParam) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        DashboardCategory category = DashboardCategory.fromString(categoryParam);

        if (!dashboardSummaryRepository.existsPeriodById(periodId)) {
            throw new NotFoundException("Period not found with id: " + periodId);
        }

        if (category == DashboardCategory.MEDICAL_COVERAGE) {
            return buildCountryMedicalCoverageSummary(periodId, category);
        }

        if (category == DashboardCategory.HOSPITAL_BEDS) {
            return buildCountryHospitalBedsSummary(periodId, category);
        }

        if (category == DashboardCategory.HEALTHCARE_ACCESS_DEFICIENCY) {
            return buildCountryHealthcareAccessDeficiencySummary(periodId, category);
        }

        throw new BadRequestException("Unsupported category in this block: " + category.getValue());
    }

    // ============================ Poblacion Vulnerable ============================
    private DashboardSummaryDto buildCountryHealthcareAccessDeficiencySummary(
            Integer periodId,
            DashboardCategory category
    ) {
        CountryHealthcareAccessDeficiencyMetrics metrics = dashboardSummaryRepository
                .findCountryHealthcareAccessDeficiencyMetrics(periodId)
                .orElseThrow(() -> new NotFoundException("No country healthcare access deficiency data found for periodId: " + periodId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findCountryHealthcareAccessDeficiencyRanking(periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findCountryHealthcareAccessDeficiencyMainChart(periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findCountryHealthcareAccessDistribution(periodId);

        DashboardSummary summary = new DashboardSummary(
                new DashboardTerritory(null, null, COUNTRY_NAME, COUNTRY_TYPE),
                metrics.getPeriod(),
                category,
                buildHealthcareAccessDeficiencyKpis(metrics),
                buildHealthcareAccessDeficiencyRanking(rankingRows),
                buildHealthcareAccessDeficiencyMainChart(mainChartData),
                buildHealthcareAccessDeficiencySecondaryChart(secondaryChartData)
        );

        return toDto(summary);
    }

    private List<DashboardKpi> buildHealthcareAccessDeficiencyKpis(
            CountryHealthcareAccessDeficiencyMetrics metrics
    ) {
        return List.of(
                new DashboardKpi(
                        "total_population",
                        "Total population",
                        toDecimalOrNull(metrics.getTotalPopulation()),
                        "people",
                        DEFAULT_VARIANT,
                        1
                ),
                new DashboardKpi(
                        "vulnerable_population",
                        "Vulnerable population",
                        toDecimalOrNull(metrics.getVulnerablePopulation()),
                        "people",
                        "red",
                        2
                ),
                new DashboardKpi(
                        "priority_states",
                        "Priority states",
                        toDecimalOrNull(metrics.getPriorityStates()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getPriorityStates(), "red", GREEN),
                        3
                ),
                new DashboardKpi(
                        "medical_coverage_index",
                        "Medical coverage index",
                        toDecimalOrNull(metrics.getMedicalCoverageIndex()),
                        DOCTORS_PER_1000,
                        getVariantFromMedicalCoverage(metrics.getMedicalCoverageIndex()),
                        4
                )
        );
    }

    private DashboardRanking buildHealthcareAccessDeficiencyRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Priority states for healthcare investment",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", STATE_LABEL),
                        new DashboardRankingColumn(POPULATION_KEY, POPULATION_LABEL),
                        new DashboardRankingColumn(DOCTORS_KEY, "Doctors"),
                        new DashboardRankingColumn("coverageIndex", "Coverage index"),
                        new DashboardRankingColumn(VALUE_KEY, "Deficiency rate")
                ),
                rows
        );
    }

    private DashboardChart buildHealthcareAccessDeficiencyMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "Population vs doctors",
                POPULATION_KEY,
                DOCTORS_KEY,
                null,
                data
        );
    }

    private DashboardChart buildHealthcareAccessDeficiencySecondaryChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "Healthcare access distribution",
                null,
                null,
                null,
                data
        );
    }

    // ============================ Infraestructura Hospitalaria ============================
    private DashboardSummaryDto buildCountryHospitalBedsSummary(
            Integer periodId,
            DashboardCategory category
    ) {
        CountryHospitalBedsMetrics metrics = dashboardSummaryRepository
                .findCountryHospitalBedsMetrics(periodId)
                .orElseThrow(() -> new NotFoundException("No country hospital beds data found for periodId: " + periodId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findCountryHospitalBedsRanking(periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findCountryHospitalBedsMainChart(periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findCountryInfrastructureDistribution(periodId);

        DashboardSummary summary = new DashboardSummary(
                new DashboardTerritory(null, null, COUNTRY_NAME, COUNTRY_TYPE),
                metrics.getPeriod(),
                category,
                buildHospitalBedsKpis(metrics),
                buildHospitalBedsRanking(rankingRows),
                buildHospitalBedsMainChart(mainChartData),
                buildHospitalBedsSecondaryChart(secondaryChartData)
        );

        return toDto(summary);
    }

    private List<DashboardKpi> buildHospitalBedsKpis(CountryHospitalBedsMetrics metrics) {
        return List.of(
                new DashboardKpi(
                        "hospital_beds_per_1000",
                        "Hospital beds per 1,000 inhabitants",
                        metrics.getHospitalBedsPer1000(),
                        "hospital_beds_per_1000",
                        getVariantFromHospitalBeds(metrics.getHospitalBedsPer1000()),
                        1
                ),
                new DashboardKpi(
                        "states_with_hospital_deficit",
                        "States with hospital deficit",
                        toDecimalOrNull(metrics.getStatesWithHospitalDeficit()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getStatesWithHospitalDeficit(), YELLOW, GREEN),
                        2
                ),
                new DashboardKpi(
                        "total_hospitals",
                        "Total hospitals",
                        toDecimalOrNull(metrics.getTotalHospitals()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        3
                ),
                new DashboardKpi(
                        "average_beds_per_hospital",
                        "Average beds per hospital",
                        metrics.getAverageBedsPerHospital(),
                        "beds_per_hospital",
                        DEFAULT_VARIANT,
                        4
                )
        );
    }

    private DashboardRanking buildHospitalBedsRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "States with lowest hospital capacity",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", STATE_LABEL),
                        new DashboardRankingColumn("hospitalBeds", "Hospital beds"),
                        new DashboardRankingColumn(POPULATION_KEY, POPULATION_LABEL),
                        new DashboardRankingColumn(VALUE_KEY, "Beds / 1,000")
                ),
                rows
        );
    }

    private DashboardChart buildHospitalBedsMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "States vs hospital beds per 1,000 inhabitants",
                null,
                null,
                new DashboardReferenceLine(
                        BigDecimal.valueOf(3.0),
                        "Recommended reference"
                ),
                data
        );
    }

    private DashboardChart buildHospitalBedsSecondaryChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "pie",
                "Infrastructure distribution",
                null,
                null,
                null,
                data
        );
    }

    private String getVariantFromHospitalBeds(BigDecimal value) {
        if (value == null) {
            return NEUTRAL;
        }

        double number = value.doubleValue();

        if (number >= 3.0) {
            return GREEN;
        }

        if (number >= 1.0) {
            return YELLOW;
        }

        return "red";
    }

    // ============================ Cobertura Medica ============================
    private DashboardSummaryDto buildCountryMedicalCoverageSummary(
            Integer periodId,
            DashboardCategory category
    ) {
        CountryMedicalCoverageMetrics metrics = dashboardSummaryRepository
                .findCountryMedicalCoverageMetrics(periodId)
                .orElseThrow(() -> new NotFoundException("No country medical coverage data found for periodId: " + periodId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findCountryMedicalCoverageRanking(periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findCountryMedicalCoverageMainChart(periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findCountrySpecialtiesDistribution(periodId);

        DashboardSummary summary = new DashboardSummary(
                new DashboardTerritory(null, null, COUNTRY_NAME, COUNTRY_TYPE),
                metrics.getPeriod(),
                category,
                buildMedicalCoverageKpis(metrics),
                buildMedicalCoverageRanking(rankingRows),
                buildMedicalCoverageMainChart(mainChartData),
                buildMedicalCoverageSecondaryChart(secondaryChartData)
        );

        return toDto(summary);
    }

    private List<DashboardKpi> buildMedicalCoverageKpis(CountryMedicalCoverageMetrics metrics) {
        return List.of(
                new DashboardKpi(
                        DOCTORS_PER_1000,
                        "Doctors per 1,000 inhabitants",
                        metrics.getDoctorsPer1000(),
                        DOCTORS_PER_1000,
                        getVariantFromMedicalCoverage(metrics.getDoctorsPer1000()),
                        1
                ),
                new DashboardKpi(
                        "critical_states",
                        "Critical states",
                        toDecimalOrNull(metrics.getCriticalStates()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getCriticalStates(), "red", GREEN),
                        2
                ),
                new DashboardKpi(
                        "total_doctors",
                        "Total doctors",
                        toDecimalOrNull(metrics.getTotalDoctors()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        3
                ),
                new DashboardKpi(
                        "average_state_medical_coverage",
                        "Average state medical coverage",
                        metrics.getAverageStateMedicalCoverage(),
                        DOCTORS_PER_1000,
                        getVariantFromMedicalCoverage(metrics.getAverageStateMedicalCoverage()),
                        4
                )
        );
    }

    private DashboardRanking buildMedicalCoverageRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "States with lowest medical coverage",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", STATE_LABEL),
                        new DashboardRankingColumn(DOCTORS_KEY, "Doctors"),
                        new DashboardRankingColumn(POPULATION_KEY, POPULATION_LABEL),
                        new DashboardRankingColumn(VALUE_KEY, "Doctors / 1,000")
                ),
                rows
        );
    }

    private DashboardChart buildMedicalCoverageMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "States vs doctors per 1,000 inhabitants",
                null,
                null,
                new DashboardReferenceLine(
                        BigDecimal.valueOf(2.7),
                        "Recommended reference"
                ),
                data
        );
    }

    private DashboardChart buildMedicalCoverageSecondaryChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "pie",
                "Distribution of specialties",
                null,
                null,
                null,
                data
        );
    }

    private String getVariantFromMedicalCoverage(BigDecimal value) {
        if (value == null) {
            return NEUTRAL;
        }

        double number = value.doubleValue();

        if (number >= 2.7) {
            return GREEN;
        }

        if (number >= 1.0) {
            return YELLOW;
        }

        return "red";
    }

    private BigDecimal toDecimalOrNull(Long value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    private BigDecimal toDecimalOrNull(BigInteger value) {
        return value == null ? null : new BigDecimal(value);
    }

    private BigDecimal toDecimalOrNull(BigDecimal value) {
        return value;
    }

    private String getVariantFromPositiveCount(Long value, String positiveVariant, String zeroVariant) {
        if (value == null) {
            return NEUTRAL;
        }

        return value > 0 ? positiveVariant : zeroVariant;
    }

    private DashboardSummaryDto toDto(DashboardSummary summary) {
        return new DashboardSummaryDto(
                toTerritoryDto(summary.getTerritory()),
                toPeriodDto(summary.getPeriod()),
                summary.getCategory().getValue(),
                summary.getKpis()
                        .stream()
                        .map(this::toKpiDto)
                        .toList(),
                toRankingDto(summary.getRanking()),
                toChartDto(summary.getMainChart()),
                toChartDto(summary.getSecondaryChart())
        );
    }

    private DashboardTerritoryDto toTerritoryDto(DashboardTerritory territory) {
        return new DashboardTerritoryDto(
                territory.getId(),
                territory.getCode(),
                territory.getName(),
                territory.getType()
        );
    }

    private DashboardPeriodDto toPeriodDto(DashboardPeriod period) {
        return new DashboardPeriodDto(
                period.getId(),
                period.getPeriodYear()
        );
    }

    private DashboardKpiDto toKpiDto(DashboardKpi kpi) {
        return new DashboardKpiDto(
                kpi.getId(),
                kpi.getLabel(),
                kpi.getValue(),
                kpi.getUnit(),
                kpi.getVariant(),
                kpi.getOrder()
        );
    }

    private DashboardRankingDto toRankingDto(DashboardRanking ranking) {
        return new DashboardRankingDto(
                ranking.getTitle(),
                ranking.getColumns()
                        .stream()
                        .map(column -> new DashboardRankingColumnDto(
                                column.getKey(),
                                column.getLabel()
                        ))
                        .toList(),
                ranking.getRows()
                        .stream()
                        .map(this::toRankingRowDto)
                        .toList()
        );
    }

    private DashboardRankingRowDto toRankingRowDto(DashboardRankingRow row) {
        return new DashboardRankingRowDto(
                row.getId(),
                row.getRank(),
                row.getCode(),
                row.getName(),
                row.getPopulation(),
                row.getDoctors(),
                row.getHospitalBeds(),
                row.getConsultingRooms(),
                row.getCoverageIndex(),
                row.getUnitType(),
                row.getCareLevel(),
                row.getValue(),
                row.getLevel(),
                row.getColorToken(),
                row.getExtra()
        );
    }

    private DashboardChartDto toChartDto(DashboardChart chart) {
        return new DashboardChartDto(
                chart.getType(),
                chart.getTitle(),
                chart.getxKey(),
                chart.getyKey(),
                chart.getReferenceLine() != null
                        ? new DashboardReferenceLineDto(
                        chart.getReferenceLine().getValue(),
                        chart.getReferenceLine().getLabel()
                )
                        : null,
                chart.getData()
                        .stream()
                        .map(this::toChartDataPointDto)
                        .toList()
        );
    }

    private DashboardChartDataPointDto toChartDataPointDto(DashboardChartDataPoint point) {
        return new DashboardChartDataPointDto(
                point.getLabel(),
                point.getCode(),
                point.getValue(),
                point.getPopulation(),
                point.getDoctors(),
                point.getHospitalBeds(),
                point.getConsultingRooms(),
                point.getCoverageIndex(),
                point.getLevel(),
                point.getColorToken(),
                point.getExtra()
        );
    }
}
