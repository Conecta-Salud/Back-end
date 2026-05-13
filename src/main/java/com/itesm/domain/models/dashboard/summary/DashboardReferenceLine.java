package com.itesm.domain.models.dashboard.summary;

import java.math.BigDecimal;

public class DashboardReferenceLine {

    private BigDecimal value;
    private String label;

    public DashboardReferenceLine(BigDecimal value, String label) {
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
