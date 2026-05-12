package com.itesm.application.dto.healthunit;

import com.itesm.domain.models.healthunit.CareLevel;

public class HealthUnitSummaryDto {

    private Integer id;
    private String clues;
    private String name;
    private Integer municipalityId;
    private String municipalityName;
    private Integer stateId;
    private String stateName;
    private String institution;
    private String establishmentType;
    private String medicalUnitType;
    private CareLevel careLevel;

    public HealthUnitSummaryDto(
            Integer id,
            String clues,
            String name,
            Integer municipalityId,
            String municipalityName,
            Integer stateId,
            String stateName,
            String institution,
            String establishmentType,
            String medicalUnitType,
            CareLevel careLevel
    ) {
        this.id = id;
        this.clues = clues;
        this.name = name;
        this.municipalityId = municipalityId;
        this.municipalityName = municipalityName;
        this.stateId = stateId;
        this.stateName = stateName;
        this.institution = institution;
        this.establishmentType = establishmentType;
        this.medicalUnitType = medicalUnitType;
        this.careLevel = careLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClues() {
        return clues;
    }

    public void setClues(String clues) {
        this.clues = clues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getEstablishmentType() {
        return establishmentType;
    }

    public void setEstablishmentType(String establishmentType) {
        this.establishmentType = establishmentType;
    }

    public String getMedicalUnitType() {
        return medicalUnitType;
    }

    public void setMedicalUnitType(String medicalUnitType) {
        this.medicalUnitType = medicalUnitType;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }
}
