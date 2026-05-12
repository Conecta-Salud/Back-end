package com.itesm.application.dto.unidadSalud;

public class InfraestructuraUnidadDto {

    private Long totalConsultorios;
    private Long totalCamasHospitalizacion;

    public InfraestructuraUnidadDto(Long totalConsultorios, Long totalCamasHospitalizacion) {
        this.totalConsultorios = totalConsultorios;
        this.totalCamasHospitalizacion = totalCamasHospitalizacion;
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
