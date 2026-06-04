package com.itesm.application.dto.map;

import com.itesm.domain.models.map.ColorToken;
import com.itesm.domain.models.map.MapLevel;

import java.math.BigDecimal;

public class MapIndicatorResponseDto {

    private String code;
    private String name;
    private BigDecimal value;
    private String level;
    private String colorToken;
    private Integer sourceYear;
    private String unit;
    private String availabilityStatus;
    private String methodologyNote;
    private String dataSourceName;

    public MapIndicatorResponseDto(
            String code,
            String name,
            BigDecimal value,
            String level,
            String colorToken
    ) {
        this(code, name, value, level, colorToken, null, null, null, null, null);
    }

    public MapIndicatorResponseDto(
            String code,
            String name,
            BigDecimal value,
            String level,
            String colorToken,
            Integer sourceYear,
            String unit,
            String availabilityStatus,
            String methodologyNote,
            String dataSourceName
    ) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.level = level;
        this.colorToken = colorToken;
        this.sourceYear = sourceYear;
        this.unit = unit;
        this.availabilityStatus = availabilityStatus;
        this.methodologyNote = methodologyNote;
        this.dataSourceName = dataSourceName;
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

    public Integer getSourceYear() {
        return sourceYear;
    }

    public void setSourceYear(Integer sourceYear) {
        this.sourceYear = sourceYear;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getMethodologyNote() {
        return methodologyNote;
    }

    public void setMethodologyNote(String methodologyNote) {
        this.methodologyNote = methodologyNote;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
}
