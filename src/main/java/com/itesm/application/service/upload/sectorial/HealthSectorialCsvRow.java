package com.itesm.application.service.upload.sectorial;

import java.util.Map;

public class HealthSectorialCsvRow {

    private final int csvRowNumber;
    private final String yearRaw;
    private final String cluesRaw;
    private final String institutionNameRaw;
    private final String stateCodeRaw;
    private final String stateNameRaw;
    private final String municipalityCodeRaw;
    private final String municipalityNameRaw;
    private final String unitNameRaw;
    private final String establishmentTypeRaw;
    private final String medicalUnitTypeRaw;
    private final String totalConsultingRoomsRaw;
    private final String totalHospitalBedsRaw;
    private final String totalDoctorsRaw;
    private final String totalNursesRaw;
    private final Map<String, String> specialtyValues;

    public HealthSectorialCsvRow(
            int csvRowNumber,
            String yearRaw,
            String cluesRaw,
            String institutionNameRaw,
            String stateCodeRaw,
            String stateNameRaw,
            String municipalityCodeRaw,
            String municipalityNameRaw,
            String unitNameRaw,
            String establishmentTypeRaw,
            String medicalUnitTypeRaw,
            String totalConsultingRoomsRaw,
            String totalHospitalBedsRaw,
            String totalDoctorsRaw,
            String totalNursesRaw,
            Map<String, String> specialtyValues
    ) {
        this.csvRowNumber = csvRowNumber;
        this.yearRaw = yearRaw;
        this.cluesRaw = cluesRaw;
        this.institutionNameRaw = institutionNameRaw;
        this.stateCodeRaw = stateCodeRaw;
        this.stateNameRaw = stateNameRaw;
        this.municipalityCodeRaw = municipalityCodeRaw;
        this.municipalityNameRaw = municipalityNameRaw;
        this.unitNameRaw = unitNameRaw;
        this.establishmentTypeRaw = establishmentTypeRaw;
        this.medicalUnitTypeRaw = medicalUnitTypeRaw;
        this.totalConsultingRoomsRaw = totalConsultingRoomsRaw;
        this.totalHospitalBedsRaw = totalHospitalBedsRaw;
        this.totalDoctorsRaw = totalDoctorsRaw;
        this.totalNursesRaw = totalNursesRaw;
        this.specialtyValues = specialtyValues == null ? Map.of() : Map.copyOf(specialtyValues);
    }

    public int getCsvRowNumber() { return csvRowNumber; }
    public String getYearRaw() { return yearRaw; }
    public String getCluesRaw() { return cluesRaw; }
    public String getInstitutionNameRaw() { return institutionNameRaw; }
    public String getStateCodeRaw() { return stateCodeRaw; }
    public String getStateNameRaw() { return stateNameRaw; }
    public String getMunicipalityCodeRaw() { return municipalityCodeRaw; }
    public String getMunicipalityNameRaw() { return municipalityNameRaw; }
    public String getUnitNameRaw() { return unitNameRaw; }
    public String getEstablishmentTypeRaw() { return establishmentTypeRaw; }
    public String getMedicalUnitTypeRaw() { return medicalUnitTypeRaw; }
    public String getTotalConsultingRoomsRaw() { return totalConsultingRoomsRaw; }
    public String getTotalHospitalBedsRaw() { return totalHospitalBedsRaw; }
    public String getTotalDoctorsRaw() { return totalDoctorsRaw; }
    public String getTotalNursesRaw() { return totalNursesRaw; }
    public Map<String, String> getSpecialtyValues() { return specialtyValues; }

    public boolean isBlank() {
        return isBlank(yearRaw)
                && isBlank(cluesRaw)
                && isBlank(institutionNameRaw)
                && isBlank(stateCodeRaw)
                && isBlank(stateNameRaw)
                && isBlank(municipalityCodeRaw)
                && isBlank(municipalityNameRaw)
                && isBlank(unitNameRaw)
                && isBlank(establishmentTypeRaw)
                && isBlank(medicalUnitTypeRaw)
                && isBlank(totalConsultingRoomsRaw)
                && isBlank(totalHospitalBedsRaw)
                && isBlank(totalDoctorsRaw)
                && isBlank(totalNursesRaw)
                && specialtyValues.values().stream().allMatch(this::isBlank);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
