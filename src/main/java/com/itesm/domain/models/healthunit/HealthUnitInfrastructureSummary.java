package com.itesm.domain.models.healthunit;

public class HealthUnitInfrastructureSummary {

    private Long totalConsultingRooms;
    private Long totalHospitalBeds;

    public HealthUnitInfrastructureSummary(Long totalConsultingRooms, Long totalHospitalBeds) {
        this.totalConsultingRooms = totalConsultingRooms;
        this.totalHospitalBeds = totalHospitalBeds;
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
