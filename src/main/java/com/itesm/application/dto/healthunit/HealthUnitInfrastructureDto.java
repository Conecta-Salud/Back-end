package com.itesm.application.dto.healthunit;

public class HealthUnitInfrastructureDto {

    private Long totalConsultingRooms;
    private Long totalHospitalBeds;

    public HealthUnitInfrastructureDto(
            Long totalConsultingRooms,
            Long totalHospitalBeds
    ) {
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
