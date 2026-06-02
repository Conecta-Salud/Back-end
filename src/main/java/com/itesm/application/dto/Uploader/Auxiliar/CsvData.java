package com.itesm.application.dto.Uploader.Auxiliar;
import com.itesm.domain.models.Uploader.Establecimiento.*;
import com.itesm.domain.models.healthunit.HealthUnitSummary;

import java.util.List;

public class CsvData {

    private List<State> states;
    private List<Municipality> municipalities;
    private List<Institution> institutions;
    private List<Establishment> establishments;
    private List<MedicalUnitTypes> medicalUnitTypes;
    private List<HealthUnitSummary> healthUnitSummaries;

    public CsvData(
            List<State> states,
            List<Municipality> municipalities,
            List<Institution> institutions,
            List<Establishment> establishments,
            List<MedicalUnitTypes> medicalUnitTypes,
            List<HealthUnitSummary> healthUnitSummaries
    ) {
        this.states = states;
        this.municipalities = municipalities;
        this.institutions = institutions;
        this.establishments = establishments;
        this.medicalUnitTypes = medicalUnitTypes;
        this.healthUnitSummaries = healthUnitSummaries;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<Municipality> municipalities) {
        this.municipalities = municipalities;
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public List<Establishment> getEstablishments() {
        return establishments;
    }

    public void setEstablishments(List<Establishment> establishments) {
        this.establishments = establishments;
    }

    public List<MedicalUnitTypes> getMedicalUnitTypes() {
        return medicalUnitTypes;
    }

    public void setMedicalUnitTypes(List<MedicalUnitTypes> medicalUnitTypes) {
        this.medicalUnitTypes = medicalUnitTypes;
    }

    public List<HealthUnitSummary> getHealthUnitSummaries() {
        return healthUnitSummaries;
    }

    public void setHealthUnitSummaries(List<HealthUnitSummary> healthUnitSummaries) {
        this.healthUnitSummaries = healthUnitSummaries;
    }
}