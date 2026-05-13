package com.itesm.application.dto.dashboard.summary;

import java.util.List;

public class DashboardChartDto {

    private String type;
    private String title;
    private String xKey;
    private String yKey;
    private DashboardReferenceLineDto referenceLine;
    private List<DashboardChartDataPointDto> data;

    public DashboardChartDto(
            String type,
            String title,
            String xKey,
            String yKey,
            DashboardReferenceLineDto referenceLine,
            List<DashboardChartDataPointDto> data
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

    public DashboardReferenceLineDto getReferenceLine() {
        return referenceLine;
    }

    public void setReferenceLine(DashboardReferenceLineDto referenceLine) {
        this.referenceLine = referenceLine;
    }

    public List<DashboardChartDataPointDto> getData() {
        return data;
    }

    public void setData(List<DashboardChartDataPointDto> data) {
        this.data = data;
    }
}
