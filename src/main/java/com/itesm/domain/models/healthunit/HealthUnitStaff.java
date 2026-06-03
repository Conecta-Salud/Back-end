package com.itesm.domain.models.healthunit;

public class HealthUnitStaff {
    private Integer id;
    private Integer healthUnitId;
    private Integer periodId;
    private Integer totalDoctors;
    private Integer totalNurses;
    private Integer dataSourceId;
    private String sourceFile;

    public HealthUnitStaff() {}

    public HealthUnitStaff(Integer id, Integer healthUnitId, Integer periodId, Integer totalDoctors, Integer totalNurses, Integer dataSourceId, String sourceFile) {
        this.id = id;
        this.healthUnitId = healthUnitId;
        this.periodId = periodId;
        this.totalDoctors = totalDoctors;
        this.totalNurses = totalNurses;
        this.dataSourceId = dataSourceId;
        this.sourceFile = sourceFile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHealthUnitId() {
        return healthUnitId;
    }

    public void setHealthUnitId(Integer healthUnitId) {
        this.healthUnitId = healthUnitId;
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
    }

    public Integer getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Integer totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Integer getTotalNurses() {
        return totalNurses;
    }

    public void setTotalNurses(Integer totalNurses) {
        this.totalNurses = totalNurses;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
}
