package com.itesm.infrastructure.persistence.entity.Upload.Indicadores;

import com.itesm.domain.models.Uploader.Auxiliar.AvailabilityStatus;
import com.itesm.domain.models.Uploader.Auxiliar.TerritoryLevel;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "territory_indicator_values")
public class TerritoryIndicatorValuesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "territory_level", nullable = false)
    private TerritoryLevel territoryLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private StateEntity state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipality_id")
    private MunicipalityEntity municipality;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indicator_id", nullable = false)
    private IndicatorsEntity indicator;

    @Column(name = "value", precision = 18, scale = 4)
    private BigDecimal value;

    @Column(name = "analysis_year", nullable = false)
    private Short analysisYear;

    @Column(name = "source_year")
    private Short sourceYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "data_source_id", nullable = false)
    private DataSourceEntity dataSource;

    @Column(name = "source_file", length = 255)
    private String sourceFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Column(name = "methodology_note", columnDefinition = "TEXT")
    private String methodologyNote;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TerritoryLevel getTerritoryLevel() {
        return territoryLevel;
    }

    public void setTerritoryLevel(TerritoryLevel territoryLevel) {
        this.territoryLevel = territoryLevel;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public MunicipalityEntity getMunicipality() {
        return municipality;
    }

    public void setMunicipality(MunicipalityEntity municipality) {
        this.municipality = municipality;
    }

    public IndicatorsEntity getIndicator() {
        return indicator;
    }

    public void setIndicator(IndicatorsEntity indicator) {
        this.indicator = indicator;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
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

    public DataSourceEntity getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEntity dataSource) {
        this.dataSource = dataSource;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getMethodologyNote() {
        return methodologyNote;
    }

    public void setMethodologyNote(String methodologyNote) {
        this.methodologyNote = methodologyNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
