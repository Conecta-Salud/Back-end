package com.itesm.domain.models.dashboard.summary;

public class DashboardPeriod {

    private Integer id;
    private Integer periodYear;

    public DashboardPeriod(Integer id, Integer periodYear) {
        this.id = id;
        this.periodYear = periodYear;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Integer periodYear) {
        this.periodYear = periodYear;
    }
}
