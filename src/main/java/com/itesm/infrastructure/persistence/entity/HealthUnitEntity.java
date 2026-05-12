package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.healthunit.CareLevel;
import jakarta.persistence.*;

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
}
