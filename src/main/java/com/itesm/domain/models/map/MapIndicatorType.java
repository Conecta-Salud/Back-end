package com.itesm.domain.models.map;

import jakarta.ws.rs.BadRequestException;

public enum MapIndicatorType {
    MEDICAL_COVERAGE("medical_coverage"),
    HOSPITAL_BEDS("hospital_beds"),
    HEALTHCARE_ACCESS_DEFICIENCY("healthcare_access_deficiency");

    private final String value;

    MapIndicatorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MapIndicatorType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Indicator is required");
        }

        for (MapIndicatorType type : values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new BadRequestException("Unsupported indicator: " + value);
    }
}
