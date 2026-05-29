package com.itesm.application.dto.department;

public class DepartmentOptionResponseDto {

    private Integer id;
    private String name;

    public DepartmentOptionResponseDto() {
    }

    public DepartmentOptionResponseDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}