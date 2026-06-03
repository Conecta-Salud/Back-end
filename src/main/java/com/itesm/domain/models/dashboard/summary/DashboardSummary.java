package com.itesm.domain.models.dashboard.summary;

import java.util.List;

public class DashboardSummary {

    private DashboardTerritory territory;
    private DashboardPeriod period;
    private DashboardCategory category;
    private List<DashboardKpi> kpis;
    private DashboardRanking ranking;
    private DashboardChart mainChart;
    private DashboardChart secondaryChart;

    public DashboardSummary(
            DashboardTerritory territory,
            DashboardPeriod period,
            DashboardCategory category,
            List<DashboardKpi> kpis,
            DashboardRanking ranking,
            DashboardChart mainChart,
            DashboardChart secondaryChart
    ) {
        this.territory = territory;
        this.period = period;
        this.category = category;
        this.kpis = kpis;
        this.ranking = ranking;
        this.mainChart = mainChart;
        this.secondaryChart = secondaryChart;
    }

    public DashboardTerritory getTerritory() {
        return territory;
    }

    public void setTerritory(DashboardTerritory territory) {
        this.territory = territory;
    }

    public DashboardPeriod getPeriod() {
        return period;
    }

    public void setPeriod(DashboardPeriod period) {
        this.period = period;
    }

    public DashboardCategory getCategory() {
        return category;
    }

    public void setCategory(DashboardCategory category) {
        this.category = category;
    }

    public List<DashboardKpi> getKpis() {
        return kpis;
    }

    public void setKpis(List<DashboardKpi> kpis) {
        this.kpis = kpis;
    }

    public DashboardRanking getRanking() {
        return ranking;
    }

    public void setRanking(DashboardRanking ranking) {
        this.ranking = ranking;
    }

    public DashboardChart getMainChart() {
        return mainChart;
    }

    public void setMainChart(DashboardChart mainChart) {
        this.mainChart = mainChart;
    }

    public DashboardChart getSecondaryChart() {
        return secondaryChart;
    }

    public void setSecondaryChart(DashboardChart secondaryChart) {
        this.secondaryChart = secondaryChart;
    }
}
