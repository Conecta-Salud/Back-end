package com.itesm.application.dto.state;

import java.math.BigDecimal;

public class StateResponseDto {

    private Integer id;
    private String name;
    private String inegiCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public StateResponseDto(
            Integer id,
            String name,
            String inegiCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.id = id;
        this.name = name;
        this.inegiCode = inegiCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInegiCode() {
        return inegiCode;
    }

    public void setInegiCode(String inegiCode) {
        this.inegiCode = inegiCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
