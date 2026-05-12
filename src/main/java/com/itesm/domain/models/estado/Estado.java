package com.itesm.domain.models.estado;

import java.math.BigDecimal;

public class Estado {

    private Integer  id;
    private String nombre;
    private String claveInegi;
    private BigDecimal lat;
    private BigDecimal  lng;

    public Estado(Integer id, String nombre, String claveIngei, BigDecimal  lat, BigDecimal  lng) {
        this.id = id;
        this.nombre = nombre;
        this.claveInegi = claveIngei;
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

    public String getClaveIngei() {
        return claveInegi;
    }

    public void setClaveIngei(String claveIngei) {
        this.claveInegi = claveIngei;
    }

    public BigDecimal  getLat() {
        return lat;
    }

    public void setLat(BigDecimal  lat) {
        this.lat = lat;
    }

    public BigDecimal  getLng() {
        return lng;
    }

    public void setLng(BigDecimal  lng) {
        this.lng = lng;
    }
}
