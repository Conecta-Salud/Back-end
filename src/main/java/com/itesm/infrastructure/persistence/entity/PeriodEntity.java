package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.period.PeriodStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "periods")
public class PeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;

    @Column(name = "period_year",nullable = false, unique = true)
    private Integer  periodYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Integer periodYear) {
        this.periodYear = periodYear;
    }

    public PeriodStatus getStatus() {
        return status;
    }

    public void setStatus(PeriodStatus status) {
        this.status = status;
    }
}