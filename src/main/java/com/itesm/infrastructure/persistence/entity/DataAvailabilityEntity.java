package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.catalog.AvailabilityStatus;
import com.itesm.domain.models.catalog.TerritoryLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "data_availability")
public class DataAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private IndicatorCategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id")
    private IndicatorEntity indicator;

    @Enumerated(EnumType.STRING)
    @Column(name = "territory_level", nullable = false)
    private TerritoryLevel territoryLevel;

    @Column(name = "analysis_year", nullable = false)
    private Short analysisYear;

    @Column(name = "source_year")
    private Short sourceYear;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Column(length = 255)
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IndicatorCategoryEntity getCategory() {
        return category;
    }

    public void setCategory(IndicatorCategoryEntity category) {
        this.category = category;
    }

    public IndicatorEntity getIndicator() {
        return indicator;
    }

    public void setIndicator(IndicatorEntity indicator) {
        this.indicator = indicator;
    }

    public TerritoryLevel getTerritoryLevel() {
        return territoryLevel;
    }

    public void setTerritoryLevel(TerritoryLevel territoryLevel) {
        this.territoryLevel = territoryLevel;
    }

    public Short getAnalysisYear() {
        return analysisYear;
    }

    public void setAnalysisYear(Short analysisYear) {
        this.analysisYear = analysisYear;
    }

    public Short getSourceYear() {
        return sourceYear;
    }

    public void setSourceYear(Short sourceYear) {
        this.sourceYear = sourceYear;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}