package com.itesm.application.dto.comparison.summary;

public class ComparisonTerritoryDto {

    private Integer id;
    private String code;
    private String name;
    private String parentName;
    private String type;

    public ComparisonTerritoryDto(
            Integer id,
            String code,
            String name,
            String parentName,
            String type
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentName = parentName;
        this.type = type;
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

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
