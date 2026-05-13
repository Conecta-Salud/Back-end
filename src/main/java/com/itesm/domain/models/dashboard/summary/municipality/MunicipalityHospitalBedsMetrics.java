package com.itesm.domain.models.dashboard.summary.municipality;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;
import com.itesm.domain.models.dashboard.summary.DashboardTerritory;

public class MunicipalityHospitalBedsMetrics {

    private DashboardTerritory territory;
    private DashboardPeriod period;
    private Long totalHospitals;
    private Long totalConsultingRooms;
    private Long totalHospitalBeds;
    private String predominantCareLevel;

    public MunicipalityHospitalBedsMetrics(
            DashboardTerritory territory,
            DashboardPeriod period,
            Long totalHospitals,
            Long totalConsultingRooms,
            Long totalHospitalBeds,
            String predominantCareLevel
    ) {
        this.territory = territory;
        this.period = period;
        this.totalHospitals = totalHospitals;
        this.totalConsultingRooms = totalConsultingRooms;
        this.totalHospitalBeds = totalHospitalBeds;
        this.predominantCareLevel = predominantCareLevel;
    }

    public DashboardTerritory getTerritory() {
        return territory;
    }

    public void setTerritory(DashboardTerritory territory) {
        this.territory = territory;
    }

    public DashboardPeriod getPeriod() {
        return period;
    }

    public void setPeriod(DashboardPeriod period) {
        this.period = period;
    }

    public Long getTotalHospitals() {
        return totalHospitals;
    }

    public void setTotalHospitals(Long totalHospitals) {
        this.totalHospitals = totalHospitals;
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

    public String getPredominantCareLevel() {
        return predominantCareLevel;
    }

    public void setPredominantCareLevel(String predominantCareLevel) {
        this.predominantCareLevel = predominantCareLevel;
    }
}
