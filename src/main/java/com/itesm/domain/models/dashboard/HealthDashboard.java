package com.itesm.domain.models.dashboard;

public class HealthDashboard {

    private Integer territoryId;
    private String territoryName;
    private String territoryType;
    private Integer periodId;
    private Integer periodYear;
    private Long totalHealthUnits;
    private Long totalDoctors;
    private Long totalNurses;
    private Long totalConsultingRooms;
    private Long totalHospitalBeds;

    public HealthDashboard(
            Integer territoryId,
            String territoryName,
            String territoryType,
            Integer periodId,
            Integer periodYear,
            Long totalHealthUnits,
            Long totalDoctors,
            Long totalNurses,
            Long totalConsultingRooms,
            Long totalHospitalBeds
    ) {
        this.territoryId = territoryId;
        this.territoryName = territoryName;
        this.territoryType = territoryType;
        this.periodId = periodId;
        this.periodYear = periodYear;
        this.totalHealthUnits = totalHealthUnits;
        this.totalDoctors = totalDoctors;
        this.totalNurses = totalNurses;
        this.totalConsultingRooms = totalConsultingRooms;
        this.totalHospitalBeds = totalHospitalBeds;
    }

    public Integer getTerritoryId() {
        return territoryId;
    }

    public void setTerritoryId(Integer territoryId) {
        this.territoryId = territoryId;
    }

    public String getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(String territoryName) {
        this.territoryName = territoryName;
    }

    public String getTerritoryType() {
        return territoryType;
    }

    public void setTerritoryType(String territoryType) {
        this.territoryType = territoryType;
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
    }

    public Integer getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Integer periodYear) {
        this.periodYear = periodYear;
    }

    public Long getTotalHealthUnits() {
        return totalHealthUnits;
    }

    public void setTotalHealthUnits(Long totalHealthUnits) {
        this.totalHealthUnits = totalHealthUnits;
    }

    public Long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Long getTotalNurses() {
        return totalNurses;
    }

    public void setTotalNurses(Long totalNurses) {
        this.totalNurses = totalNurses;
    }

    public Long getTotalConsultingRooms() {
        return totalConsultingRooms;
    }

    public void setTotalConsultingRooms(Long totalConsultingRooms) {
        this.totalConsultingRooms = totalConsultingRooms;
    }

    public Long getTotalHospitalBeds() {
        return totalHospitalBeds;
    }

    public void setTotalHospitalBeds(Long totalHospitalBeds) {
        this.totalHospitalBeds = totalHospitalBeds;
    }
}