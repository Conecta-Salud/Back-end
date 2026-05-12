package com.itesm.application.dto.comparacion;

import com.itesm.application.dto.dashboard.DashboardIndicadoresDto;
import com.itesm.application.dto.dashboard.PeriodoDto;
import com.itesm.application.dto.dashboard.SaludDashboardDto;
// Compa
public class ComparacionTerritorioDto {

    private Integer id;
    private String nombre;
    private String tipo;
    private PeriodoDto periodo;
    private DashboardIndicadoresDto indicadores;
    private SaludDashboardDto salud;

    public ComparacionTerritorioDto(
            Integer id,
            String nombre,
            String tipo,
            PeriodoDto periodo,
            DashboardIndicadoresDto indicadores,
            SaludDashboardDto salud
    ) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.periodo = periodo;
        this.indicadores = indicadores;
        this.salud = salud;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public PeriodoDto getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoDto periodo) {
        this.periodo = periodo;
    }

    public DashboardIndicadoresDto getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(DashboardIndicadoresDto indicadores) {
        this.indicadores = indicadores;
    }

    public SaludDashboardDto getSalud() {
        return salud;
    }

    public void setSalud(SaludDashboardDto salud) {
        this.salud = salud;
    }
}
