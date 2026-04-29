package com.itesm.domain.models.indicador;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IndicadorEstado {
    private Integer id;
    private Integer idEstado;
    private String nombreEstado;
    private Integer idPeriodo;
    private Integer anio;
    private BigInteger poblacionTotal;
    private BigDecimal porcentaje60mas;
    private BigInteger carenciaAccesoSalud;
    private BigInteger situacionPobrezaTotal;

    public IndicadorEstado(
            Integer id,
            Integer idEstado,
            String nombreEstado,
            Integer idPeriodo,
            Integer anio,
            BigInteger poblacionTotal,
            BigDecimal porcentaje60mas,
            BigInteger carenciaAccesoSalud,
            BigInteger situacionPobrezaTotal
    ) {
        this.id = id;
        this.idEstado = idEstado;
        this.nombreEstado = nombreEstado;
        this.idPeriodo = idPeriodo;
        this.anio = anio;
        this.poblacionTotal = poblacionTotal;
        this.porcentaje60mas = porcentaje60mas;
        this.carenciaAccesoSalud = carenciaAccesoSalud;
        this.situacionPobrezaTotal = situacionPobrezaTotal;
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

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public Integer getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Integer idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigInteger getPoblacionTotal() {
        return poblacionTotal;
    }

    public void setPoblacionTotal(BigInteger poblacionTotal) {
        this.poblacionTotal = poblacionTotal;
    }

    public BigDecimal getPorcentaje60mas() {
        return porcentaje60mas;
    }

    public void setPorcentaje60mas(BigDecimal porcentaje60mas) {
        this.porcentaje60mas = porcentaje60mas;
    }

    public BigInteger getCarenciaAccesoSalud() {
        return carenciaAccesoSalud;
    }

    public void setCarenciaAccesoSalud(BigInteger carenciaAccesoSalud) {
        this.carenciaAccesoSalud = carenciaAccesoSalud;
    }

    public BigInteger getSituacionPobrezaTotal() {
        return situacionPobrezaTotal;
    }

    public void setSituacionPobrezaTotal(BigInteger situacionPobrezaTotal) {
        this.situacionPobrezaTotal = situacionPobrezaTotal;
    }
}
