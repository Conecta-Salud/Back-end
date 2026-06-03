package com.itesm.domain.models.map;

import jakarta.ws.rs.BadRequestException;

public enum MapIndicatorType {
    MEDICAL_COVERAGE("medical_coverage", "doctors_per_1000"),
    HOSPITAL_BEDS("hospital_beds", "beds_per_1000"),
    HEALTHCARE_ACCESS_DEFICIENCY("healthcare_access_deficiency", "healthcare_access_deficiency");

    private final String apiValue;
    private final String indicatorCode;

    MapIndicatorType(String apiValue, String indicatorCode) {
        this.apiValue = apiValue;
        this.indicatorCode = indicatorCode;
    }

    public String getValue() {
        return apiValue;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public static MapIndicatorType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Indicator is required");
        }

        for (MapIndicatorType type : values()) {
            if (type.apiValue.equalsIgnoreCase(value.trim())
                    || type.indicatorCode.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new BadRequestException("Unsupported indicator: " + value);
    }
}