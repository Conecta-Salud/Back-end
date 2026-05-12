package com.itesm.application.dto.dashboard;

public class IndicatorsResponseDto {

    private TerritoryDto territory;
    private PeriodDto period;
    private DashboardIndicatorsDto indicators;

    public IndicatorsResponseDto(
            TerritoryDto territory,
            PeriodDto period,
            DashboardIndicatorsDto indicators
    ) {
        this.territory = territory;
        this.period = period;
        this.indicators = indicators;
    }

    public TerritoryDto getTerritory() {
        return territory;
    }

    public void setTerritory(TerritoryDto territory) {
        this.territory = territory;
    }

    public PeriodDto getPeriod() {
        return period;
    }

    public void setPeriod(PeriodDto period) {
        this.period = period;
    }

    public DashboardIndicatorsDto getIndicators() {
        return indicators;
    }

    public void setIndicators(DashboardIndicatorsDto indicators) {
        this.indicators = indicators;
    }
}