package com.itesm.application.dto.dashboard.summary;

import java.util.List;

public class DashboardSummaryDto {

    private DashboardTerritoryDto territory;
    private DashboardPeriodDto period;
    private String category;
    private List<DashboardKpiDto> kpis;
    private DashboardRankingDto ranking;
    private DashboardChartDto mainChart;
    private DashboardChartDto secondaryChart;

    public DashboardSummaryDto(
            DashboardTerritoryDto territory,
            DashboardPeriodDto period,
            String category,
            List<DashboardKpiDto> kpis,
            DashboardRankingDto ranking,
            DashboardChartDto mainChart,
            DashboardChartDto secondaryChart
    ) {
        this.territory = territory;
        this.period = period;
        this.category = category;
        this.kpis = kpis;
        this.ranking = ranking;
        this.mainChart = mainChart;
        this.secondaryChart = secondaryChart;
    }

    public DashboardTerritoryDto getTerritory() {
        return territory;
    }

    public void setTerritory(DashboardTerritoryDto territory) {
        this.territory = territory;
    }

    public DashboardPeriodDto getPeriod() {
        return period;
    }

    public void setPeriod(DashboardPeriodDto period) {
        this.period = period;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<DashboardKpiDto> getKpis() {
        return kpis;
    }

    public void setKpis(List<DashboardKpiDto> kpis) {
        this.kpis = kpis;
    }

    public DashboardRankingDto getRanking() {
        return ranking;
    }

    public void setRanking(DashboardRankingDto ranking) {
        this.ranking = ranking;
    }

    public DashboardChartDto getMainChart() {
        return mainChart;
    }

    public void setMainChart(DashboardChartDto mainChart) {
        this.mainChart = mainChart;
    }

    public DashboardChartDto getSecondaryChart() {
        return secondaryChart;
    }

    public void setSecondaryChart(DashboardChartDto secondaryChart) {
        this.secondaryChart = secondaryChart;
    }
}
