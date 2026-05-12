package com.itesm.domain.models.healthunit;

public class HealthUnitDetail {

    private Integer id;
    private String clues;
    private String name;

    private Integer municipalityId;
    private String municipalityName;
    private Integer stateId;
    private String stateName;

    private String institutionName;
    private String establishmentTypeName;
    private String medicalUnitTypeName;
    private CareLevel careLevel;

    private HealthUnitStaffSummary staff;
    private HealthUnitInfrastructureSummary infrastructure;

    public HealthUnitDetail(
            Integer id,
            String clues,
            String name,
            Integer municipalityId,
            String municipalityName,
            Integer stateId,
            String stateName,
            String institutionName,
            String establishmentTypeName,
            String medicalUnitTypeName,
            CareLevel careLevel,
            HealthUnitStaffSummary staff,
            HealthUnitInfrastructureSummary infrastructure
    ) {
        this.id = id;
        this.clues = clues;
        this.name = name;
        this.municipalityId = municipalityId;
        this.municipalityName = municipalityName;
        this.stateId = stateId;
        this.stateName = stateName;
        this.institutionName = institutionName;
        this.establishmentTypeName = establishmentTypeName;
        this.medicalUnitTypeName = medicalUnitTypeName;
        this.careLevel = careLevel;
        this.staff = staff;
        this.infrastructure = infrastructure;
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

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getEstablishmentTypeName() {
        return establishmentTypeName;
    }

    public void setEstablishmentTypeName(String establishmentTypeName) {
        this.establishmentTypeName = establishmentTypeName;
    }

    public String getMedicalUnitTypeName() {
        return medicalUnitTypeName;
    }

    public void setMedicalUnitTypeName(String medicalUnitTypeName) {
        this.medicalUnitTypeName = medicalUnitTypeName;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }

    public HealthUnitStaffSummary getStaff() {
        return staff;
    }

    public void setStaff(HealthUnitStaffSummary staff) {
        this.staff = staff;
    }

    public HealthUnitInfrastructureSummary getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(HealthUnitInfrastructureSummary infrastructure) {
        this.infrastructure = infrastructure;
    }
}
