package com.itesm.domain.models.mapa;

public enum ColorToken {

    GREEN("green"),
    YELLOW("yellow"),
    RED("red"),
    NEUTRAL("neutral");

    private final String value;

    ColorToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
