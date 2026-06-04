package com.itesm.application.service.upload.establishments;

public class HealthEstablishmentCsvRow {

    private final int csvRowNumber;
    private final String cluesRaw;
    private final String institutionNameRaw;
    private final String stateCodeRaw;
    private final String stateNameRaw;
    private final String municipalityCodeRaw;
    private final String municipalityNameRaw;
    private final String localityNameRaw;
    private final String establishmentTypeRaw;
    private final String medicalUnitTypeRaw;
    private final String unitNameRaw;
    private final String operationStatusRaw;
    private final String careLevelRaw;
    private final String latitudeRaw;
    private final String longitudeRaw;

    public HealthEstablishmentCsvRow(
            int csvRowNumber,
            String cluesRaw,
            String institutionNameRaw,
            String stateCodeRaw,
            String stateNameRaw,
            String municipalityCodeRaw,
            String municipalityNameRaw,
            String localityNameRaw,
            String establishmentTypeRaw,
            String medicalUnitTypeRaw,
            String unitNameRaw,
            String operationStatusRaw,
            String careLevelRaw,
            String latitudeRaw,
            String longitudeRaw
    ) {
        this.csvRowNumber = csvRowNumber;
        this.cluesRaw = cluesRaw;
        this.institutionNameRaw = institutionNameRaw;
        this.stateCodeRaw = stateCodeRaw;
        this.stateNameRaw = stateNameRaw;
        this.municipalityCodeRaw = municipalityCodeRaw;
        this.municipalityNameRaw = municipalityNameRaw;
        this.localityNameRaw = localityNameRaw;
        this.establishmentTypeRaw = establishmentTypeRaw;
        this.medicalUnitTypeRaw = medicalUnitTypeRaw;
        this.unitNameRaw = unitNameRaw;
        this.operationStatusRaw = operationStatusRaw;
        this.careLevelRaw = careLevelRaw;
        this.latitudeRaw = latitudeRaw;
        this.longitudeRaw = longitudeRaw;
    }

    public int getCsvRowNumber() { return csvRowNumber; }
    public String getCluesRaw() { return cluesRaw; }
    public String getInstitutionNameRaw() { return institutionNameRaw; }
    public String getStateCodeRaw() { return stateCodeRaw; }
    public String getStateNameRaw() { return stateNameRaw; }
    public String getMunicipalityCodeRaw() { return municipalityCodeRaw; }
    public String getMunicipalityNameRaw() { return municipalityNameRaw; }
    public String getLocalityNameRaw() { return localityNameRaw; }
    public String getEstablishmentTypeRaw() { return establishmentTypeRaw; }
    public String getMedicalUnitTypeRaw() { return medicalUnitTypeRaw; }
    public String getUnitNameRaw() { return unitNameRaw; }
    public String getOperationStatusRaw() { return operationStatusRaw; }
    public String getCareLevelRaw() { return careLevelRaw; }
    public String getLatitudeRaw() { return latitudeRaw; }
    public String getLongitudeRaw() { return longitudeRaw; }

    public boolean isBlank() {
        return isBlank(cluesRaw)
                && isBlank(institutionNameRaw)
                && isBlank(stateCodeRaw)
                && isBlank(stateNameRaw)
                && isBlank(municipalityCodeRaw)
                && isBlank(municipalityNameRaw)
                && isBlank(localityNameRaw)
                && isBlank(establishmentTypeRaw)
                && isBlank(medicalUnitTypeRaw)
                && isBlank(unitNameRaw)
                && isBlank(operationStatusRaw)
                && isBlank(careLevelRaw)
                && isBlank(latitudeRaw)
                && isBlank(longitudeRaw);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
