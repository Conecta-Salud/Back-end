package com.itesm.domain.models.Uploader.indicator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MunicipalityIndicator {
    private Integer id;
    private Integer municipalityId;
    private Integer periodId;

    private BigInteger totalPopulation;
    private BigDecimal percentageOver60;
    private BigInteger healthcareAccessDeficiency;
    private BigInteger totalPovertyPopulation;

    public MunicipalityIndicator(
            Integer id,
            Integer municipalityId,
            Integer periodId,
            BigInteger totalPopulation,
            BigDecimal percentageOver60,
            BigInteger healthcareAccessDeficiency,
            BigInteger totalPovertyPopulation
    ) {
        this.id = id;
        this.municipalityId = municipalityId;
        this.periodId = periodId;

        this.totalPopulation = totalPopulation;
        this.percentageOver60 = percentageOver60;
        this.healthcareAccessDeficiency = healthcareAccessDeficiency;
        this.totalPovertyPopulation = totalPovertyPopulation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Integer municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
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
