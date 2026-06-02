package com.itesm.application.dto.Uploader.Establecimiento;

public class MedicalUnitTypesDto {
    private Integer id;
    private String name;

    public MedicalUnitTypesDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public MedicalUnitTypesDto() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
