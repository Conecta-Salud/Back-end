package com.itesm.domain.models.dashboard.summary.state;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;
import com.itesm.domain.models.dashboard.summary.DashboardTerritory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StateMedicalCoverageMetrics {

    private DashboardTerritory territory;
    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private Long totalDoctors;
    private BigDecimal doctorsPer1000;
    private Long criticalMunicipalities;
    private BigDecimal averageMunicipalCoverage;

    public StateMedicalCoverageMetrics(
            DashboardTerritory territory,
            DashboardPeriod period,
            BigInteger totalPopulation,
            Long totalDoctors,
            BigDecimal doctorsPer1000,
            Long criticalMunicipalities,
            BigDecimal averageMunicipalCoverage
    ) {
        this.territory = territory;
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.totalDoctors = totalDoctors;
        this.doctorsPer1000 = doctorsPer1000;
        this.criticalMunicipalities = criticalMunicipalities;
        this.averageMunicipalCoverage = averageMunicipalCoverage;
    }

    public DashboardTerritory getTerritory() {
        return territory;
    }

    public void setTerritory(DashboardTerritory territory) {
        this.territory = territory;
    }

    public DashboardPeriod getPeriod() {
        return period;
    }

    public void setPeriod(DashboardPeriod period) {
        this.period = period;
    }

    public BigInteger getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(BigInteger totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public Long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public BigDecimal getDoctorsPer1000() {
        return doctorsPer1000;
    }

    public void setDoctorsPer1000(BigDecimal doctorsPer1000) {
        this.doctorsPer1000 = doctorsPer1000;
    }

    public Long getCriticalMunicipalities() {
        return criticalMunicipalities;
    }

    public void setCriticalMunicipalities(Long criticalMunicipalities) {
        this.criticalMunicipalities = criticalMunicipalities;
    }

    public BigDecimal getAverageMunicipalCoverage() {
        return averageMunicipalCoverage;
    }

    public void setAverageMunicipalCoverage(BigDecimal averageMunicipalCoverage) {
        this.averageMunicipalCoverage = averageMunicipalCoverage;
    }
}
