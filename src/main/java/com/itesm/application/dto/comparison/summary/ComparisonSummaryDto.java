package com.itesm.application.dto.comparison.summary;

import java.util.List;

public class ComparisonSummaryDto {

    private ComparisonPeriodDto period;
    private String level;
    private List<ComparisonTerritoryDto> territories;
    private List<ComparisonChartDto> charts;
    private List<ComparisonPriorityResultDto> priority;

    public ComparisonSummaryDto(
            ComparisonPeriodDto period,
            String level,
            List<ComparisonTerritoryDto> territories,
            List<ComparisonChartDto> charts,
            List<ComparisonPriorityResultDto> priority
    ) {
        this.period = period;
        this.level = level;
        this.territories = territories;
        this.charts = charts;
        this.priority = priority;
    }

    public ComparisonPeriodDto getPeriod() {
        return period;
    }

    public void setPeriod(ComparisonPeriodDto period) {
        this.period = period;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<ComparisonTerritoryDto> getTerritories() {
        return territories;
    }

    public void setTerritories(List<ComparisonTerritoryDto> territories) {
        this.territories = territories;
    }

    public List<ComparisonChartDto> getCharts() {
        return charts;
    }

    public void setCharts(List<ComparisonChartDto> charts) {
        this.charts = charts;
    }

    public List<ComparisonPriorityResultDto> getPriority() {
        return priority;
    }

    public void setPriority(List<ComparisonPriorityResultDto> priority) {
        this.priority = priority;
    }
}
