package com.itesm.domain.models.indicator;

import java.math.BigDecimal;

public class TerritoryIndicatorValueDto {

    private final Long id;
    private final String territoryLevel;
    private final Integer stateId;
    private final String stateCode;
    private final String stateName;
    private final Integer municipalityId;
    private final String municipalityCode;
    private final String municipalityName;
    private final String indicatorCode;
    private final String indicatorName;
    private final String categoryCode;
    private final String categoryName;
    private final BigDecimal value;
    private final Integer analysisYear;
    private final Integer sourceYear;
    private final String unit;
    private final String valueType;
    private final String dataSourceCode;
    private final String dataSourceName;
    private final String dataSourceInstitution;
    private final String dataSourceOfficialUrl;
    private final String availabilityStatus;
    private final String methodologyNote;
    private final String sourceFile;

    public TerritoryIndicatorValueDto(
            Long id,
            String territoryLevel,
            Integer stateId,
            String stateCode,
            String stateName,
            Integer municipalityId,
            String municipalityCode,
            String municipalityName,
            String indicatorCode,
            String indicatorName,
            String categoryCode,
            String categoryName,
            BigDecimal value,
            Integer analysisYear,
            Integer sourceYear,
            String unit,
            String valueType,
            String dataSourceCode,
            String dataSourceName,
            String dataSourceInstitution,
            String dataSourceOfficialUrl,
            String availabilityStatus,
            String methodologyNote,
            String sourceFile
    ) {
        this.id = id;
        this.territoryLevel = territoryLevel;
        this.stateId = stateId;
        this.stateCode = stateCode;
        this.stateName = stateName;
        this.municipalityId = municipalityId;
        this.municipalityCode = municipalityCode;
        this.municipalityName = municipalityName;
        this.indicatorCode = indicatorCode;
        this.indicatorName = indicatorName;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.value = value;
        this.analysisYear = analysisYear;
        this.sourceYear = sourceYear;
        this.unit = unit;
        this.valueType = valueType;
        this.dataSourceCode = dataSourceCode;
        this.dataSourceName = dataSourceName;
        this.dataSourceInstitution = dataSourceInstitution;
        this.dataSourceOfficialUrl = dataSourceOfficialUrl;
        this.availabilityStatus = availabilityStatus;
        this.methodologyNote = methodologyNote;
        this.sourceFile = sourceFile;
    }

    public Long getId() {
        return id;
    }

    public String getTerritoryLevel() {
        return territoryLevel;
    }

    public Integer getStateId() {
        return stateId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public Integer getMunicipalityId() {
        return municipalityId;
    }

    public String getMunicipalityCode() {
        return municipalityCode;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Integer getAnalysisYear() {
        return analysisYear;
    }

    public Integer getSourceYear() {
        return sourceYear;
    }

    public String getUnit() {
        return unit;
    }

    public String getValueType() {
        return valueType;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public String getDataSourceInstitution() {
        return dataSourceInstitution;
    }

    public String getDataSourceOfficialUrl() {
        return dataSourceOfficialUrl;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getMethodologyNote() {
        return methodologyNote;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public Integer getTerritoryId() {
        return "municipality".equals(territoryLevel) ? municipalityId : stateId;
    }

    public String getTerritoryCode() {
        return "municipality".equals(territoryLevel) ? municipalityCode : stateCode;
    }

    public String getTerritoryName() {
        return "municipality".equals(territoryLevel) ? municipalityName : stateName;
    }
}
