package com.itesm.domain.models.Uploader.indicator;

import com.itesm.domain.models.Uploader.Auxiliar.ValueType;

public class Indicators {
    private Integer id;
    private Integer categoryId;

    private String code;
    private String name;
    private String description;

    private String unit;
    private ValueType valueType;

    private Boolean higherIsBetter;
    private Boolean isCalculated;

    private String formulaDescription;

    private Integer displayOrder;
    private Boolean isActive;

    public Indicators() {}

    public Indicators(Integer id, Integer categoryId, String code, String name, String description, String unit, ValueType valueType, Boolean higherIsBetter, Boolean isCalculated, String formulaDescription, Integer displayOrder, Boolean isActive) {
        this.id = id;
        this.categoryId = categoryId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.valueType = valueType;
        this.higherIsBetter = higherIsBetter;
        this.isCalculated = isCalculated;
        this.formulaDescription = formulaDescription;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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
}
