package com.itesm.domain.models.mapa;

public enum NivelMapa {

    BUENO("bueno"),
    RIESGO("riesgo"),
    CRITICO("critico"),
    SIN_DATOS("sin_datos");

    private final String value;

    NivelMapa(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
