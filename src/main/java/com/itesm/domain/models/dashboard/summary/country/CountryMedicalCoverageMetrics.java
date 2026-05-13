package com.itesm.domain.models.dashboard.summary.country;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CountryMedicalCoverageMetrics {

    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private Long totalDoctors;
    private BigDecimal doctorsPer1000;
    private Long criticalStates;
    private BigDecimal averageStateMedicalCoverage;

    public CountryMedicalCoverageMetrics(
            DashboardPeriod period,
            BigInteger totalPopulation,
            Long totalDoctors,
            BigDecimal doctorsPer1000,
            Long criticalStates,
            BigDecimal averageStateMedicalCoverage
    ) {
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.totalDoctors = totalDoctors;
        this.doctorsPer1000 = doctorsPer1000;
        this.criticalStates = criticalStates;
        this.averageStateMedicalCoverage = averageStateMedicalCoverage;
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

    public Long getCriticalStates() {
        return criticalStates;
    }

    public void setCriticalStates(Long criticalStates) {
        this.criticalStates = criticalStates;
    }

    public BigDecimal getAverageStateMedicalCoverage() {
        return averageStateMedicalCoverage;
    }

    public void setAverageStateMedicalCoverage(BigDecimal averageStateMedicalCoverage) {
        this.averageStateMedicalCoverage = averageStateMedicalCoverage;
    }
}
