package com.itesm.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_unit_staff_specialties", indexes = {
    @Index(name = "idx_health_unit_staff_specialties_staff", columnList = "health_unit_staff_id"),
    @Index(name = "idx_health_unit_staff_specialties_specialty", columnList = "specialty_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_health_unit_staff_specialty", columnNames = {"health_unit_staff_id", "specialty_id"})
})
public class HealthUnitStaffSpecialtyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_unit_staff_id", nullable = false)
    private HealthUnitStaffEntity healthUnitStaff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialty_id", nullable = false)
    private SpecialtyEntity specialty;

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

    public HealthUnitStaffEntity getHealthUnitStaff() {
        return healthUnitStaff;
    }

    public void setHealthUnitStaff(HealthUnitStaffEntity healthUnitStaff) {
        this.healthUnitStaff = healthUnitStaff;
    }

    public SpecialtyEntity getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyEntity specialty) {
        this.specialty = specialty;
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
