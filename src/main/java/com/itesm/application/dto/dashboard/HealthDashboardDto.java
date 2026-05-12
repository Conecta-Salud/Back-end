package com.itesm.application.dto.dashboard;

public class HealthDashboardDto {

    private Long totalHealthUnits;
    private Long totalDoctors;
    private Long totalNurses;
    private Long totalConsultingRooms;
    private Long totalHospitalBeds;

    public HealthDashboardDto(
            Long totalHealthUnits,
            Long totalDoctors,
            Long totalNurses,
            Long totalConsultingRooms,
            Long totalHospitalBeds
    ) {
        this.totalHealthUnits = totalHealthUnits;
        this.totalDoctors = totalDoctors;
        this.totalNurses = totalNurses;
        this.totalConsultingRooms = totalConsultingRooms;
        this.totalHospitalBeds = totalHospitalBeds;
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