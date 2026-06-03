package com.itesm.application.dto.Uploader.Auxiliar;

import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;

import java.util.List;

public class CsvIndicatorData {

    private List<TerritoryIndicatorValues> indicatorValues;

    public CsvIndicatorData(List<TerritoryIndicatorValues> indicatorValues) {
        this.indicatorValues = indicatorValues;
    }

    public List<TerritoryIndicatorValues> getIndicatorValues() {
        return indicatorValues;
    }

    public void setIndicatorValues(List<TerritoryIndicatorValues> indicatorValues) {
        this.indicatorValues = indicatorValues;
    }
}
