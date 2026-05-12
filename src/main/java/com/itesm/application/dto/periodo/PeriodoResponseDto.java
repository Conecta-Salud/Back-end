package com.itesm.application.dto.periodo;

import com.itesm.domain.models.periodo.PeriodoEstatus;

public class PeriodoResponseDto {

    private Integer id;
    private Integer anio;
    private PeriodoEstatus estatus;

    public PeriodoResponseDto(Integer id, Integer anio, PeriodoEstatus estatus) {
        this.id = id;
        this.anio = anio;
        this.estatus = estatus;
    }

    public Integer getId() {
        return id;
    }

    public Integer getAnio() {
        return anio;
    }

    public PeriodoEstatus getEstatus() {
        return estatus;
    }
}