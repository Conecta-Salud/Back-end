package com.itesm.application.dto.comparison;

import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.HealthDashboardDto;

public class TerritoryComparisonDto {

    private Integer id;
    private String name;
    private String type;
    private PeriodDto period;
    private DashboardIndicatorsDto indicators;
    private HealthDashboardDto health;

    public TerritoryComparisonDto(
            Integer id,
            String name,
            String type,
            PeriodDto period,
            DashboardIndicatorsDto indicators,
            HealthDashboardDto health
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.period = period;
        this.indicators = indicators;
        this.health = health;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public HealthDashboardDto getHealth() {
        return health;
    }

    public void setHealth(HealthDashboardDto health) {
        this.health = health;
    }
}
