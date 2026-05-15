package com.itesm.domain.models.comparison.summary;

import java.util.List;

public class ComparisonSummary {

    private ComparisonPeriod period;
    private String level;
    private List<ComparisonTerritory> territories;
    private List<ComparisonChart> charts;
    private List<ComparisonPriorityResult> priority;

    public ComparisonSummary(
            ComparisonPeriod period,
            String level,
            List<ComparisonTerritory> territories,
            List<ComparisonChart> charts,
            List<ComparisonPriorityResult> priority
    ) {
        this.period = period;
        this.level = level;
        this.territories = territories;
        this.charts = charts;
        this.priority = priority;
    }

    public ComparisonPeriod getPeriod() {
        return period;
    }

    public void setPeriod(ComparisonPeriod period) {
        this.period = period;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<ComparisonTerritory> getTerritories() {
        return territories;
    }

    public void setTerritories(List<ComparisonTerritory> territories) {
        this.territories = territories;
    }

    public List<ComparisonChart> getCharts() {
        return charts;
    }

    public void setCharts(List<ComparisonChart> charts) {
        this.charts = charts;
    }

    public List<ComparisonPriorityResult> getPriority() {
        return priority;
    }

    public void setPriority(List<ComparisonPriorityResult> priority) {
        this.priority = priority;
    }
}
