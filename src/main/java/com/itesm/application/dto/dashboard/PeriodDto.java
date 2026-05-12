package com.itesm.application.dto.dashboard;

public class PeriodDto {

    private Integer id;
    private Integer periodYear;

    public PeriodDto(Integer id, Integer periodYear) {
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