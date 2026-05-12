package com.itesm.domain.models.comparison;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TerritoryComparison {

    private Integer id;
    private String name;
    private String type;

    private Integer periodId;
    private Integer periodYear;

    private BigInteger totalPopulation;
    private BigDecimal percentageOver60;
    private BigInteger healthcareAccessDeficiency;
    private BigInteger totalPovertyPopulation;

    private Long totalHealthUnits;
    private Long totalDoctors;
    private Long totalNurses;
    private Long totalConsultingRooms;
    private Long totalHospitalBeds;

    public TerritoryComparison(
            Integer id,
            String name,
            String type,
            Integer periodId,
            Integer periodYear,
            BigInteger totalPopulation,
            BigDecimal percentageOver60,
            BigInteger healthcareAccessDeficiency,
            BigInteger totalPovertyPopulation,
            Long totalHealthUnits,
            Long totalDoctors,
            Long totalNurses,
            Long totalConsultingRooms,
            Long totalHospitalBeds
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.periodId = periodId;
        this.periodYear = periodYear;
        this.totalPopulation = totalPopulation;
        this.percentageOver60 = percentageOver60;
        this.healthcareAccessDeficiency = healthcareAccessDeficiency;
        this.totalPovertyPopulation = totalPovertyPopulation;
        this.totalHealthUnits = totalHealthUnits;
        this.totalDoctors = totalDoctors;
        this.totalNurses = totalNurses;
        this.totalConsultingRooms = totalConsultingRooms;
        this.totalHospitalBeds = totalHospitalBeds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getTotalHealthUnits() {
        return totalHealthUnits;
    }

    public void setTotalHealthUnits(Long totalHealthUnits) {
        this.totalHealthUnits = totalHealthUnits;
    }

    public Long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Long getTotalNurses() {
        return totalNurses;
    }

    public void setTotalNurses(Long totalNurses) {
        this.totalNurses = totalNurses;
    }

    public Long getTotalConsultingRooms() {
        return totalConsultingRooms;
    }

    public void setTotalConsultingRooms(Long totalConsultingRooms) {
        this.totalConsultingRooms = totalConsultingRooms;
    }

    public Long getTotalHospitalBeds() {
        return totalHospitalBeds;
    }

    public void setTotalHospitalBeds(Long totalHospitalBeds) {
        this.totalHospitalBeds = totalHospitalBeds;
    }
}

