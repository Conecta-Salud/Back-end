package com.itesm.domain.models.dashboard.summary.country;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CountryHospitalBedsMetrics {

    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private Long totalHospitalBeds;
    private BigDecimal hospitalBedsPer1000;
    private Long statesWithHospitalDeficit;
    private Long totalHospitals;
    private BigDecimal averageBedsPerHospital;

    public CountryHospitalBedsMetrics(
            DashboardPeriod period,
            BigInteger totalPopulation,
            Long totalHospitalBeds,
            BigDecimal hospitalBedsPer1000,
            Long statesWithHospitalDeficit,
            Long totalHospitals,
            BigDecimal averageBedsPerHospital
    ) {
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.totalHospitalBeds = totalHospitalBeds;
        this.hospitalBedsPer1000 = hospitalBedsPer1000;
        this.statesWithHospitalDeficit = statesWithHospitalDeficit;
        this.totalHospitals = totalHospitals;
        this.averageBedsPerHospital = averageBedsPerHospital;
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

    public Long getTotalHospitalBeds() {
        return totalHospitalBeds;
    }

    public void setTotalHospitalBeds(Long totalHospitalBeds) {
        this.totalHospitalBeds = totalHospitalBeds;
    }

    public BigDecimal getHospitalBedsPer1000() {
        return hospitalBedsPer1000;
    }

    public void setHospitalBedsPer1000(BigDecimal hospitalBedsPer1000) {
        this.hospitalBedsPer1000 = hospitalBedsPer1000;
    }

    public Long getStatesWithHospitalDeficit() {
        return statesWithHospitalDeficit;
    }

    public void setStatesWithHospitalDeficit(Long statesWithHospitalDeficit) {
        this.statesWithHospitalDeficit = statesWithHospitalDeficit;
    }

    public Long getTotalHospitals() {
        return totalHospitals;
    }

    public void setTotalHospitals(Long totalHospitals) {
        this.totalHospitals = totalHospitals;
    }

    public BigDecimal getAverageBedsPerHospital() {
        return averageBedsPerHospital;
    }

    public void setAverageBedsPerHospital(BigDecimal averageBedsPerHospital) {
        this.averageBedsPerHospital = averageBedsPerHospital;
    }
}
