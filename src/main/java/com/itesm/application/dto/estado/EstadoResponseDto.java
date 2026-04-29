package com.itesm.application.dto.estado;

import java.math.BigDecimal;

public class EstadoResponseDto {

    private Integer id;
    private String nombre;
    private String claveInegi;
    private BigDecimal lat;
    private BigDecimal lng;

    public EstadoResponseDto(Integer id, String nombre, String claveInegi, BigDecimal lat, BigDecimal lng) {
        this.id = id;
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
