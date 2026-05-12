package com.itesm.application.dto.mapa;

import java.math.BigDecimal;

public class MapaIndicadorResponseDto {

    private String code;
    private String name;
    private BigDecimal value;
    private String level;
    private String colorToken;

    public MapaIndicadorResponseDto(
            String code,
            String name,
            BigDecimal value,
            String level,
            String colorToken
    ) {
        this.code = code;
        this.name = name;
        this.value = value;
        this.level = level;
        this.colorToken = colorToken;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColorToken() {
        return colorToken;
    }

    public void setColorToken(String colorToken) {
        this.colorToken = colorToken;
    }
}
