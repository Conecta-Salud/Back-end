package com.itesm.domain.models.municipality;

import java.math.BigDecimal;

public class Municipality {
    private Integer id;
    private Integer stateId;
    private String name;
    private String inegiCode;

    public Municipality(
            Integer id,
            Integer stateId,
            String name,
            String inegiCode
    ) {
        this.id = id;
        this.stateId = stateId;
        this.name = name;
        this.inegiCode = inegiCode;
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
}
