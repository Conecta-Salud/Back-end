package com.itesm.application.dto.healthunit;

import com.itesm.domain.models.healthunit.CareLevel;

public class HealthUnitClassificationDto {

    private String institution;
    private String establishmentType;
    private String medicalUnitType;
    private CareLevel careLevel;

    public HealthUnitClassificationDto(
            String institution,
            String establishmentType,
            String medicalUnitType,
            CareLevel careLevel
    ) {
        this.institution = institution;
        this.establishmentType = establishmentType;
        this.medicalUnitType = medicalUnitType;
        this.careLevel = careLevel;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getEstablishmentType() {
        return establishmentType;
    }

    public void setEstablishmentType(String establishmentType) {
        this.establishmentType = establishmentType;
    }

    public String getMedicalUnitType() {
        return medicalUnitType;
    }

    public void setMedicalUnitType(String medicalUnitType) {
        this.medicalUnitType = medicalUnitType;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }
}
