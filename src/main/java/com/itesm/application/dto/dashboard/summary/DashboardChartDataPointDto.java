package com.itesm.application.dto.dashboard.summary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class DashboardChartDataPointDto {

    private String label;
    private String code;
    private BigDecimal value;
    private BigInteger population;
    private Long doctors;
    private Long hospitalBeds;
    private Long consultingRooms;
    private BigDecimal coverageIndex;
    private String level;
    private String colorToken;
    private Map<String, Object> extra;

    public DashboardChartDataPointDto(
            String label,
            String code,
            BigDecimal value,
            BigInteger population,
            Long doctors,
            Long hospitalBeds,
            Long consultingRooms,
            BigDecimal coverageIndex,
            String level,
            String colorToken,
            Map<String, Object> extra
    ) {
        this.label = label;
        this.code = code;
        this.value = value;
        this.population = population;
        this.doctors = doctors;
        this.hospitalBeds = hospitalBeds;
        this.consultingRooms = consultingRooms;
        this.coverageIndex = coverageIndex;
        this.level = level;
        this.colorToken = colorToken;
        this.extra = extra;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
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
