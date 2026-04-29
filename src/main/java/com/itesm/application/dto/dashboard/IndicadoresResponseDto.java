package com.itesm.application.dto.dashboard;

public class IndicadoresResponseDto {

    private TerritorioDto territorio;
    private PeriodoDto periodo;
    private IndicadoresDto indicadores;

    public IndicadoresResponseDto(
            TerritorioDto territorio,
            PeriodoDto periodo,
            IndicadoresDto indicadores
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

    public IndicadoresDto getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(IndicadoresDto indicadores) {
        this.indicadores = indicadores;
    }
}