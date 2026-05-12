package com.itesm.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "indicadores_estado")
@NamedEntityGraph(
        name = "IndicadorEstado.withEstadoAndPeriodo",
        attributeNodes = {
                @NamedAttributeNode("estado"),
                @NamedAttributeNode("periodo")
        }
)
public class IndicadorEstadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoEntity estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_periodo", nullable = false)
    private PeriodoEntity periodo;

    @Column(name = "poblacion_total", nullable = false)
    private BigInteger poblacionTotal;

    @Column(name = "porcentaje_60_mas", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje60mas;

    @Column(name = "carencia_acceso_salud", nullable = false)
    private BigInteger carenciaAccesoSalud;

    @Column(name = "situacion_pobreza_total", nullable = false)
    private BigInteger situacionPobrezaTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoEntity getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntity estado) {
        this.estado = estado;
    }

    public PeriodoEntity getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoEntity periodo) {
        this.periodo = periodo;
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
