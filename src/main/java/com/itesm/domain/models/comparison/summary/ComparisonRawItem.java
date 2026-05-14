package com.itesm.domain.models.comparison.summary;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ComparisonRawItem {

    private ComparisonTerritory territory;
    private ComparisonPeriod period;

    private BigInteger totalPopulation;
    private BigDecimal percentageOver60;
    private BigInteger totalPovertyPopulation;

    private Long totalHealthUnits;
    private Long totalHospitals;
    private Long totalDoctors;
    private Long totalHospitalBeds;

    public ComparisonRawItem(
            ComparisonTerritory territory,
            ComparisonPeriod period,
            BigInteger totalPopulation,
            BigDecimal percentageOver60,
            BigInteger totalPovertyPopulation,
            Long totalHealthUnits,
            Long totalHospitals,
            Long totalDoctors,
            Long totalHospitalBeds
    ) {
        this.territory = territory;
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.percentageOver60 = percentageOver60;
        this.totalPovertyPopulation = totalPovertyPopulation;
        this.totalHealthUnits = totalHealthUnits;
        this.totalHospitals = totalHospitals;
        this.totalDoctors = totalDoctors;
        this.totalHospitalBeds = totalHospitalBeds;
    }

    public ComparisonTerritory getTerritory() {
        return territory;
    }

    public void setTerritory(ComparisonTerritory territory) {
        this.territory = territory;
    }

    public ComparisonPeriod getPeriod() {
        return period;
    }

    public void setPeriod(ComparisonPeriod period) {
        this.period = period;
    }

    public BigInteger getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(BigInteger totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public BigDecimal getPercentageOver60() {
        return percentageOver60;
    }

    public void setPercentageOver60(BigDecimal percentageOver60) {
        this.percentageOver60 = percentageOver60;
    }

    public BigInteger getTotalPovertyPopulation() {
        return totalPovertyPopulation;
    }

    public void setTotalPovertyPopulation(BigInteger totalPovertyPopulation) {
        this.totalPovertyPopulation = totalPovertyPopulation;
    }

    public Long getTotalHealthUnits() {
        return totalHealthUnits;
    }

    public void setTotalHealthUnits(Long totalHealthUnits) {
        this.totalHealthUnits = totalHealthUnits;
    }

    public Long getTotalHospitals() {
        return totalHospitals;
    }

    public void setTotalHospitals(Long totalHospitals) {
        this.totalHospitals = totalHospitals;
    }

    public Long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Long getTotalHospitalBeds() {
        return totalHospitalBeds;
    }

    public void setTotalHospitalBeds(Long totalHospitalBeds) {
        this.totalHospitalBeds = totalHospitalBeds;
    }
}
