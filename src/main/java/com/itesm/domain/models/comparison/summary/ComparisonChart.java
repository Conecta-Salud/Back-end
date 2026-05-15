package com.itesm.domain.models.comparison.summary;

import java.util.List;

public class ComparisonChart {

    private String id;
    private String title;
    private String type;
    private ComparisonReferenceLine referenceLine;
    private List<ComparisonChartDataPoint> data;

    public ComparisonChart(
            String id,
            String title,
            String type,
            ComparisonReferenceLine referenceLine,
            List<ComparisonChartDataPoint> data
    ) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.referenceLine = referenceLine;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ComparisonReferenceLine getReferenceLine() {
        return referenceLine;
    }

    public void setReferenceLine(ComparisonReferenceLine referenceLine) {
        this.referenceLine = referenceLine;
    }

    public List<ComparisonChartDataPoint> getData() {
        return data;
    }

    public void setData(List<ComparisonChartDataPoint> data) {
        this.data = data;
    }
}
