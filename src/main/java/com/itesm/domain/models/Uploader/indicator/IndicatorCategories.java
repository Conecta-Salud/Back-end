package com.itesm.domain.models.Uploader.indicator;

public class IndicatorCategories {
    private Integer id;

    private String code;
    private String name;
    private String description;

    private Integer displayOrder;
    private Boolean isActive;

    public IndicatorCategories() {}

    public IndicatorCategories(Boolean isActive, Integer displayOrder, String description, String name, String code, Integer id) {
        this.isActive = isActive;
        this.displayOrder = displayOrder;
        this.description = description;
        this.name = name;
        this.code = code;
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
