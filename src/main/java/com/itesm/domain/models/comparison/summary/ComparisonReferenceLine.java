package com.itesm.domain.models.comparison.summary;

import java.math.BigDecimal;

public class ComparisonReferenceLine {

    private BigDecimal value;
    private String label;

    public ComparisonReferenceLine(BigDecimal value, String label) {
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
