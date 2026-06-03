package com.itesm.domain.models.map;

import java.math.BigDecimal;

public class MapIndicator {

    private String code;
    private String name;
    private BigDecimal value;
    private MapLevel level;
    private ColorToken colorToken;
    private Integer sourceYear;
    private String unit;
    private String availabilityStatus;
    private String methodologyNote;
    private String dataSourceName;

    public MapIndicator(String code, String name, BigDecimal value, MapIndicatorType indicatorType) {
        this(code, name, value, indicatorType, null, null, null, null, null);
    }

    public MapIndicator(
            String code,
            String name,
            BigDecimal value,
            MapIndicatorType indicatorType,
            Integer sourceYear,
            String unit,
            String availabilityStatus,
            String methodologyNote,
            String dataSourceName
    ) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.sourceYear = sourceYear;
        this.unit = unit;
        this.availabilityStatus = availabilityStatus;
        this.methodologyNote = methodologyNote;
        this.dataSourceName = dataSourceName;

        classify(indicatorType);
    }

    private void classify(MapIndicatorType indicatorType) {
        if (value == null) {
            this.level = MapLevel.NO_DATA;
            this.colorToken = ColorToken.NEUTRAL;
            return;
        }

        double numericValue = value.doubleValue();

        switch (indicatorType) {
            case MEDICAL_COVERAGE:
                classifyHigherIsBetter(numericValue, 2.7, 1.0);
                break;

            case HOSPITAL_BEDS:
                classifyHigherIsBetter(numericValue, 3.0, 1.0);
                break;

            case HEALTHCARE_ACCESS_DEFICIENCY:
                classifyLowerIsBetter(numericValue);
                break;
        }
    }

    private void classifyHigherIsBetter(double numericValue, double goodMinimum, double riskMinimum) {
        if (numericValue >= goodMinimum) {
            this.level = MapLevel.GOOD;
            this.colorToken = ColorToken.GREEN;
        } else if (numericValue >= riskMinimum) {
            this.level = MapLevel.RISK;
            this.colorToken = ColorToken.YELLOW;
        } else {
            this.level = MapLevel.CRITICAL;
            this.colorToken = ColorToken.RED;
        }
    }

    private void classifyLowerIsBetter(double numericValue) {
        if (numericValue <= 20) {
            this.level = MapLevel.GOOD;
            this.colorToken = ColorToken.GREEN;
        } else if (numericValue < 40) {
            this.level = MapLevel.RISK;
            this.colorToken = ColorToken.YELLOW;
        } else {
            this.level = MapLevel.CRITICAL;
            this.colorToken = ColorToken.RED;
        }
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

    public MapLevel getLevel() {
        return level;
    }

    public void setLevel(MapLevel level) {
        this.level = level;
    }

    public ColorToken getColorToken() {
        return colorToken;
    }

    public void setColorToken(ColorToken colorToken) {
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
