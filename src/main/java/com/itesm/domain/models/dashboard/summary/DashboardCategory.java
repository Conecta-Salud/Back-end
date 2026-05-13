package com.itesm.domain.models.dashboard.summary;

import jakarta.ws.rs.BadRequestException;

public enum DashboardCategory {

    MEDICAL_COVERAGE("medical_coverage"),
    HOSPITAL_BEDS("hospital_beds"),
    HEALTHCARE_ACCESS_DEFICIENCY("healthcare_access_deficiency");

    private final String value;

    DashboardCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DashboardCategory fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("category is required");
        }

        for (DashboardCategory category : values()) {
            if (category.value.equalsIgnoreCase(value.trim())) {
                return category;
            }
        }

        throw new BadRequestException("Unsupported dashboard category: " + value);
    }
}