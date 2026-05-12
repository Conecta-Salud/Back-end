package com.itesm.application.dto.dashboard;

public class IndicadoresResponseDto {

    private TerritorioDto territorio;
    private PeriodoDto periodo;
    private DashboardIndicadoresDto indicadores;

    public IndicadoresResponseDto(
            TerritorioDto territorio,
            PeriodoDto periodo,
            DashboardIndicadoresDto indicadores
    ) {
        this.territorio = territorio;
        this.periodo = periodo;
        this.indicadores = indicadores;
    }

    public TerritorioDto getTerritorio() {
        return territorio;
    }

    public void setTerritorio(TerritorioDto territorio) {
        this.territorio = territorio;
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
}