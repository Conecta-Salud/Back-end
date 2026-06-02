package com.itesm.infrastructure.persistence.entity.Upload.Establecimientos;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "states")
public class StateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "inegi_code", length = 10)
    private String inegiCode;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal  longitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInegiCode() {
        return inegiCode;
    }

    public void setInegiCode(String inegiCode) {
        this.inegiCode = inegiCode;
    }

}
