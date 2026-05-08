package com.itesm.application.dto.unidadSalud;

public class TerritorioUnidadDto {

    private Integer idMunicipio;
    private String municipio;
    private Integer idEstado;
    private String estado;

    public TerritorioUnidadDto(Integer idMunicipio, String municipio, Integer idEstado, String estado) {
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.idEstado = idEstado;
        this.estado = estado;
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
}
