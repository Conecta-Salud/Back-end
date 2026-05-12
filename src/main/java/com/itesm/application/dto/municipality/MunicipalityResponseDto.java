package com.itesm.application.dto.municipality;

import java.math.BigDecimal;

public class MunicipalityResponseDto {

    private Integer id;
    private Integer stateId;
    private String name;
    private String inegiCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public MunicipalityResponseDto(
            Integer id,
            Integer stateId,
            String name,
            String inegiCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.id = id;
        this.stateId = stateId;
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

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
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
