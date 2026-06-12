package com.itesm.application.usecase.dashboard.summary;

import com.itesm.application.dto.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.state.StateHealthcareAccessDeficiencyMetrics;
import com.itesm.domain.models.dashboard.summary.state.StateHospitalBedsMetrics;
import com.itesm.domain.models.dashboard.summary.state.StateMedicalCoverageMetrics;
import com.itesm.domain.repository.DashboardSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class GetStateDashboardSummaryUseCase {

    private static final String DEFAULT_VARIANT = "default";
    private static final String COUNT_UNIT = "count";
    private static final String GREEN = "green";
    private static final String YELLOW = "yellow";
    private static final String NEUTRAL = "neutral";
    private static final String DOCTORS_PER_1000 = "doctors_per_1000";
    private static final String MUNICIPALITY_LABEL = "Municipality";
    private static final String POPULATION_KEY = "population";
    private static final String POPULATION_LABEL = "Population";
    private static final String DOCTORS_KEY = "doctors";
    private static final String VALUE_KEY = "value";

    private final DashboardSummaryRepository dashboardSummaryRepository;

    @Inject
    public GetStateDashboardSummaryUseCase(DashboardSummaryRepository dashboardSummaryRepository) {
        this.dashboardSummaryRepository = dashboardSummaryRepository;
    }

    public DashboardSummaryDto execute(Integer stateId, Integer periodId, String categoryParam) {
        if (stateId == null) {
            throw new BadRequestException("stateId is required");
        }

        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        DashboardCategory category = DashboardCategory.fromString(categoryParam);

        if (!dashboardSummaryRepository.existsPeriodById(periodId)) {
            throw new NotFoundException("Period not found with id: " + periodId);
        }

        if (!dashboardSummaryRepository.existsStateById(stateId)) {
            throw new NotFoundException("State not found with id: " + stateId);
        }

        if (category == DashboardCategory.MEDICAL_COVERAGE) {
            return buildStateMedicalCoverageSummary(stateId, periodId, category);
        }

        if (category == DashboardCategory.HOSPITAL_BEDS) {
            return buildStateHospitalBedsSummary(stateId, periodId, category);
        }

        if (category == DashboardCategory.HEALTHCARE_ACCESS_DEFICIENCY) {
            return buildStateHealthcareAccessDeficiencySummary(stateId, periodId, category);
        }

        throw new BadRequestException("Unsupported category in this block: " + category.getValue());
    }

    // ============================ Poblacion Vulnerable ============================
    private DashboardSummaryDto buildStateHealthcareAccessDeficiencySummary(
            Integer stateId,
            Integer periodId,
            DashboardCategory category
    ) {
        StateHealthcareAccessDeficiencyMetrics metrics = dashboardSummaryRepository
                .findStateHealthcareAccessDeficiencyMetrics(stateId, periodId)
                .orElseThrow(() -> new NotFoundException("No state healthcare access deficiency data found for stateId: " + stateId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findStateHealthcareAccessDeficiencyRanking(stateId, periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findStateHealthcareAccessDeficiencyMainChart(stateId, periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findStateHealthcareAccessDistribution(stateId, periodId);

        DashboardSummary summary = new DashboardSummary(
                metrics.getTerritory(),
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
            StateHealthcareAccessDeficiencyMetrics metrics
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
                        "priority_municipalities",
                        "Priority municipalities",
                        toDecimalOrNull(metrics.getPriorityMunicipalities()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getPriorityMunicipalities(), "red", GREEN),
                        2
                ),
                new DashboardKpi(
                        "medical_coverage_index",
                        "Medical coverage index",
                        toDecimalOrNull(metrics.getMedicalCoverageIndex()),
                        DOCTORS_PER_1000,
                        getVariantFromMedicalCoverage(metrics.getMedicalCoverageIndex()),
                        3
                ),
                new DashboardKpi(
                        "available_infrastructure",
                        "Available infrastructure",
                        toDecimalOrNull(metrics.getAvailableInfrastructure()),
                        "health_units",
                        DEFAULT_VARIANT,
                        4
                )
        );
    }

    private DashboardRanking buildHealthcareAccessDeficiencyRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Priority municipalities for healthcare investment",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", MUNICIPALITY_LABEL),
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
    private DashboardSummaryDto buildStateHospitalBedsSummary(
            Integer stateId,
            Integer periodId,
            DashboardCategory category
    ) {
        StateHospitalBedsMetrics metrics = dashboardSummaryRepository
                .findStateHospitalBedsMetrics(stateId, periodId)
                .orElseThrow(() -> new NotFoundException("No state hospital beds data found for stateId: " + stateId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findStateHospitalBedsRanking(stateId, periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findStateHospitalBedsMainChart(stateId, periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findStateInfrastructureDistribution(stateId, periodId);

        DashboardSummary summary = new DashboardSummary(
                metrics.getTerritory(),
                metrics.getPeriod(),
                category,
                buildHospitalBedsKpis(metrics),
                buildHospitalBedsRanking(rankingRows),
                buildHospitalBedsMainChart(mainChartData),
                buildHospitalBedsSecondaryChart(secondaryChartData)
        );

        return toDto(summary);
    }

    private List<DashboardKpi> buildHospitalBedsKpis(StateHospitalBedsMetrics metrics) {
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
                        "municipalities_with_hospital_deficit",
                        "Municipalities with hospital deficit",
                        toDecimalOrNull(metrics.getMunicipalitiesWithHospitalDeficit()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getMunicipalitiesWithHospitalDeficit(), YELLOW, GREEN),
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
                        "total_consulting_rooms",
                        "Total consulting rooms",
                        toDecimalOrNull(metrics.getTotalConsultingRooms()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        4
                )
        );
    }

    private DashboardRanking buildHospitalBedsRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Municipalities with lowest hospital capacity",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", MUNICIPALITY_LABEL),
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
                "Municipalities vs hospital beds per 1,000 inhabitants",
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
    private DashboardSummaryDto buildStateMedicalCoverageSummary(
            Integer stateId,
            Integer periodId,
            DashboardCategory category
    ) {
        StateMedicalCoverageMetrics metrics = dashboardSummaryRepository
                .findStateMedicalCoverageMetrics(stateId, periodId)
                .orElseThrow(() -> new NotFoundException("No state medical coverage data found for stateId: " + stateId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findStateMedicalCoverageRanking(stateId, periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findStateMedicalCoverageMainChart(stateId, periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findStateSpecialtiesDistribution(stateId, periodId);

        DashboardSummary summary = new DashboardSummary(
                metrics.getTerritory(),
                metrics.getPeriod(),
                category,
                buildMedicalCoverageKpis(metrics),
                buildMedicalCoverageRanking(rankingRows),
                buildMedicalCoverageMainChart(mainChartData),
                buildMedicalCoverageSecondaryChart(secondaryChartData)
        );

        return toDto(summary);
    }

    private List<DashboardKpi> buildMedicalCoverageKpis(StateMedicalCoverageMetrics metrics) {
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
                        "critical_municipalities",
                        "Critical municipalities",
                        toDecimalOrNull(metrics.getCriticalMunicipalities()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getCriticalMunicipalities(), "red", GREEN),
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
                        "average_municipal_coverage",
                        "Average municipal coverage",
                        metrics.getAverageMunicipalCoverage(),
                        DOCTORS_PER_1000,
                        getVariantFromMedicalCoverage(metrics.getAverageMunicipalCoverage()),
                        4
                )
        );
    }

    private DashboardRanking buildMedicalCoverageRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Municipalities with lowest medical coverage",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", MUNICIPALITY_LABEL),
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
                "Municipalities vs doctors per 1,000 inhabitants",
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
                summary.getKpis().stream().map(this::toKpiDto).toList(),
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
                        .map(column -> new DashboardRankingColumnDto(column.getKey(), column.getLabel()))
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
