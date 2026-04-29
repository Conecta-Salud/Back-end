package com.itesm.application.dto.municipio;

import java.math.BigDecimal;

public class MunicipioResponseDto {

    private Integer id;
    private Integer idEstado;
    private String nombre;
    private String claveInegi;
    private BigDecimal lat;
    private BigDecimal lng;

    public MunicipioResponseDto(Integer id, Integer idEstado, String nombre, String claveInegi, BigDecimal lat, BigDecimal lng) {
        this.id = id;
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.claveInegi = claveInegi;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClaveInegi() {
        return claveInegi;
    }

    public void setClaveInegi(String claveInegi) {
        this.claveInegi = claveInegi;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }
}
