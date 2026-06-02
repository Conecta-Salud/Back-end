package com.itesm.application.dto.Uploader.Establecimiento;

public class StateDto {
    private Integer id;
    private String name;
    private String inegiCode;

    public StateDto(Integer id, String name, String inegiCode) {
        this.id = id;
        this.name = name;
        this.inegiCode = inegiCode;
    }
    public StateDto() {}
    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getInegiCode() {
        return inegiCode;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInegiCode(String inegiCode) {
        this.inegiCode = inegiCode;
    }
}
