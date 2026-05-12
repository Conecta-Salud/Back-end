package com.itesm.domain.models.indicator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StateIndicator {
    private Integer id;
    private Integer stateId;
    private String stateName;
    private Integer periodId;
    private Integer periodYear;
    private BigInteger totalPopulation;
    private BigDecimal percentageOver60;
    private BigInteger healthcareAccessDeficiency;
    private BigInteger totalPovertyPopulation;

    public StateIndicator(
            Integer id,
            Integer stateId,
            String stateName,
            Integer periodId,
            Integer periodYear,
            BigInteger totalPopulation,
            BigDecimal percentageOver60,
            BigInteger healthcareAccessDeficiency,
            BigInteger totalPovertyPopulation
    ) {
        this.id = id;
        this.stateId = stateId;
        this.stateName = stateName;
        this.periodId = periodId;
        this.periodYear = periodYear;
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

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
    }

    public Integer getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Integer periodYear) {
        this.periodYear = periodYear;
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
