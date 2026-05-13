package com.itesm.application.dto.dashboard.summary;

import java.math.BigDecimal;

public class DashboardKpiDto {

    private String id;
    private String label;
    private BigDecimal value;
    private String unit;
    private String variant;
    private Integer order;

    public DashboardKpiDto(
            String id,
            String label,
            BigDecimal value,
            String unit,
            String variant,
            Integer order
    ) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.unit = unit;
        this.variant = variant;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
