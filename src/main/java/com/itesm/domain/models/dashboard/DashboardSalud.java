package com.itesm.domain.models.dashboard;

public class DashboardSalud {

    private Integer idTerritorio;
    private String nombreTerritorio;
    private String tipoTerritorio;
    private Integer idPeriodo;
    private Integer anio;
    private Long totalUnidades;
    private Long totalMedicos;
    private Long totalEnfermeras;
    private Long totalConsultorios;
    private Long totalCamasHospitalizacion;

    public DashboardSalud(
            Integer idTerritorio,
            String nombreTerritorio,
            String tipoTerritorio,
            Integer idPeriodo,
            Integer anio,
            Long totalUnidades,
            Long totalMedicos,
            Long totalEnfermeras,
            Long totalConsultorios,
            Long totalCamasHospitalizacion
    ) {
        this.idTerritorio = idTerritorio;
        this.nombreTerritorio = nombreTerritorio;
        this.tipoTerritorio = tipoTerritorio;
        this.idPeriodo = idPeriodo;
        this.anio = anio;
        this.totalUnidades = totalUnidades;
        this.totalMedicos = totalMedicos;
        this.totalEnfermeras = totalEnfermeras;
        this.totalConsultorios = totalConsultorios;
        this.totalCamasHospitalizacion = totalCamasHospitalizacion;
    }

    public Integer getIdTerritorio() {
        return idTerritorio;
    }

    public void setIdTerritorio(Integer idTerritorio) {
        this.idTerritorio = idTerritorio;
    }

    public String getNombreTerritorio() {
        return nombreTerritorio;
    }

    public void setNombreTerritorio(String nombreTerritorio) {
        this.nombreTerritorio = nombreTerritorio;
    }

    public String getTipoTerritorio() {
        return tipoTerritorio;
    }

    public void setTipoTerritorio(String tipoTerritorio) {
        this.tipoTerritorio = tipoTerritorio;
    }

    public Integer getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Integer idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Long getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Long totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public Long getTotalMedicos() {
        return totalMedicos;
    }

    public void setTotalMedicos(Long totalMedicos) {
        this.totalMedicos = totalMedicos;
    }

    public Long getTotalEnfermeras() {
        return totalEnfermeras;
    }

    public void setTotalEnfermeras(Long totalEnfermeras) {
        this.totalEnfermeras = totalEnfermeras;
    }

    public Long getTotalConsultorios() {
        return totalConsultorios;
    }

    public void setTotalConsultorios(Long totalConsultorios) {
        this.totalConsultorios = totalConsultorios;
    }

    public Long getTotalCamasHospitalizacion() {
        return totalCamasHospitalizacion;
    }

    public void setTotalCamasHospitalizacion(Long totalCamasHospitalizacion) {
        this.totalCamasHospitalizacion = totalCamasHospitalizacion;
    }
}