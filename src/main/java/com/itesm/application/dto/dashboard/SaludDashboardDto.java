package com.itesm.application.dto.dashboard;

public class SaludDashboardDto {

    private Long totalUnidades;
    private Long totalMedicos;
    private Long totalEnfermeras;
    private Long totalConsultorios;
    private Long totalCamasHospitalizacion;

    public SaludDashboardDto(
            Long totalUnidades,
            Long totalMedicos,
            Long totalEnfermeras,
            Long totalConsultorios,
            Long totalCamasHospitalizacion
    ) {
        this.totalUnidades = totalUnidades;
        this.totalMedicos = totalMedicos;
        this.totalEnfermeras = totalEnfermeras;
        this.totalConsultorios = totalConsultorios;
        this.totalCamasHospitalizacion = totalCamasHospitalizacion;
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