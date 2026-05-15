package com.itesm.domain.models.dashboard.summary.state;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;
import com.itesm.domain.models.dashboard.summary.DashboardTerritory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StateHealthcareAccessDeficiencyMetrics {

    private DashboardTerritory territory;
    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private Long priorityMunicipalities;
    private BigDecimal medicalCoverageIndex;
    private Long availableInfrastructure;

    public StateHealthcareAccessDeficiencyMetrics(
            DashboardTerritory territory,
            DashboardPeriod period,
            BigInteger totalPopulation,
            Long priorityMunicipalities,
            BigDecimal medicalCoverageIndex,
            Long availableInfrastructure
    ) {
        this.territory = territory;
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.priorityMunicipalities = priorityMunicipalities;
        this.medicalCoverageIndex = medicalCoverageIndex;
        this.availableInfrastructure = availableInfrastructure;
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

    public Long getPriorityMunicipalities() {
        return priorityMunicipalities;
    }

    public void setPriorityMunicipalities(Long priorityMunicipalities) {
        this.priorityMunicipalities = priorityMunicipalities;
    }

    public BigDecimal getMedicalCoverageIndex() {
        return medicalCoverageIndex;
    }

    public void setMedicalCoverageIndex(BigDecimal medicalCoverageIndex) {
        this.medicalCoverageIndex = medicalCoverageIndex;
    }

    public Long getAvailableInfrastructure() {
        return availableInfrastructure;
    }

    public void setAvailableInfrastructure(Long availableInfrastructure) {
        this.availableInfrastructure = availableInfrastructure;
    }
}
