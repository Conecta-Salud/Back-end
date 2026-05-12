package com.itesm.domain.models.unidadSalud;

public class UnidadSaludDetalle {

    private Integer id;
    private String clues;
    private String nombre;

    private Integer idMunicipio;
    private String municipio;
    private Integer idEstado;
    private String estado;

    private String institucion;
    private String tipoEstablecimiento;
    private String tipologia;
    private NivelAtencion nivelAtencion;

    private PersonalUnidadResumen personal;
    private InfraestructuraUnidadResumen infraestructura;

    public UnidadSaludDetalle(
            Integer id,
            String clues,
            String nombre,
            Integer idMunicipio,
            String municipio,
            Integer idEstado,
            String estado,
            String institucion,
            String tipoEstablecimiento,
            String tipologia,
            NivelAtencion nivelAtencion,
            PersonalUnidadResumen personal,
            InfraestructuraUnidadResumen infraestructura
    ) {
        this.id = id;
        this.clues = clues;
        this.nombre = nombre;
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.idEstado = idEstado;
        this.estado = estado;
        this.institucion = institucion;
        this.tipoEstablecimiento = tipoEstablecimiento;
        this.tipologia = tipologia;
        this.nivelAtencion = nivelAtencion;
        this.personal = personal;
        this.infraestructura = infraestructura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClues() {
        return clues;
    }

    public void setClues(String clues) {
        this.clues = clues;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public PersonalUnidadResumen getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalUnidadResumen personal) {
        this.personal = personal;
    }

    public InfraestructuraUnidadResumen getInfraestructura() {
        return infraestructura;
    }

    public void setInfraestructura(InfraestructuraUnidadResumen infraestructura) {
        this.infraestructura = infraestructura;
    }
}
