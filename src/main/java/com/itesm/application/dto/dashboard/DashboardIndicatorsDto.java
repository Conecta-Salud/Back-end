package com.itesm.application.dto.dashboard;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DashboardIndicatorsDto {

    private BigInteger totalPopulation;
    private BigDecimal percentageOver60;
    private BigInteger healthcareAccessDeficiency;
    private BigInteger totalPovertyPopulation;

    public DashboardIndicatorsDto(
            BigInteger totalPopulation,
            BigDecimal percentageOver60,
            BigInteger healthcareAccessDeficiency,
            BigInteger totalPovertyPopulation
    ) {
        this.totalPopulation = totalPopulation;
        this.percentageOver60 = percentageOver60;
        this.healthcareAccessDeficiency = healthcareAccessDeficiency;
        this.totalPovertyPopulation = totalPovertyPopulation;
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

    public BigInteger getHealthcareAccessDeficiency() {
        return healthcareAccessDeficiency;
    }

    public void setHealthcareAccessDeficiency(BigInteger healthcareAccessDeficiency) {
        this.healthcareAccessDeficiency = healthcareAccessDeficiency;
    }

    public BigInteger getTotalPovertyPopulation() {
        return totalPovertyPopulation;
    }

    public void setTotalPovertyPopulation(BigInteger totalPovertyPopulation) {
        this.totalPovertyPopulation = totalPovertyPopulation;
    }
}