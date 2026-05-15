package com.itesm.domain.models.dashboard.summary.state;

import com.itesm.domain.models.dashboard.summary.DashboardPeriod;
import com.itesm.domain.models.dashboard.summary.DashboardTerritory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StateHospitalBedsMetrics {

    private DashboardTerritory territory;
    private DashboardPeriod period;
    private BigInteger totalPopulation;
    private Long totalHospitalBeds;
    private BigDecimal hospitalBedsPer1000;
    private Long municipalitiesWithHospitalDeficit;
    private Long totalHospitals;
    private Long totalConsultingRooms;

    public StateHospitalBedsMetrics(
            DashboardTerritory territory,
            DashboardPeriod period,
            BigInteger totalPopulation,
            Long totalHospitalBeds,
            BigDecimal hospitalBedsPer1000,
            Long municipalitiesWithHospitalDeficit,
            Long totalHospitals,
            Long totalConsultingRooms
    ) {
        this.territory = territory;
        this.period = period;
        this.totalPopulation = totalPopulation;
        this.totalHospitalBeds = totalHospitalBeds;
        this.hospitalBedsPer1000 = hospitalBedsPer1000;
        this.municipalitiesWithHospitalDeficit = municipalitiesWithHospitalDeficit;
        this.totalHospitals = totalHospitals;
        this.totalConsultingRooms = totalConsultingRooms;
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

    public BigInteger getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(BigInteger totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public Long getTotalHospitalBeds() {
        return totalHospitalBeds;
    }

    public void setTotalHospitalBeds(Long totalHospitalBeds) {
        this.totalHospitalBeds = totalHospitalBeds;
    }

    public BigDecimal getHospitalBedsPer1000() {
        return hospitalBedsPer1000;
    }

    public void setHospitalBedsPer1000(BigDecimal hospitalBedsPer1000) {
        this.hospitalBedsPer1000 = hospitalBedsPer1000;
    }

    public Long getMunicipalitiesWithHospitalDeficit() {
        return municipalitiesWithHospitalDeficit;
    }

    public void setMunicipalitiesWithHospitalDeficit(Long municipalitiesWithHospitalDeficit) {
        this.municipalitiesWithHospitalDeficit = municipalitiesWithHospitalDeficit;
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
}
