package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.period.PeriodStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "periods")
public class PeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "period_year", nullable = false, unique = true)
    private Short periodYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodStatus status;

    @Column(length = 255)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Short periodYear) {
        this.periodYear = periodYear;
    }

    public PeriodStatus getStatus() {
        return status;
    }

    public void setStatus(PeriodStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}