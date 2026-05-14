package com.itesm.domain.models.comparison.summary;

import jakarta.ws.rs.BadRequestException;

public enum ComparisonLevel {

    STATE("state"),
    MUNICIPALITY("municipality");

    private final String value;

    ComparisonLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ComparisonLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("comparison level is required");
        }

        for (ComparisonLevel level : values()) {
            if (level.value.equalsIgnoreCase(value.trim())) {
                return level;
            }
        }

        throw new BadRequestException("Unsupported comparison level: " + value);
    }
}