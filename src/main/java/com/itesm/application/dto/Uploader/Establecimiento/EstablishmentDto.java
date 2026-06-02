package com.itesm.application.dto.Uploader.Establecimiento;

public class EstablishmentDto {
    private Integer id;
    private String name;

    public EstablishmentDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public EstablishmentDto() {}

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
