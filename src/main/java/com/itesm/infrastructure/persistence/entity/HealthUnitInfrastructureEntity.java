package com.itesm.infrastructure.persistence.entity;

import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.DataSourceEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_unit_infrastructure", indexes = {
    @Index(name = "idx_health_unit_infrastructure_period_unit", columnList = "period_id,health_unit_id"),
    @Index(name = "idx_health_unit_infrastructure_source", columnList = "data_source_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_health_unit_infrastructure_period", columnNames = {"health_unit_id", "period_id"})
})
public class HealthUnitInfrastructureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_unit_id", nullable = false)
    private HealthUnitEntity healthUnit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_id", nullable = false)
    private PeriodEntity period;

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
