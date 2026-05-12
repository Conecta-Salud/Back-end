package com.itesm.application.dto.healthunit;

public class HealthUnitDetailDto {

    private Integer id;
    private String clues;
    private String name;
    private HealthUnitTerritoryDto territory;
    private HealthUnitClassificationDto classification;
    private HealthUnitStaffDto staff;
    private HealthUnitInfrastructureDto infrastructure;

    public HealthUnitDetailDto(
            Integer id,
            String clues,
            String name,
            HealthUnitTerritoryDto territory,
            HealthUnitClassificationDto classification,
            HealthUnitStaffDto staff,
            HealthUnitInfrastructureDto infrastructure
    ) {
        this.id = id;
        this.clues = clues;
        this.name = name;
        this.territory = territory;
        this.classification = classification;
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

    public HealthUnitTerritoryDto getTerritory() {
        return territory;
    }

    public void setTerritory(HealthUnitTerritoryDto territory) {
        this.territory = territory;
    }

    public HealthUnitClassificationDto getClassification() {
        return classification;
    }

    public void setClassification(HealthUnitClassificationDto classification) {
        this.classification = classification;
    }

    public HealthUnitStaffDto getStaff() {
        return staff;
    }

    public void setStaff(HealthUnitStaffDto staff) {
        this.staff = staff;
    }

    public HealthUnitInfrastructureDto getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(HealthUnitInfrastructureDto infrastructure) {
        this.infrastructure = infrastructure;
    }
}
