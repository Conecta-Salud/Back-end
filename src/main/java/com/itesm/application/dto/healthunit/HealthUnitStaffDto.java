package com.itesm.application.dto.healthunit;

public class HealthUnitStaffDto {

    private Long totalDoctors;
    private Long totalNurses;

    public HealthUnitStaffDto(Long totalDoctors, Long totalNurses) {
        this.totalDoctors = totalDoctors;
        this.totalNurses = totalNurses;
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
}
