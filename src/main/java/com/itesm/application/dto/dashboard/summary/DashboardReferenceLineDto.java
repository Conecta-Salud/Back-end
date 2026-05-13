package com.itesm.application.dto.dashboard.summary;

import java.math.BigDecimal;

public class DashboardReferenceLineDto {

    private BigDecimal value;
    private String label;

    public DashboardReferenceLineDto(BigDecimal value, String label) {
        this.value = value;
        this.label = label;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
