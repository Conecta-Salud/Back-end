package com.itesm.domain.models.location;

public class LocationSearchResult {

    private Integer id;
    private String code;
    private String name;
    private String type;
    private Integer stateId;
    private String stateCode;
    private String stateName;
    private String displayName;

    public LocationSearchResult(
            Integer id,
            String code,
            String name,
            String type,
            Integer stateId,
            String stateCode,
            String stateName,
            String displayName
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.stateId = stateId;
        this.stateCode = stateCode;
        this.stateName = stateName;
        this.displayName = displayName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}