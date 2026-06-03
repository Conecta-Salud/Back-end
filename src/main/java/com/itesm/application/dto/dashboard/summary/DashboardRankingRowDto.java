package com.itesm.application.dto.dashboard.summary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class DashboardRankingRowDto {

    private String id;
    private Integer rank;
    private String code;
    private String name;
    private BigInteger population;
    private Long doctors;
    private Long hospitalBeds;
    private Long consultingRooms;
    private BigDecimal coverageIndex;
    private String unitType;
    private String careLevel;
    private BigDecimal value;
    private String level;
    private String colorToken;
    private Map<String, Object> extra;

    public DashboardRankingRowDto(
            String id,
            Integer rank,
            String code,
            String name,
            BigInteger population,
            Long doctors,
            Long hospitalBeds,
            Long consultingRooms,
            BigDecimal coverageIndex,
            String unitType,
            String careLevel,
            BigDecimal value,
            String level,
            String colorToken,
            Map<String, Object> extra
    ) {
        this.id = id;
        this.rank = rank;
        this.code = code;
        this.name = name;
        this.population = population;
        this.doctors = doctors;
        this.hospitalBeds = hospitalBeds;
        this.consultingRooms = consultingRooms;
        this.coverageIndex = coverageIndex;
        this.unitType = unitType;
        this.careLevel = careLevel;
        this.value = value;
        this.level = level;
        this.colorToken = colorToken;
        this.extra = extra;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getPopulation() {
        return population;
    }

    public void setPopulation(BigInteger population) {
        this.population = population;
    }

    public Long getDoctors() {
        return doctors;
    }

    public void setDoctors(Long doctors) {
        this.doctors = doctors;
    }

    public Long getHospitalBeds() {
        return hospitalBeds;
    }

    public void setHospitalBeds(Long hospitalBeds) {
        this.hospitalBeds = hospitalBeds;
    }

    public Long getConsultingRooms() {
        return consultingRooms;
    }

    public void setConsultingRooms(Long consultingRooms) {
        this.consultingRooms = consultingRooms;
    }

    public BigDecimal getCoverageIndex() {
        return coverageIndex;
    }

    public void setCoverageIndex(BigDecimal coverageIndex) {
        this.coverageIndex = coverageIndex;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColorToken() {
        return colorToken;
    }

    public void setColorToken(String colorToken) {
        this.colorToken = colorToken;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
