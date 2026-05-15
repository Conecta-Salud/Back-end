package com.itesm.application.dto.comparison.summary;

import java.util.List;

public class ComparisonChartDto {

    private String id;
    private String title;
    private String type;
    private ComparisonReferenceLineDto referenceLine;
    private List<ComparisonChartDataPointDto> data;

    public ComparisonChartDto(
            String id,
            String title,
            String type,
            ComparisonReferenceLineDto referenceLine,
            List<ComparisonChartDataPointDto> data
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

    public ComparisonReferenceLineDto getReferenceLine() {
        return referenceLine;
    }

    public void setReferenceLine(ComparisonReferenceLineDto referenceLine) {
        this.referenceLine = referenceLine;
    }

    public List<ComparisonChartDataPointDto> getData() {
        return data;
    }

    public void setData(List<ComparisonChartDataPointDto> data) {
        this.data = data;
    }
}
