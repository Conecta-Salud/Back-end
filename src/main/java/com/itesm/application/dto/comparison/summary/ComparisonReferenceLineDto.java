package com.itesm.application.dto.comparison.summary;

import java.math.BigDecimal;

public class ComparisonReferenceLineDto {

    private BigDecimal value;
    private String label;

    public ComparisonReferenceLineDto(BigDecimal value, String label) {
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
