package com.itesm.application.dto.availability;

public class DataAvailabilityItemDto {

    private final String categoryCode;
    private final String categoryName;
    private final String indicatorCode;
    private final String indicatorName;
    private final String territoryLevel;
    private final Integer analysisYear;
    private final Integer sourceYear;
    private final boolean available;
    private final String availabilityStatus;
    private final String note;

    public DataAvailabilityItemDto(
            String categoryCode,
            String categoryName,
            String indicatorCode,
            String indicatorName,
            String territoryLevel,
            Integer analysisYear,
            Integer sourceYear,
            boolean available,
            String availabilityStatus,
            String note
    ) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.indicatorCode = indicatorCode;
        this.indicatorName = indicatorName;
        this.territoryLevel = territoryLevel;
        this.analysisYear = analysisYear;
        this.sourceYear = sourceYear;
        this.available = available;
        this.availabilityStatus = availabilityStatus;
        this.note = note;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public String getTerritoryLevel() {
        return territoryLevel;
    }

    public Integer getAnalysisYear() {
        return analysisYear;
    }

    public Integer getSourceYear() {
        return sourceYear;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getNote() {
        return note;
    }
}
