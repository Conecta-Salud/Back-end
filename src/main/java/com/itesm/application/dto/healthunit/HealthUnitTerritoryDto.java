package com.itesm.application.dto.healthunit;

public class HealthUnitTerritoryDto {

    private Integer municipalityId;
    private String municipalityName;
    private Integer stateId;
    private String stateName;

    public HealthUnitTerritoryDto(
            Integer municipalityId,
            String municipalityName,
            Integer stateId,
            String stateName
    ) {
        this.municipalityId = municipalityId;
        this.municipalityName = municipalityName;
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public Integer getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Integer municipalityId) {
        this.municipalityId = municipalityId;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
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
}
