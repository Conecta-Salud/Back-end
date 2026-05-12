package com.itesm.domain.models.mapa;

import java.math.BigDecimal;

public class MapaIndicador {

    private String code;
    private String name;
    private BigDecimal value;
    private NivelMapa level;
    private ColorToken colorToken;

    public MapaIndicador(String code, String name, BigDecimal value, IndicadorMapaTipo indicadorTipo) {
        this.code = code;
        this.name = name;
        this.value = value;

        clasificar(indicadorTipo);
    }

    private void clasificar(IndicadorMapaTipo indicadorTipo) {
        if (value == null) {
            this.level = NivelMapa.SIN_DATOS;
            this.colorToken = ColorToken.NEUTRAL;
            return;
        }
        double valor = value.doubleValue();

        switch (indicadorTipo) {
            case COBERTURA_MEDICA:
                clasificarMayorEsMejor(valor, 2.7, 1.0);
                break;

            case CAMAS_HOSPITALARIAS:
                clasificarMayorEsMejor(valor, 3.0, 1.0);
                break;

            case CARENCIA_ACCESO_SALUD:
                clasificarMenorEsMejor(valor);
                break;
        }
    }

    private void clasificarMayorEsMejor(double valor, double buenoMinimo, double riesgoMinimo) {
        if (valor >= buenoMinimo) {
            this.level = NivelMapa.BUENO;
            this.colorToken = ColorToken.GREEN;
        } else if (valor >= riesgoMinimo) {
            this.level = NivelMapa.RIESGO;
            this.colorToken = ColorToken.YELLOW;
        } else {
            this.level = NivelMapa.CRITICO;
            this.colorToken = ColorToken.RED;
        }
    }

    private void clasificarMenorEsMejor(double valor) {
        if (valor <= 20) {
            this.level = NivelMapa.BUENO;
            this.colorToken = ColorToken.GREEN;
        } else if (valor < 40) {
            this.level = NivelMapa.RIESGO;
            this.colorToken = ColorToken.YELLOW;
        } else {
            this.level = NivelMapa.CRITICO;
            this.colorToken = ColorToken.RED;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public NivelMapa getLevel() {
        return level;
    }

    public void setLevel(NivelMapa level) {
        this.level = level;
    }

    public ColorToken getColorToken() {
        return colorToken;
    }

    public void setColorToken(ColorToken colorToken) {
        this.colorToken = colorToken;
    }
}
