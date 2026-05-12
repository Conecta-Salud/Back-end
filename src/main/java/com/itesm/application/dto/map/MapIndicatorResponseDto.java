package com.itesm.application.dto.map;

import com.itesm.domain.models.map.ColorToken;
import com.itesm.domain.models.map.MapLevel;

import java.math.BigDecimal;

public class MapIndicatorResponseDto {

    private String code;
    private String name;
    private BigDecimal value;
    private MapLevel level;
    private ColorToken colorToken;

    public MapIndicatorResponseDto(
            String code,
            String name,
            BigDecimal value,
            MapLevel level,
            ColorToken colorToken
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
}
