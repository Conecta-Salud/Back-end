package com.itesm.domain.models.period;

public class Period {

    private Integer id;
    private Integer periodYear;
    private PeriodStatus status;

    public Period(Integer id, Integer periodYear, PeriodStatus status) {
        this.id = id;
        this.periodYear = periodYear;
        this.status = status;
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

    public PeriodStatus getStatus() {
        return status;
    }

    public void setStatus(PeriodStatus status) {
        this.status = status;
    }
}
