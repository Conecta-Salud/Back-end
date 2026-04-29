package com.itesm.application.dto.dashboard;

public class PeriodoDto {

    private Integer id;
    private Integer anio;

    public PeriodoDto(Integer id, Integer anio) {
        this.id = id;
        this.anio = anio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
}