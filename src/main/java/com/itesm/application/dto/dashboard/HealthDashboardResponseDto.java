package com.itesm.application.dto.dashboard;

public class HealthDashboardResponseDto {

    private TerritoryDto territory;
    private PeriodDto period;
    private HealthDashboardDto health;

    public HealthDashboardResponseDto(
            TerritoryDto territory,
            PeriodDto period,
            HealthDashboardDto health
    ) {
        this.territory = territory;
        this.period = period;
        this.health = health;
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

    public HealthDashboardDto getHealth() {
        return health;
    }

    public void setHealth(HealthDashboardDto health) {
        this.health = health;
    }
}