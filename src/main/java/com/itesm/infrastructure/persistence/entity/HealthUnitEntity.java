package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.healthunit.CareLevel;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "health_units")
@NamedEntityGraph(
        name = "HealthUnit.summary",
        attributeNodes = {
                @NamedAttributeNode(value = "municipality", subgraph = "municipality.state"),
                @NamedAttributeNode("institution"),
                @NamedAttributeNode("establishmentType"),
                @NamedAttributeNode("medicalUnitType")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "municipality.state",
                        attributeNodes = {
                                @NamedAttributeNode("state")
                        }
                )
        }
)
public class HealthUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String clues;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private MunicipalityEntity municipality;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    private InstitutionEntity institution;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "establishment_type_id", nullable = false)
    private EstablishmentTypeEntity establishmentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_unit_type_id", nullable = false)
    private MedicalUnitTypeEntity medicalUnitType;

    @Enumerated(EnumType.STRING)
    @Column(name = "care_level", nullable = false)
    private CareLevel careLevel;

    @Column(name = "source_year")
    private Short sourceYear;

    @Column(name = "operation_status", length = 100)
    private String operationStatus;

    @Column(name = "locality_name", length = 180)
    private String localityName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClues() {
        return clues;
    }

    public void setClues(String clues) {
        this.clues = clues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MunicipalityEntity getMunicipality() {
        return municipality;
    }

    public void setMunicipality(MunicipalityEntity municipality) {
        this.municipality = municipality;
    }

    public InstitutionEntity getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionEntity institution) {
        this.institution = institution;
    }

    public EstablishmentTypeEntity getEstablishmentType() {
        return establishmentType;
    }

    public void setEstablishmentType(EstablishmentTypeEntity establishmentType) {
        this.establishmentType = establishmentType;
    }

    public MedicalUnitTypeEntity getMedicalUnitType() {
        return medicalUnitType;
    }

    public void setMedicalUnitType(MedicalUnitTypeEntity medicalUnitType) {
        this.medicalUnitType = medicalUnitType;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }

    public Short getSourceYear() {
        return sourceYear;
    }

    public void setSourceYear(Short sourceYear) {
        this.sourceYear = sourceYear;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
