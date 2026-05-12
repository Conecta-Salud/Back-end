package com.itesm.domain.models.map;

public enum MapLevel {

    GOOD("good"),
    RISK("risk"),
    CRITICAL("critical"),
    NO_DATA("no_data");

    private final String value;

    MapLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
