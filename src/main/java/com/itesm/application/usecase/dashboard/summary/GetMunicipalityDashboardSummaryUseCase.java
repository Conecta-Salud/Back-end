package com.itesm.application.usecase.dashboard.summary;

import com.itesm.application.dto.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.*;
import com.itesm.domain.models.dashboard.summary.municipality.MunicipalityHealthcareAccessDeficiencyMetrics;
import com.itesm.domain.models.dashboard.summary.municipality.MunicipalityHospitalBedsMetrics;
import com.itesm.domain.models.dashboard.summary.municipality.MunicipalityMedicalCoverageMetrics;
import com.itesm.domain.repository.DashboardSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class GetMunicipalityDashboardSummaryUseCase {

    private static final String DEFAULT_VARIANT = "default";
    private static final String COUNT_UNIT = "count";
    private static final String GREEN = "green";
    private static final String YELLOW = "yellow";
    private static final String NEUTRAL = "neutral";
    private static final String DOCTORS_PER_1000 = "doctors_per_1000";
    private static final String HEALTH_UNIT_LABEL = "Health unit";
    private static final String CLUES_LABEL = "CLUES";
    private static final String UNIT_TYPE_KEY = "unitType";
    private static final String UNIT_TYPE_LABEL = "Unit type";
    private static final String CARE_LEVEL_KEY = "careLevel";
    private static final String CARE_LEVEL_LABEL = "Care level";

    private final DashboardSummaryRepository dashboardSummaryRepository;

    @Inject
    public GetMunicipalityDashboardSummaryUseCase(DashboardSummaryRepository dashboardSummaryRepository) {
        this.dashboardSummaryRepository = dashboardSummaryRepository;
    }

    public DashboardSummaryDto execute(Integer municipalityId, Integer periodId, String categoryParam) {
        if (municipalityId == null) {
            throw new BadRequestException("municipalityId is required");
        }

        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        DashboardCategory category = DashboardCategory.fromString(categoryParam);

        if (!dashboardSummaryRepository.existsPeriodById(periodId)) {
            throw new NotFoundException("Period not found with id: " + periodId);
        }

        if (!dashboardSummaryRepository.existsMunicipalityById(municipalityId)) {
            throw new NotFoundException("Municipality not found with id: " + municipalityId);
        }

        if (category == DashboardCategory.MEDICAL_COVERAGE) {
            return buildMunicipalityMedicalCoverageSummary(municipalityId, periodId, category);
        }

        if (category == DashboardCategory.HOSPITAL_BEDS) {
            return buildMunicipalityHospitalBedsSummary(municipalityId, periodId, category);
        }

        if (category == DashboardCategory.HEALTHCARE_ACCESS_DEFICIENCY) {
            return buildMunicipalityHealthcareAccessDeficiencySummary(municipalityId, periodId, category);
        }

        throw new BadRequestException("Unsupported category in this block: " + category.getValue());
    }

    // ============================ Poblacion Vulnerable ============================
    private DashboardSummaryDto buildMunicipalityHealthcareAccessDeficiencySummary(
            Integer municipalityId,
            Integer periodId,
            DashboardCategory category
    ) {
        MunicipalityHealthcareAccessDeficiencyMetrics metrics = dashboardSummaryRepository
                .findMunicipalityHealthcareAccessDeficiencyMetrics(municipalityId, periodId)
                .orElseThrow(() -> new NotFoundException("No municipality healthcare access deficiency data found for municipalityId: " + municipalityId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findMunicipalityHealthcareAccessDeficiencyRanking(municipalityId, periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findMunicipalityHealthcareAccessDeficiencyMainChart(municipalityId, periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findMunicipalityHealthcareAccessDistribution(municipalityId, periodId);

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
            MunicipalityHealthcareAccessDeficiencyMetrics metrics
    ) {
        return List.of(
                new DashboardKpi(
                        "total_population",
                        "Population",
                        toDecimalOrNull(metrics.getTotalPopulation()),
                        "people",
                        DEFAULT_VARIANT,
                        1
                ),
                new DashboardKpi(
                        "available_doctors",
                        "Available doctors",
                        toDecimalOrNull(metrics.getAvailableDoctors()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        2
                ),
                new DashboardKpi(
                        "health_centers",
                        "Health centers",
                        toDecimalOrNull(metrics.getHealthCenters()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getHealthCenters(), GREEN, "red"),
                        3
                ),
                new DashboardKpi(
                        "coverage_index",
                        "Coverage index",
                        toDecimalOrNull(metrics.getCoverageIndex()),
                        DOCTORS_PER_1000,
                        getVariantFromMedicalCoverage(metrics.getCoverageIndex()),
                        4
                )
        );
    }

    private DashboardRanking buildHealthcareAccessDeficiencyRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Available health units",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", HEALTH_UNIT_LABEL),
                        new DashboardRankingColumn("doctors", "Doctors"),
                        new DashboardRankingColumn("code", CLUES_LABEL),
                        new DashboardRankingColumn(UNIT_TYPE_KEY, UNIT_TYPE_LABEL),
                        new DashboardRankingColumn(CARE_LEVEL_KEY, CARE_LEVEL_LABEL)
                ),
                rows
        );
    }

    private DashboardChart buildHealthcareAccessDeficiencyMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "Doctors by health unit",
                null,
                null,
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
    private DashboardSummaryDto buildMunicipalityHospitalBedsSummary(
            Integer municipalityId,
            Integer periodId,
            DashboardCategory category
    ) {
        MunicipalityHospitalBedsMetrics metrics = dashboardSummaryRepository
                .findMunicipalityHospitalBedsMetrics(municipalityId, periodId)
                .orElseThrow(() -> new NotFoundException("No municipality hospital beds data found for municipalityId: " + municipalityId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findMunicipalityHospitalBedsRanking(municipalityId, periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findMunicipalityHospitalBedsMainChart(municipalityId, periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findMunicipalityInfrastructureDistribution(municipalityId, periodId);

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

    private List<DashboardKpi> buildHospitalBedsKpis(MunicipalityHospitalBedsMetrics metrics) {
        return List.of(
                new DashboardKpi(
                        "total_hospitals",
                        "Hospitals",
                        toDecimalOrNull(metrics.getTotalHospitals()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getTotalHospitals(), GREEN, "red"),
                        1
                ),
                new DashboardKpi(
                        "total_consulting_rooms",
                        "Consulting rooms",
                        toDecimalOrNull(metrics.getTotalConsultingRooms()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        2
                ),
                new DashboardKpi(
                        "total_hospital_beds",
                        "Hospital beds",
                        toDecimalOrNull(metrics.getTotalHospitalBeds()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getTotalHospitalBeds(), GREEN, "red"),
                        3
                ),
                new DashboardKpi(
                        "predominant_care_level",
                        "Predominant care level",
                        null,
                        metrics.getPredominantCareLevel(),
                        DEFAULT_VARIANT,
                        4
                )
        );
    }

    private DashboardRanking buildHospitalBedsRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Health units by hospital infrastructure",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", HEALTH_UNIT_LABEL),
                        new DashboardRankingColumn("hospitalBeds", "Hospital beds"),
                        new DashboardRankingColumn("consultingRooms", "Consulting rooms"),
                        new DashboardRankingColumn("code", CLUES_LABEL),
                        new DashboardRankingColumn(UNIT_TYPE_KEY, UNIT_TYPE_LABEL),
                        new DashboardRankingColumn(CARE_LEVEL_KEY, CARE_LEVEL_LABEL)
                ),
                rows
        );
    }

    private DashboardChart buildHospitalBedsMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "Hospital beds by health unit",
                null,
                null,
                null,
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

    // ============================ Cobertura Medica ============================
    private DashboardSummaryDto buildMunicipalityMedicalCoverageSummary(
            Integer municipalityId,
            Integer periodId,
            DashboardCategory category
    ) {
        MunicipalityMedicalCoverageMetrics metrics = dashboardSummaryRepository
                .findMunicipalityMedicalCoverageMetrics(municipalityId, periodId)
                .orElseThrow(() -> new NotFoundException("No municipality medical coverage data found for municipalityId: " + municipalityId));

        List<DashboardRankingRow> rankingRows = dashboardSummaryRepository
                .findMunicipalityMedicalCoverageRanking(municipalityId, periodId, 10);

        List<DashboardChartDataPoint> mainChartData = dashboardSummaryRepository
                .findMunicipalityMedicalCoverageMainChart(municipalityId, periodId);

        List<DashboardChartDataPoint> secondaryChartData = dashboardSummaryRepository
                .findMunicipalitySpecialtiesDistribution(municipalityId, periodId);

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

    private List<DashboardKpi> buildMedicalCoverageKpis(MunicipalityMedicalCoverageMetrics metrics) {
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
                        "total_doctors",
                        "Total doctors",
                        toDecimalOrNull(metrics.getTotalDoctors()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        2
                ),
                new DashboardKpi(
                        "available_consulting_rooms",
                        "Available consulting rooms",
                        toDecimalOrNull(metrics.getTotalConsultingRooms()),
                        COUNT_UNIT,
                        DEFAULT_VARIANT,
                        3
                ),
                new DashboardKpi(
                        "available_hospitals",
                        "Available hospitals",
                        toDecimalOrNull(metrics.getTotalHospitals()),
                        COUNT_UNIT,
                        getVariantFromPositiveCount(metrics.getTotalHospitals(), GREEN, "red"),
                        4
                )
        );
    }

    private DashboardRanking buildMedicalCoverageRanking(List<DashboardRankingRow> rows) {
        return new DashboardRanking(
                "Health units by available doctors",
                List.of(
                        new DashboardRankingColumn("rank", "Rank"),
                        new DashboardRankingColumn("name", HEALTH_UNIT_LABEL),
                        new DashboardRankingColumn("doctors", "Doctors"),
                        new DashboardRankingColumn("code", CLUES_LABEL),
                        new DashboardRankingColumn(UNIT_TYPE_KEY, UNIT_TYPE_LABEL),
                        new DashboardRankingColumn(CARE_LEVEL_KEY, CARE_LEVEL_LABEL)
                ),
                rows
        );
    }

    private DashboardChart buildMedicalCoverageMainChart(List<DashboardChartDataPoint> data) {
        return new DashboardChart(
                "bar",
                "Doctors by health unit",
                null,
                null,
                null,
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
