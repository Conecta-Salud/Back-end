package com.itesm.infrastructure.persistence.entity.Upload.Indicadores;

import com.itesm.domain.models.Uploader.Auxiliar.ValueType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "indicators")
public class IndicatorsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private IndicatorCategoriesEntity category;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private ValueType valueType;

    @Column(name = "higher_is_better", nullable = false)
    private Boolean higherIsBetter;

    @Column(name = "is_calculated", nullable = false)
    private Boolean isCalculated;

    @Column(name = "formula_description", columnDefinition = "TEXT")
    private String formulaDescription;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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

    public IndicatorCategoriesEntity getCategory() {
        return category;
    }

    public void setCategory(IndicatorCategoriesEntity category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public Boolean getHigherIsBetter() {
        return higherIsBetter;
    }

    public void setHigherIsBetter(Boolean higherIsBetter) {
        this.higherIsBetter = higherIsBetter;
    }

    public Boolean getCalculated() {
        return isCalculated;
    }

    public void setCalculated(Boolean calculated) {
        isCalculated = calculated;
    }

    public String getFormulaDescription() {
        return formulaDescription;
    }

    public void setFormulaDescription(String formulaDescription) {
        this.formulaDescription = formulaDescription;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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
