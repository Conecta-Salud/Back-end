package com.itesm.application.dto.location;

public class LocationSearchResultDto {

    private Integer id;
    private String code;
    private String name;
    private String type;
    private Integer stateId;
    private String stateCode;
    private String stateName;
    private String displayName;

    public LocationSearchResultDto(
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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getStateId() {
        return stateId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public String getDisplayName() {
        return displayName;
    }
}