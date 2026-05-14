package com.itesm.domain.repository;

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

import java.util.List;
import java.util.Optional;

public interface DashboardSummaryRepository {

    // =========================== Cobertura Medica ===========================
    // País
    Optional<CountryMedicalCoverageMetrics> findCountryMedicalCoverageMetrics(Integer periodId);
    List<DashboardRankingRow> findCountryMedicalCoverageRanking(Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findCountryMedicalCoverageMainChart(Integer periodId);
    List<DashboardChartDataPoint> findCountryMedicalCoverageSecondaryChart(Integer periodId);
    List<DashboardChartDataPoint> findCountrySpecialtiesDistribution(Integer periodId);
    boolean existsPeriodById(Integer periodId);
    // Estado
    Optional<StateMedicalCoverageMetrics> findStateMedicalCoverageMetrics(Integer stateId, Integer periodId);
    List<DashboardRankingRow> findStateMedicalCoverageRanking(Integer stateId, Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findStateMedicalCoverageMainChart(Integer stateId, Integer periodId);
    List<DashboardChartDataPoint> findStateMedicalCoverageSecondaryChart(Integer stateId, Integer periodId);
    List<DashboardChartDataPoint> findStateSpecialtiesDistribution(Integer stateId, Integer periodId);
    boolean existsStateById(Integer stateId);
    // Municipios
    Optional<MunicipalityMedicalCoverageMetrics> findMunicipalityMedicalCoverageMetrics(Integer municipalityId, Integer periodId);
    List<DashboardRankingRow> findMunicipalityMedicalCoverageRanking(Integer municipalityId, Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findMunicipalityMedicalCoverageMainChart(Integer municipalityId, Integer periodId);
    List<DashboardChartDataPoint> findMunicipalityMedicalCoverageSecondaryChart(Integer municipalityId, Integer periodId);
    List<DashboardChartDataPoint> findMunicipalitySpecialtiesDistribution(Integer municipalityId, Integer periodId);
    boolean existsMunicipalityById(Integer municipalityId);

    // =========================== Infraestructura Hospitalaria ===========================
    // País
    Optional<CountryHospitalBedsMetrics> findCountryHospitalBedsMetrics(Integer periodId);
    List<DashboardRankingRow> findCountryHospitalBedsRanking(Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findCountryHospitalBedsMainChart(Integer periodId);
    List<DashboardChartDataPoint> findCountryHospitalBedsSecondaryChart(Integer periodId);
    List<DashboardChartDataPoint> findCountryInfrastructureDistribution(Integer periodId);
    // Estado
    Optional<StateHospitalBedsMetrics> findStateHospitalBedsMetrics(Integer stateId, Integer periodId);
    List<DashboardRankingRow> findStateHospitalBedsRanking(Integer stateId, Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findStateHospitalBedsMainChart(Integer stateId, Integer periodId);
    List<DashboardChartDataPoint> findStateHospitalBedsSecondaryChart(Integer stateId, Integer periodId);
    List<DashboardChartDataPoint> findStateInfrastructureDistribution(Integer stateId, Integer periodId);
    // Municipios
    Optional<MunicipalityHospitalBedsMetrics> findMunicipalityHospitalBedsMetrics(Integer municipalityId, Integer periodId);
    List<DashboardRankingRow> findMunicipalityHospitalBedsRanking(Integer municipalityId, Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findMunicipalityHospitalBedsMainChart(Integer municipalityId, Integer periodId);
    List<DashboardChartDataPoint> findMunicipalityHospitalBedsSecondaryChart(Integer municipalityId, Integer periodId);
    List<DashboardChartDataPoint> findMunicipalityInfrastructureDistribution(Integer municipalityId, Integer periodId);

    // =========================== Poblacion Vulnerable ===========================
    // País
    Optional<CountryHealthcareAccessDeficiencyMetrics> findCountryHealthcareAccessDeficiencyMetrics(Integer periodId);
    List<DashboardRankingRow> findCountryHealthcareAccessDeficiencyRanking(Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findCountryHealthcareAccessDeficiencyMainChart(Integer periodId);
    List<DashboardChartDataPoint> findCountryHealthcareAccessDeficiencySecondaryChart(Integer periodId);
    List<DashboardChartDataPoint> findCountryHealthcareAccessDistribution(Integer periodId);
    // Estado
    Optional<StateHealthcareAccessDeficiencyMetrics> findStateHealthcareAccessDeficiencyMetrics(Integer stateId, Integer periodId);
    List<DashboardRankingRow> findStateHealthcareAccessDeficiencyRanking(Integer stateId, Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findStateHealthcareAccessDeficiencyMainChart(Integer stateId, Integer periodId);
    List<DashboardChartDataPoint> findStateHealthcareAccessDeficiencySecondaryChart(Integer stateId, Integer periodId);
    List<DashboardChartDataPoint> findStateHealthcareAccessDistribution(Integer stateId, Integer periodId);
    // Municipios
    Optional<MunicipalityHealthcareAccessDeficiencyMetrics> findMunicipalityHealthcareAccessDeficiencyMetrics(Integer municipalityId, Integer periodId);
    List<DashboardRankingRow> findMunicipalityHealthcareAccessDeficiencyRanking(Integer municipalityId, Integer periodId, Integer limit);
    List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDeficiencyMainChart(Integer municipalityId, Integer periodId);
    List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDeficiencySecondaryChart(Integer municipalityId, Integer periodId);
    List<DashboardChartDataPoint> findMunicipalityHealthcareAccessDistribution(Integer municipalityId, Integer periodId);
}
