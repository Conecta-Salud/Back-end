package com.itesm.application.dto.unidadSalud;

import com.itesm.domain.models.unidadSalud.NivelAtencion;

public class ClasificacionUnidadDto {

    private String institucion;
    private String tipoEstablecimiento;
    private String tipologia;
    private NivelAtencion nivelAtencion;

    public ClasificacionUnidadDto(String institucion, String tipoEstablecimiento, String tipologia, NivelAtencion nivelAtencion) {
        this.institucion = institucion;
        this.tipoEstablecimiento = tipoEstablecimiento;
        this.tipologia = tipologia;
        this.nivelAtencion = nivelAtencion;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getTipoEstablecimiento() {
        return tipoEstablecimiento;
    }

    public void setTipoEstablecimiento(String tipoEstablecimiento) {
        this.tipoEstablecimiento = tipoEstablecimiento;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public NivelAtencion getNivelAtencion() {
        return nivelAtencion;
    }

    public void setNivelAtencion(NivelAtencion nivelAtencion) {
        this.nivelAtencion = nivelAtencion;
    }
}
