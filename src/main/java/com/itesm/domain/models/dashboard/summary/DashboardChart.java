package com.itesm.domain.models.dashboard.summary;

import java.util.List;

public class DashboardChart {

    private String type;
    private String title;
    private String xKey;
    private String yKey;
    private DashboardReferenceLine referenceLine;
    private List<DashboardChartDataPoint> data;

    public DashboardChart(
            String type,
            String title,
            String xKey,
            String yKey,
            DashboardReferenceLine referenceLine,
            List<DashboardChartDataPoint> data
    ) {
        this.type = type;
        this.title = title;
        this.xKey = xKey;
        this.yKey = yKey;
        this.referenceLine = referenceLine;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getxKey() {
        return xKey;
    }

    public void setxKey(String xKey) {
        this.xKey = xKey;
    }

    public String getyKey() {
        return yKey;
    }

    public void setyKey(String yKey) {
        this.yKey = yKey;
    }

    public DashboardReferenceLine getReferenceLine() {
        return referenceLine;
    }

    public void setReferenceLine(DashboardReferenceLine referenceLine) {
        this.referenceLine = referenceLine;
    }

    public List<DashboardChartDataPoint> getData() {
        return data;
    }

    public void setData(List<DashboardChartDataPoint> data) {
        this.data = data;
    }
}
