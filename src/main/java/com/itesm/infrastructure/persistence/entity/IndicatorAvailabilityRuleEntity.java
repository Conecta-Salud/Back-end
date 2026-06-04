package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.catalog.TerritoryLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "indicator_availability_rules")
public class IndicatorAvailabilityRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indicator_id", nullable = false)
    private IndicatorEntity indicator;

    @Enumerated(EnumType.STRING)
    @Column(name = "territory_level", nullable = false)
    private TerritoryLevel territoryLevel;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "availability_note", length = 255)
    private String availabilityNote;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getAvailabilityNote() {
        return availabilityNote;
    }

    public void setAvailabilityNote(String availabilityNote) {
        this.availabilityNote = availabilityNote;
    }
}