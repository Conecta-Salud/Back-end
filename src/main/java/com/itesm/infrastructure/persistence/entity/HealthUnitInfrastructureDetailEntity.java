package com.itesm.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_unit_infrastructure_details", indexes = {
    @Index(name = "idx_health_unit_infrastructure_details_parent", columnList = "health_unit_infrastructure_id"),
    @Index(name = "idx_health_unit_infrastructure_details_type", columnList = "infrastructure_type_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_health_unit_infrastructure_detail", columnNames = {"health_unit_infrastructure_id", "infrastructure_type_id"})
})
public class HealthUnitInfrastructureDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_unit_infrastructure_id", nullable = false)
    private HealthUnitInfrastructureEntity healthUnitInfrastructure;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "infrastructure_type_id", nullable = false)
    private InfrastructureTypeEntity infrastructureType;

    @Column(nullable = false)
    private Integer quantity = 0;

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

    public HealthUnitInfrastructureEntity getHealthUnitInfrastructure() {
        return healthUnitInfrastructure;
    }

    public void setHealthUnitInfrastructure(HealthUnitInfrastructureEntity healthUnitInfrastructure) {
        this.healthUnitInfrastructure = healthUnitInfrastructure;
    }

    public InfrastructureTypeEntity getInfrastructureType() {
        return infrastructureType;
    }

    public void setInfrastructureType(InfrastructureTypeEntity infrastructureType) {
        this.infrastructureType = infrastructureType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
