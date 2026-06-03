package com.itesm.domain.models.Uploader.indicator;

import com.itesm.domain.models.Uploader.Auxiliar.AvailabilityStatus;
import com.itesm.domain.models.Uploader.Auxiliar.TerritoryLevel;

import java.math.BigDecimal;

public class TerritoryIndicatorValues {
    private Long id;
    private TerritoryLevel territoryLevel;
    private Integer stateId;
    private Integer municipalityId;
    private Integer indicatorId;
    private BigDecimal value;
    private Short analysisYear;
    private Short sourceYear;
    private Integer dataSourceId;
    private String sourceFile;
    private AvailabilityStatus availabilityStatus;
    private String methodologyNote;

    public TerritoryIndicatorValues() {}

    public TerritoryIndicatorValues(Long id, TerritoryLevel territoryLevel, Integer stateId, Integer municipalityId, Integer indicatorId, BigDecimal value, Short analysisYear, Short sourceYear, Integer dataSourceId, String sourceFile, AvailabilityStatus availabilityStatus, String methodologyNote) {
        this.id = id;
        this.territoryLevel = territoryLevel;
        this.stateId = stateId;
        this.municipalityId = municipalityId;
        this.indicatorId = indicatorId;
        this.value = value;
        this.analysisYear = analysisYear;
        this.sourceYear = sourceYear;
        this.dataSourceId = dataSourceId;
        this.sourceFile = sourceFile;
        this.availabilityStatus = availabilityStatus;
        this.methodologyNote = methodologyNote;
    }

    public Long getId() {
        return id;
    }

    public TerritoryLevel getTerritoryLevel() {
        return territoryLevel;
    }

    public Integer getStateId() {
        return stateId;
    }

    public Integer getMunicipalityId() {
        return municipalityId;
    }

    public Integer getIndicatorId() {
        return indicatorId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Short getAnalysisYear() {
        return analysisYear;
    }

    public Short getSourceYear() {
        return sourceYear;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getMethodologyNote() {
        return methodologyNote;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTerritoryLevel(TerritoryLevel territoryLevel) {
        this.territoryLevel = territoryLevel;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public void setMunicipalityId(Integer municipalityId) {
        this.municipalityId = municipalityId;
    }

    public void setIndicatorId(Integer indicatorId) {
        this.indicatorId = indicatorId;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setAnalysisYear(Short analysisYear) {
        this.analysisYear = analysisYear;
    }

    public void setSourceYear(Short sourceYear) {
        this.sourceYear = sourceYear;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setMethodologyNote(String methodologyNote) {
        this.methodologyNote = methodologyNote;
    }
}
