package com.itesm.infrastructure.persistence.entity.Upload.Indicadores;

import com.itesm.infrastructure.persistence.entity.PeriodEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "state_indicators")
@NamedEntityGraph(
        name = "StateIndicator.withStateAndPeriod",
        attributeNodes = {
                @NamedAttributeNode("state"),
                @NamedAttributeNode("period")
        }
)
public class StateIndicatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private StateEntity state;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_id", nullable = false)
    private PeriodEntity period;

    @Column(name = "total_population", nullable = false)
    private BigInteger totalPopulation;

    @Column(name = "percentage_over_60", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentageOver60;

    @Column(name = "healthcare_access_deficiency", nullable = false)
    private BigInteger healthcareAccessDeficiency;

    @Column(name = "total_poverty_population", nullable = false)
    private BigInteger totalPovertyPopulation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public PeriodEntity getPeriod() {
        return period;
    }

    public void setPeriod(PeriodEntity period) {
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
