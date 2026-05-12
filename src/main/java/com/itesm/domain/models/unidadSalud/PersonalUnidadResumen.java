package com.itesm.domain.models.unidadSalud;

public class PersonalUnidadResumen {

    private Long totalMedicos;
    private Long totalEnfermeras;

    public PersonalUnidadResumen(Long totalMedicos, Long totalEnfermeras) {
        this.totalMedicos = totalMedicos;
        this.totalEnfermeras = totalEnfermeras;
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
}
