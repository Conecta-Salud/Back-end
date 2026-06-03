package com.itesm.infrastructure.persistence.entity;

import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.DataSourceEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_unit_staff", indexes = {
    @Index(name = "idx_health_unit_staff_period_unit", columnList = "period_id,health_unit_id"),
    @Index(name = "idx_health_unit_staff_source", columnList = "data_source_id"),
    @Index(name = "idx_health_unit_staff_period", columnList = "health_unit_id,period_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_health_unit_staff_period", columnNames = {"health_unit_id", "period_id"})
})
public class HealthUnitStaffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_unit_id", nullable = false)
    private HealthUnitEntity healthUnit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_id", nullable = false)
    private PeriodEntity period;

    @Column(name = "total_doctors", nullable = false)
    private Integer totalDoctors = 0;

    @Column(name = "total_nurses", nullable = false)
    private Integer totalNurses = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    private DataSourceEntity dataSource;

    @Column(name = "source_file", length = 255)
    private String sourceFile;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HealthUnitEntity getHealthUnit() {
        return healthUnit;
    }

    public void setHealthUnit(HealthUnitEntity healthUnit) {
        this.healthUnit = healthUnit;
    }

    public PeriodEntity getPeriod() {
        return period;
    }

    public void setPeriod(PeriodEntity period) {
        this.period = period;
    }

    public Integer getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Integer totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Integer getTotalNurses() {
        return totalNurses;
    }

    public void setTotalNurses(Integer totalNurses) {
        this.totalNurses = totalNurses;
    }

    public DataSourceEntity getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEntity dataSource) {
        this.dataSource = dataSource;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
