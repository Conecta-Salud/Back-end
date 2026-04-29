package com.itesm.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "estados")
public class EstadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "clave_inegi", nullable = false, length = 10)
    private String claveInegi;

    @Column(precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(precision = 10, scale = 7)
    private BigDecimal  lng;

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
