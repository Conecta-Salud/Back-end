package com.itesm.application.dto.Uploader.Establecimiento;

public class MunicipalityDto {
    private Integer id;
    private String name;
    private String inegiCode;

    public MunicipalityDto(Integer id, String name, String inegiCode) {
        this.id = id;
        this.name = name;
        this.inegiCode = inegiCode;
    }
    public MunicipalityDto() {}
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
