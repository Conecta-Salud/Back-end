package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.catalog.IndicatorValueType;
import jakarta.persistence.*;

@Entity
@Table(name = "indicators")
public class IndicatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private IndicatorCategoryEntity category;

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
    private IndicatorValueType valueType;

    @Column(name = "higher_is_better", nullable = false)
    private boolean higherIsBetter = true;

    @Column(name = "is_calculated", nullable = false)
    private boolean calculated = false;

    @Column(name = "formula_description", columnDefinition = "TEXT")
    private String formulaDescription;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IndicatorCategoryEntity getCategory() {
        return category;
    }

    public void setCategory(IndicatorCategoryEntity category) {
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

    public IndicatorValueType getValueType() {
        return valueType;
    }

    public void setValueType(IndicatorValueType valueType) {
        this.valueType = valueType;
    }

    public boolean isHigherIsBetter() {
        return higherIsBetter;
    }

    public void setHigherIsBetter(boolean higherIsBetter) {
        this.higherIsBetter = higherIsBetter;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}