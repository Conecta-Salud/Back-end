package com.itesm.domain.models.Uploader.indicator;

public class DataSource {
    private Integer id;

    private String code;
    private String name;
    private String institution;

    private String description;

    private String officialUrl;
    private String refreshFrequency;

    public DataSource() {}

    public DataSource(Integer id, String code, String name, String institution, String description, String officialUrl, String refreshFrequency) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.institution = institution;
        this.description = description;
        this.officialUrl = officialUrl;
        this.refreshFrequency = refreshFrequency;
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

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public void setOfficialUrl(String officialUrl) {
        this.officialUrl = officialUrl;
    }

    public String getRefreshFrequency() {
        return refreshFrequency;
    }

    public void setRefreshFrequency(String refreshFrequency) {
        this.refreshFrequency = refreshFrequency;
    }
}
