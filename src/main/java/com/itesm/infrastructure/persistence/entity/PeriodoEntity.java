package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.periodo.PeriodoEstatus;
import jakarta.persistence.*;

@Entity
@Table(name = "periodos")
public class PeriodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;

    @Column(nullable = false, unique = true)
    private Integer  anio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodoEstatus estatus;

    public Integer  getId() {
        return id;
    }

    public Integer  getAnio() {
        return anio;
    }

    public PeriodoEstatus getEstatus() {
        return estatus;
    }
}