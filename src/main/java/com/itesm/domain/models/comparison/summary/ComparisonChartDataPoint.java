package com.itesm.domain.models.comparison.summary;

import java.math.BigDecimal;
import java.util.Map;

public class ComparisonChartDataPoint {

    private String territoryCode;
    private String label;
    private String subtitle;
    private BigDecimal value;
    private String variant;
    private Map<String, Object> extra;

    public ComparisonChartDataPoint(
            String territoryCode,
            String label,
            String subtitle,
            BigDecimal value,
            String variant,
            Map<String, Object> extra
    ) {
        this.territoryCode = territoryCode;
        this.label = label;
        this.subtitle = subtitle;
        this.value = value;
        this.variant = variant;
        this.extra = extra;
    }

    public String getTerritoryCode() {
        return territoryCode;
    }

    public void setTerritoryCode(String territoryCode) {
        this.territoryCode = territoryCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
