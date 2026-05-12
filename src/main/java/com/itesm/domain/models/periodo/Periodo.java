package com.itesm.domain.models.periodo;

public class Periodo {

    private Integer  id;
    private Integer  anio;
    private PeriodoEstatus estatus;

    public Periodo(Integer id, Integer anio, PeriodoEstatus estatus) {
        this.id = id;
        this.anio = anio;
        this.estatus = estatus;
    }

    public Integer  getId() {
        return id;
    }

    public Integer  getAnio() {
        return anio;
    }

    public PeriodoEstatus getEstatus() {
        return estatus;
    }
}
