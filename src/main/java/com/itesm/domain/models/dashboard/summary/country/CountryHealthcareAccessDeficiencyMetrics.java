package com.itesm.domain.models.dashboard.summary.country;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CountryHealthcareAccessDeficiencyMetrics {

    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private BigInteger vulnerablePopulation;
    private Long priorityStates;
    private BigDecimal medicalCoverageIndex;

    public CountryHealthcareAccessDeficiencyMetrics(
            DashboardPeriod period,
            BigInteger totalPopulation,
            BigInteger vulnerablePopulation,
            Long priorityStates,
            BigDecimal medicalCoverageIndex
    ) {
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.vulnerablePopulation = vulnerablePopulation;
        this.priorityStates = priorityStates;
        this.medicalCoverageIndex = medicalCoverageIndex;
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

    public BigInteger getVulnerablePopulation() {
        return vulnerablePopulation;
    }

    public void setVulnerablePopulation(BigInteger vulnerablePopulation) {
        this.vulnerablePopulation = vulnerablePopulation;
    }

    public Long getPriorityStates() {
        return priorityStates;
    }

    public void setPriorityStates(Long priorityStates) {
        this.priorityStates = priorityStates;
    }

    public BigDecimal getMedicalCoverageIndex() {
        return medicalCoverageIndex;
    }

    public void setMedicalCoverageIndex(BigDecimal medicalCoverageIndex) {
        this.medicalCoverageIndex = medicalCoverageIndex;
    }
}
