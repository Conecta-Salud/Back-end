package com.itesm.domain.models.mapa;

import jakarta.ws.rs.BadRequestException;

public enum IndicadorMapaTipo {
    COBERTURA_MEDICA("cobertura_medica"),
    CAMAS_HOSPITALARIAS("camas_hospitalarias"),
    CARENCIA_ACCESO_SALUD("carencia_acceso_salud");

    private final String value;

    IndicadorMapaTipo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IndicadorMapaTipo fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("El indicador es obligatorio");
        }

        for (IndicadorMapaTipo tipo : values()) {
            if (tipo.value.equalsIgnoreCase(value.trim())) {
                return tipo;
            }
        }

        throw new BadRequestException("Indicador no soportado: " + value);
    }
}
