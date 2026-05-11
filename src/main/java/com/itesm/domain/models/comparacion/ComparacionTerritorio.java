package com.itesm.domain.models.comparacion;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ComparacionTerritorio {

    private Integer id;
    private String nombre;
    private String tipo;

    private Integer idPeriodo;
    private Integer anio;

    private BigInteger poblacionTotal;
    private BigDecimal porcentaje60mas;
    private BigInteger carenciaAccesoSalud;
    private BigInteger situacionPobrezaTotal;

    private Long totalUnidades;
    private Long totalMedicos;
    private Long totalEnfermeras;
    private Long totalConsultorios;
    private Long totalCamasHospitalizacion;

    public ComparacionTerritorio(
            Integer id,
            String nombre,
            String tipo,
            Integer idPeriodo,
            Integer anio,
            BigInteger poblacionTotal,
            BigDecimal porcentaje60mas,
            BigInteger carenciaAccesoSalud,
            BigInteger situacionPobrezaTotal,
            Long totalUnidades,
            Long totalMedicos,
            Long totalEnfermeras,
            Long totalConsultorios,
            Long totalCamasHospitalizacion
    ) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.idPeriodo = idPeriodo;
        this.anio = anio;
        this.poblacionTotal = poblacionTotal;
        this.porcentaje60mas = porcentaje60mas;
        this.carenciaAccesoSalud = carenciaAccesoSalud;
        this.situacionPobrezaTotal = situacionPobrezaTotal;
        this.totalUnidades = totalUnidades;
        this.totalMedicos = totalMedicos;
        this.totalEnfermeras = totalEnfermeras;
        this.totalConsultorios = totalConsultorios;
        this.totalCamasHospitalizacion = totalCamasHospitalizacion;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Long getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Long totalUnidades) {
        this.totalUnidades = totalUnidades;
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

    public Long getTotalConsultorios() {
        return totalConsultorios;
    }

    public void setTotalConsultorios(Long totalConsultorios) {
        this.totalConsultorios = totalConsultorios;
    }

    public Long getTotalCamasHospitalizacion() {
        return totalCamasHospitalizacion;
    }

    public void setTotalCamasHospitalizacion(Long totalCamasHospitalizacion) {
        this.totalCamasHospitalizacion = totalCamasHospitalizacion;
    }
}

