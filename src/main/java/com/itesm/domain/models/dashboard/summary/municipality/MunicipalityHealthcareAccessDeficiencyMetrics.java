package com.itesm.domain.models.dashboard.summary.municipality;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;
import com.itesm.domain.models.dashboard.summary.DashboardTerritory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MunicipalityHealthcareAccessDeficiencyMetrics {

    private DashboardTerritory territory;
    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private Long availableDoctors;
    private Long healthCenters;
    private BigDecimal coverageIndex;

    public MunicipalityHealthcareAccessDeficiencyMetrics(
            DashboardTerritory territory,
            DashboardPeriod period,
            BigInteger totalPopulation,
            Long availableDoctors,
            Long healthCenters,
            BigDecimal coverageIndex
    ) {
        this.territory = territory;
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.availableDoctors = availableDoctors;
        this.healthCenters = healthCenters;
        this.coverageIndex = coverageIndex;
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

    public Long getAvailableDoctors() {
        return availableDoctors;
    }

    public void setAvailableDoctors(Long availableDoctors) {
        this.availableDoctors = availableDoctors;
    }

    public Long getHealthCenters() {
        return healthCenters;
    }

    public void setHealthCenters(Long healthCenters) {
        this.healthCenters = healthCenters;
    }

    public BigDecimal getCoverageIndex() {
        return coverageIndex;
    }

    public void setCoverageIndex(BigDecimal coverageIndex) {
        this.coverageIndex = coverageIndex;
    }
}
