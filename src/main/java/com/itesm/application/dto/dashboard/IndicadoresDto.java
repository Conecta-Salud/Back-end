package com.itesm.application.dto.dashboard;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IndicadoresDto {

    private BigInteger poblacionTotal;
    private BigDecimal porcentaje60mas;
    private BigInteger carenciaAccesoSalud;
    private BigInteger situacionPobrezaTotal;

    public IndicadoresDto(
            BigInteger poblacionTotal,
            BigDecimal porcentaje60mas,
            BigInteger carenciaAccesoSalud,
            BigInteger situacionPobrezaTotal
    ) {
        this.poblacionTotal = poblacionTotal;
        this.porcentaje60mas = porcentaje60mas;
        this.carenciaAccesoSalud = carenciaAccesoSalud;
        this.situacionPobrezaTotal = situacionPobrezaTotal;
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