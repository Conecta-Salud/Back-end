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
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetCountryDashboardSummaryUseCase {

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
                new DashboardTerritory(null, null, "MÉXICO", "country"),
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
                        new BigDecimal(metrics.getTotalPopulation()),
                        "people",
                        "default",
                        1
                ),
                new DashboardKpi(
                        "vulnerable_population",
                        "Vulnerable population",
                        new BigDecimal(metrics.getVulnerablePopulation()),
                        "people",
                        "red",
                        2
                ),
                new DashboardKpi(
                        "priority_states",
                        "Priority states",
                        BigDecimal.valueOf(metrics.getPriorityStates()),
                        "count",
                        metrics.getPriorityStates() > 0 ? "red" : "green",
                        3
                ),
                new DashboardKpi(
                        "medical_coverage_index",
                        "Medical coverage index",
                        metrics.getMedicalCoverageIndex(),
                        "doctors_per_1000",
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
                        new DashboardRankingColumn("name", "State"),
                        new DashboardRankingColumn("population", "Population"),
                        new DashboardRankingColumn("doctors", "Doctors"),
                        new DashboardRankingColumn("coverageIndex", "Coverage index"),
                        new DashboardRankingColumn("value", "Deficiency rate")
                ),
                rows
        );
    }

    private DashboardChart buildHealthcareAccessDeficiencyMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "Population vs doctors",
                "population",
                "doctors",
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
                new DashboardTerritory(null, null, "MÉXICO", "country"),
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
                        BigDecimal.valueOf(metrics.getStatesWithHospitalDeficit()),
                        "count",
                        metrics.getStatesWithHospitalDeficit() > 0 ? "yellow" : "green",
                        2
                ),
                new DashboardKpi(
                        "total_hospitals",
                        "Total hospitals",
                        BigDecimal.valueOf(metrics.getTotalHospitals()),
                        "count",
                        "default",
                        3
                ),
                new DashboardKpi(
                        "average_beds_per_hospital",
                        "Average beds per hospital",
                        metrics.getAverageBedsPerHospital(),
                        "beds_per_hospital",
                        "default",
                        4
                )
        );
    }

    private DashboardRanking buildHospitalBedsRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "States with lowest hospital capacity",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", "State"),
                        new DashboardRankingColumn("hospitalBeds", "Hospital beds"),
                        new DashboardRankingColumn("population", "Population"),
                        new DashboardRankingColumn("value", "Beds / 1,000")
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
            return "neutral";
        }

        double number = value.doubleValue();

        if (number >= 3.0) {
            return "green";
        }

        if (number >= 1.0) {
            return "yellow";
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
                new DashboardTerritory(null, null, "MÉXICO", "country"),
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
                        "doctors_per_1000",
                        "Doctors per 1,000 inhabitants",
                        metrics.getDoctorsPer1000(),
                        "doctors_per_1000",
                        getVariantFromMedicalCoverage(metrics.getDoctorsPer1000()),
                        1
                ),
                new DashboardKpi(
                        "critical_states",
                        "Critical states",
                        BigDecimal.valueOf(metrics.getCriticalStates()),
                        "count",
                        metrics.getCriticalStates() > 0 ? "red" : "green",
                        2
                ),
                new DashboardKpi(
                        "total_doctors",
                        "Total doctors",
                        BigDecimal.valueOf(metrics.getTotalDoctors()),
                        "count",
                        "default",
                        3
                ),
                new DashboardKpi(
                        "average_state_medical_coverage",
                        "Average state medical coverage",
                        metrics.getAverageStateMedicalCoverage(),
                        "doctors_per_1000",
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
                        new DashboardRankingColumn("name", "State"),
                        new DashboardRankingColumn("doctors", "Doctors"),
                        new DashboardRankingColumn("population", "Population"),
                        new DashboardRankingColumn("value", "Doctors / 1,000")
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
            return "neutral";
        }

        double number = value.doubleValue();

        if (number >= 2.7) {
            return "green";
        }

        if (number >= 1.0) {
            return "yellow";
        }

        return "red";
    }

    private DashboardSummaryDto toDto(DashboardSummary summary) {
        return new DashboardSummaryDto(
                toTerritoryDto(summary.getTerritory()),
                toPeriodDto(summary.getPeriod()),
                summary.getCategory().getValue(),
                summary.getKpis()
                        .stream()
                        .map(this::toKpiDto)
                        .collect(Collectors.toList()),
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
                        .collect(Collectors.toList()),
                ranking.getRows()
                        .stream()
                        .map(this::toRankingRowDto)
                        .collect(Collectors.toList())
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
                        .collect(Collectors.toList())
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
