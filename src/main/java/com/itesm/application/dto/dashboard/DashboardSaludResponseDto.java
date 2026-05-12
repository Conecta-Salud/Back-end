package com.itesm.application.dto.dashboard;

public class DashboardSaludResponseDto {

    private TerritorioDto territorio;
    private PeriodoDto periodo;
    private SaludDashboardDto salud;

    public DashboardSaludResponseDto(
            TerritorioDto territorio,
            PeriodoDto periodo,
            SaludDashboardDto salud
    ) {
        this.territorio = territorio;
        this.periodo = periodo;
        this.salud = salud;
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

    public SaludDashboardDto getSalud() {
        return salud;
    }

    public void setSalud(SaludDashboardDto salud) {
        this.salud = salud;
    }
}