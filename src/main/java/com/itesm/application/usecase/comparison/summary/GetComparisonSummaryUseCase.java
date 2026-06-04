package com.itesm.application.usecase.comparison.summary;

import com.itesm.application.dto.comparison.summary.*;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.service.activity.ActivityActions;
import com.itesm.application.service.activity.ActivityLoggerService;
import com.itesm.application.service.activity.ActivityModules;
import com.itesm.domain.models.comparison.summary.*;
import com.itesm.domain.repository.ComparisonSummaryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetComparisonSummaryUseCase {

    private static final BigDecimal DOCTORS_REFERENCE_PER_1000 = BigDecimal.valueOf(2.7);
    private static final BigDecimal HOSPITAL_BEDS_REFERENCE_PER_1000 = BigDecimal.valueOf(3.0);
    private static final BigDecimal HOSPITALS_REFERENCE_PER_100K = BigDecimal.valueOf(5.0);
    private static final BigDecimal OLDER_ADULTS_REFERENCE_PERCENTAGE = BigDecimal.valueOf(20.0);

    private final ComparisonSummaryRepository comparisonSummaryRepository;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ActivityLoggerService activityLoggerService;

    @Inject
    public GetComparisonSummaryUseCase(
            ComparisonSummaryRepository comparisonSummaryRepository,
            AuthenticatedUserContext authenticatedUserContext,
            ActivityLoggerService activityLoggerService
    ) {
        this.comparisonSummaryRepository = comparisonSummaryRepository;
        this.authenticatedUserContext = authenticatedUserContext;
        this.activityLoggerService = activityLoggerService;
    }

    public ComparisonSummaryDto executeStates(Integer periodId, List<String> stateCodes) {
        validateCommon(periodId, stateCodes, "stateCodes");

        List<ComparisonRawItem> items = comparisonSummaryRepository
                .findStateComparisonItemsByCodes(periodId, stateCodes);

        validateResultSize(items, stateCodes, "states");

        ComparisonSummary summary = buildSummary(
                ComparisonLevel.STATE,
                items
        );

        ComparisonSummaryDto response = toDto(summary);

        logComparisonActivity(
                ActivityActions.COMPARE_STATES,
                "Compared states " + String.join(", ", stateCodes)
        );

        return response;
    }

    public ComparisonSummaryDto executeMunicipalities(Integer periodId, List<String> municipalityCodes) {
        validateCommon(periodId, municipalityCodes, "municipalityCodes");

        List<ComparisonRawItem> items = comparisonSummaryRepository
                .findMunicipalityComparisonItemsByCodes(periodId, municipalityCodes);

        validateResultSize(items, municipalityCodes, "municipalities");

        ComparisonSummary summary = buildSummary(
                ComparisonLevel.MUNICIPALITY,
                items
        );

        ComparisonSummaryDto response = toDto(summary);

        logComparisonActivity(
                ActivityActions.COMPARE_MUNICIPALITIES,
                "Compared municipalities " + String.join(", ", municipalityCodes)
        );

        return response;
    }

    private void logComparisonActivity(String action, String detail) {
        CurrentUser currentUser = authenticatedUserContext.getCurrentUser();

        activityLoggerService.logSuccess(
                currentUser.getUserId(),
                action,
                ActivityModules.COMPARISON,
                detail
        );
    }

    private void validateCommon(Integer periodId, List<String> codes, String paramName) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        if (!comparisonSummaryRepository.existsPeriodById(periodId)) {
            throw new NotFoundException("Period not found with id: " + periodId);
        }

        if (codes == null || codes.isEmpty()) {
            throw new BadRequestException(paramName + " is required");
        }

        if (codes.size() != 2) {
            throw new BadRequestException("Comparison summary requires exactly 2 territories");
        }

        boolean hasInvalidCode = codes.stream()
                .anyMatch(code -> code == null || code.isBlank());

        if (hasInvalidCode) {
            throw new BadRequestException(paramName + " cannot contain empty values");
        }

        long uniqueCodes = codes.stream()
                .map(String::trim)
                .distinct()
                .count();

        if (uniqueCodes != 2) {
            throw new BadRequestException("Comparison summary requires 2 different territories");
        }
    }

    private void validateResultSize(List<ComparisonRawItem> items, List<String> codes, String levelName) {
        if (items == null || items.size() != 2) {
            throw new NotFoundException(
                    "Could not find complete data for the selected " + levelName + ": " + codes
            );
        }
    }

    private boolean hasPovertyData(List<ComparisonRawItem> items) {
        return items.stream()
                .allMatch(item ->
                        item.getTotalPovertyPopulation() != null
                                && item.getTotalPopulation() != null
                                && item.getTotalPopulation().compareTo(BigInteger.ZERO) > 0
                );
    }

    private ComparisonSummary buildSummary(
            ComparisonLevel level,
            List<ComparisonRawItem> items
    ) {
        ComparisonPeriod period = items.get(0).getPeriod();

        List<ComparisonChart> charts = new java.util.ArrayList<>();

        charts.add(buildMedicalCoverageChart(items));
        charts.add(buildDoctorDeficitChart(items));
        charts.add(buildHospitalBedsChart(items));

        if (level == ComparisonLevel.STATE && hasPovertyData(items)) {
            charts.add(buildPovertyChart(items));
        }

        return new ComparisonSummary(
                period,
                level.getValue(),
                items.stream()
                        .map(ComparisonRawItem::getTerritory)
                        .collect(Collectors.toList()),
                charts,
                items.stream()
                        .map(this::buildPriorityResult)
                        .collect(Collectors.toList())
        );
    }

    private ComparisonChart buildMedicalCoverageChart(List<ComparisonRawItem> items) {
        List<ComparisonChartDataPoint> data = items.stream()
                .map(item -> {
                    BigDecimal medicalCoverage = calculateMedicalCoverage(item);
                    return new ComparisonChartDataPoint(
                            item.getTerritory().getCode(),
                            item.getTerritory().getName(),
                            item.getTerritory().getParentName(),
                            medicalCoverage,
                            variantHigherIsBetter(medicalCoverage, DOCTORS_REFERENCE_PER_1000, BigDecimal.ONE),
                            extra(
                                    "availabilityStatus", availabilityStatus(medicalCoverage),
                                    "totalDoctors", item.getTotalDoctors(),
                                    "totalPopulation", item.getTotalPopulation()
                            )
                    );
                })
                .collect(Collectors.toList());

        return new ComparisonChart(
                "medical_coverage",
                "Medical coverage",
                "bar",
                new ComparisonReferenceLine(
                        DOCTORS_REFERENCE_PER_1000,
                        "Minimum reference / 2.7"
                ),
                data
        );
    }

    private ComparisonChart buildDoctorDeficitChart(List<ComparisonRawItem> items) {
        List<ComparisonChartDataPoint> data = items.stream()
                .map(item -> {
                    BigDecimal medicalCoverage = calculateMedicalCoverage(item);
                    BigDecimal deficitPer1000 = medicalCoverage == null
                            ? null
                            : DOCTORS_REFERENCE_PER_1000
                                    .subtract(medicalCoverage)
                                    .max(BigDecimal.ZERO)
                                    .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal estimatedDoctorDeficit = calculateEstimatedDoctorDeficit(item);

                    return new ComparisonChartDataPoint(
                            item.getTerritory().getCode(),
                            item.getTerritory().getName(),
                            item.getTerritory().getParentName(),
                            deficitPer1000,
                            variantLowerIsBetter(deficitPer1000, BigDecimal.ONE, DOCTORS_REFERENCE_PER_1000),
                            extra(
                                    "availabilityStatus", availabilityStatus(deficitPer1000),
                                    "estimatedDoctorDeficit", estimatedDoctorDeficit,
                                    "medicalCoverage", medicalCoverage,
                                    "reference", DOCTORS_REFERENCE_PER_1000
                            )
                    );
                })
                .collect(Collectors.toList());

        return new ComparisonChart(
                "doctor_deficit",
                "Estimated doctor deficit",
                "bar",
                null,
                data
        );
    }

    private ComparisonChart buildHospitalBedsChart(List<ComparisonRawItem> items) {
        List<ComparisonChartDataPoint> data = items.stream()
                .map(item -> {
                    BigDecimal hospitalBedsPer1000 = calculateHospitalBedsPer1000(item);
                    return new ComparisonChartDataPoint(
                            item.getTerritory().getCode(),
                            item.getTerritory().getName(),
                            item.getTerritory().getParentName(),
                            hospitalBedsPer1000,
                            variantHigherIsBetter(hospitalBedsPer1000, HOSPITAL_BEDS_REFERENCE_PER_1000, BigDecimal.ONE),
                            extra(
                                    "availabilityStatus", availabilityStatus(hospitalBedsPer1000),
                                    "totalHospitalBeds", item.getTotalHospitalBeds(),
                                    "totalPopulation", item.getTotalPopulation()
                            )
                    );
                })
                .collect(Collectors.toList());

        return new ComparisonChart(
                "hospital_beds_per_1000",
                "Hospital beds per 1,000 inhabitants",
                "bar",
                new ComparisonReferenceLine(
                        HOSPITAL_BEDS_REFERENCE_PER_1000,
                        "Minimum reference / 3.0"
                ),
                data
        );
    }

    private ComparisonChart buildPovertyChart(List<ComparisonRawItem> items) {
        List<ComparisonChartDataPoint> data = items.stream()
                .map(item -> {
                    BigDecimal povertyRate = calculatePovertyRate(item);
                    return new ComparisonChartDataPoint(
                            item.getTerritory().getCode(),
                            item.getTerritory().getName(),
                            item.getTerritory().getParentName(),
                            povertyRate,
                            variantLowerIsBetter(povertyRate, BigDecimal.valueOf(20), BigDecimal.valueOf(40)),
                            extra(
                                    "totalPovertyPopulation", item.getTotalPovertyPopulation(),
                                    "totalPopulation", item.getTotalPopulation()
                            )
                    );
                })
                .collect(Collectors.toList());

        return new ComparisonChart(
                "poverty_rate",
                "Population in poverty",
                "bar",
                null,
                data
        );
    }

    private ComparisonPriorityResult buildPriorityResult(ComparisonRawItem item) {
        BigDecimal hospitalsPer100k = calculateHospitalsPer100k(item);
        BigDecimal medicalCoverage = calculateMedicalCoverage(item);
        BigDecimal olderAdultsPercentage = safeBigDecimal(item.getPercentageOver60());

        BigDecimal medicalRisk = calculateMedicalRisk(medicalCoverage);
        BigDecimal hospitalRisk = calculateHospitalRisk(hospitalsPer100k);
        BigDecimal olderAdultRisk = calculateOlderAdultRisk(olderAdultsPercentage);

        BigDecimal score = safeRisk(medicalRisk).multiply(BigDecimal.valueOf(0.45))
                .add(safeRisk(hospitalRisk).multiply(BigDecimal.valueOf(0.35)))
                .add(safeRisk(olderAdultRisk).multiply(BigDecimal.valueOf(0.20)))
                .setScale(2, RoundingMode.HALF_UP);

        PriorityClassification classification = classifyPriority(score);

        return new ComparisonPriorityResult(
                item.getTerritory().getCode(),
                item.getTerritory().getName(),
                item.getTerritory().getParentName(),
                score,
                classification.level(),
                classification.label(),
                classification.colorToken(),
                List.of(
                        new ComparisonPriorityFactor(
                                "hospitals_per_100k",
                                "Hospitals per population",
                                hospitalsPer100k,
                                "hospitals_per_100k",
                                variantHigherIsBetter(hospitalsPer100k, HOSPITALS_REFERENCE_PER_100K, BigDecimal.valueOf(2))
                        ),
                        new ComparisonPriorityFactor(
                                "medical_coverage",
                                "Medical coverage",
                                medicalCoverage,
                                "doctors_per_1000",
                                variantHigherIsBetter(medicalCoverage, DOCTORS_REFERENCE_PER_1000, BigDecimal.ONE)
                        ),
                        new ComparisonPriorityFactor(
                                "older_adults",
                                "Older adults",
                                olderAdultsPercentage,
                                "percentage",
                                variantLowerIsBetter(olderAdultsPercentage, BigDecimal.valueOf(12), BigDecimal.valueOf(20))
                        )
                )
        );
    }

    private BigDecimal calculateMedicalCoverage(ComparisonRawItem item) {
        return item.getDoctorsPer1000();
    }

    private BigDecimal calculateHospitalBedsPer1000(ComparisonRawItem item) {
        return item.getBedsPer1000();
    }

    private BigDecimal calculatePovertyRate(ComparisonRawItem item) {
        if (item.getTotalPovertyPopulation() == null || item.getTotalPopulation() == null) {
            return null;
        }

        return divide(
                new BigDecimal(item.getTotalPovertyPopulation()).multiply(BigDecimal.valueOf(100)),
                new BigDecimal(item.getTotalPopulation())
        );
    }

    private BigDecimal calculateHospitalsPer100k(ComparisonRawItem item) {
        if (item.getTotalPopulation() == null) {
            return null;
        }

        return divide(
                BigDecimal.valueOf(safeLong(item.getTotalHospitals())).multiply(BigDecimal.valueOf(100000)),
                new BigDecimal(item.getTotalPopulation())
        );
    }

    private BigDecimal calculateEstimatedDoctorDeficit(ComparisonRawItem item) {
        if (item.getTotalPopulation() == null || item.getTotalDoctors() == null) {
            return null;
        }

        BigDecimal requiredDoctors = new BigDecimal(item.getTotalPopulation())
                .multiply(DOCTORS_REFERENCE_PER_1000)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);

        return requiredDoctors
                .subtract(BigDecimal.valueOf(safeLong(item.getTotalDoctors())))
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
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

    private String availabilityStatus(BigDecimal value) {
        return value == null ? "not_available" : "available";
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal calculateMedicalRisk(BigDecimal medicalCoverage) {
        if (medicalCoverage == null) {
            return null;
        }

        return DOCTORS_REFERENCE_PER_1000
                .subtract(medicalCoverage)
                .max(BigDecimal.ZERO)
                .divide(DOCTORS_REFERENCE_PER_1000, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateHospitalRisk(BigDecimal hospitalsPer100k) {
        if (hospitalsPer100k == null) {
            return null;
        }

        return HOSPITALS_REFERENCE_PER_100K
                .subtract(hospitalsPer100k)
                .max(BigDecimal.ZERO)
                .divide(HOSPITALS_REFERENCE_PER_100K, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeRisk(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal calculateOlderAdultRisk(BigDecimal olderAdultsPercentage) {
        return olderAdultsPercentage
                .divide(OLDER_ADULTS_REFERENCE_PERCENTAGE, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return numerator.divide(denominator, 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String variantHigherIsBetter(
            BigDecimal value,
            BigDecimal goodReference,
            BigDecimal riskReference
    ) {
        if (value == null) return "neutral";

        if (value.compareTo(goodReference) >= 0) {
            return "green";
        }

        if (value.compareTo(riskReference) >= 0) {
            return "yellow";
        }

        return "red";
    }

    private String variantLowerIsBetter(
            BigDecimal value,
            BigDecimal goodMax,
            BigDecimal riskMax
    ) {
        if (value == null) return "neutral";

        if (value.compareTo(goodMax) <= 0) {
            return "green";
        }

        if (value.compareTo(riskMax) <= 0) {
            return "yellow";
        }

        return "red";
    }

    private PriorityClassification classifyPriority(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return new PriorityClassification("high", "High", "red");
        }

        if (score.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return new PriorityClassification("medium", "Medium", "yellow");
        }

        return new PriorityClassification("low", "Low", "green");
    }

    private ComparisonSummaryDto toDto(ComparisonSummary summary) {
        return new ComparisonSummaryDto(
                toPeriodDto(summary.getPeriod()),
                summary.getLevel(),
                summary.getTerritories()
                        .stream()
                        .map(this::toTerritoryDto)
                        .collect(Collectors.toList()),
                summary.getCharts()
                        .stream()
                        .map(this::toChartDto)
                        .collect(Collectors.toList()),
                summary.getPriority()
                        .stream()
                        .map(this::toPriorityResultDto)
                        .collect(Collectors.toList())
        );
    }

    private ComparisonPeriodDto toPeriodDto(ComparisonPeriod period) {
        return new ComparisonPeriodDto(
                period.getId(),
                period.getPeriodYear()
        );
    }

    private ComparisonTerritoryDto toTerritoryDto(ComparisonTerritory territory) {
        return new ComparisonTerritoryDto(
                territory.getId(),
                territory.getCode(),
                territory.getName(),
                territory.getParentName(),
                territory.getType()
        );
    }

    private ComparisonChartDto toChartDto(ComparisonChart chart) {
        return new ComparisonChartDto(
                chart.getId(),
                chart.getTitle(),
                chart.getType(),
                chart.getReferenceLine() != null
                        ? new ComparisonReferenceLineDto(
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

    private ComparisonChartDataPointDto toChartDataPointDto(ComparisonChartDataPoint point) {
        return new ComparisonChartDataPointDto(
                point.getTerritoryCode(),
                point.getLabel(),
                point.getSubtitle(),
                point.getValue(),
                point.getVariant(),
                point.getExtra()
        );
    }

    private ComparisonPriorityResultDto toPriorityResultDto(ComparisonPriorityResult result) {
        return new ComparisonPriorityResultDto(
                result.getTerritoryCode(),
                result.getName(),
                result.getParentName(),
                result.getScore(),
                result.getLevel(),
                result.getLabel(),
                result.getColorToken(),
                result.getFactors()
                        .stream()
                        .map(this::toPriorityFactorDto)
                        .collect(Collectors.toList())
        );
    }

    private ComparisonPriorityFactorDto toPriorityFactorDto(ComparisonPriorityFactor factor) {
        return new ComparisonPriorityFactorDto(
                factor.getId(),
                factor.getLabel(),
                factor.getValue(),
                factor.getUnit(),
                factor.getVariant()
        );
    }

    private record PriorityClassification(
            String level,
            String label,
            String colorToken
    ) {
    }
}
