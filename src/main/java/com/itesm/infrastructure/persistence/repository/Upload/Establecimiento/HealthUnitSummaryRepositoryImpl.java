package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;


import com.itesm.domain.models.healthunit.HealthUnitSummary;
import com.itesm.domain.repository.Upload.Establecimiento.HealthUnitSummaryRepository;
import com.itesm.infrastructure.persistence.entity.EstablishmentTypeEntity;
import com.itesm.infrastructure.persistence.entity.HealthUnitEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.InstitutionEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MedicalUnitTypeEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class HealthUnitSummaryRepositoryImpl implements HealthUnitSummaryRepository, PanacheRepositoryBase<HealthUnitEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void save(List<HealthUnitSummary> healthUnitSummaries) {
        Map<String, MunicipalityEntity> municipalitiesByCode = loadMunicipalitiesByCode();
        Map<String, MunicipalityEntity> municipalitiesByStateAndName = loadMunicipalitiesByStateAndName();
        Map<String, StateEntity> statesByInegiCode = loadStatesByInegiCode();
        Map<String, InstitutionEntity> institutionsByName = loadInstitutionsByName();
        Map<String, EstablishmentTypeEntity> establishmentTypesByName = loadEstablishmentTypesByName();
        Map<String, MedicalUnitTypeEntity> medicalUnitTypesByName = loadMedicalUnitTypesByName();
        Map<String, HealthUnitEntity> healthUnitsByClues = loadHealthUnitsByClues();

        for (HealthUnitSummary healthUnitSummary : healthUnitSummaries) {
            if (healthUnitSummary.getClues() == null || healthUnitSummary.getClues().isBlank()) {
                continue;
            }

            if (healthUnitSummary.getName() == null || healthUnitSummary.getName().isBlank()) {
                continue;
            }

            String clues = healthUnitSummary.getClues().trim();
            HealthUnitEntity existing = healthUnitsByClues.get(clues);
            if (existing != null) {
                applySummary(
                        existing,
                        healthUnitSummary,
                        municipalitiesByCode,
                        municipalitiesByStateAndName,
                        statesByInegiCode,
                        institutionsByName,
                        establishmentTypesByName,
                        medicalUnitTypesByName
                );
                continue;
            }

            HealthUnitEntity entity = new HealthUnitEntity();

            entity.setClues(clues);
            applySummary(
                    entity,
                    healthUnitSummary,
                    municipalitiesByCode,
                    municipalitiesByStateAndName,
                    statesByInegiCode,
                    institutionsByName,
                    establishmentTypesByName,
                    medicalUnitTypesByName
            );

            persist(entity);
            healthUnitsByClues.put(clues, entity);
        }
    }

    private void applySummary(
            HealthUnitEntity entity,
            HealthUnitSummary healthUnitSummary,
            Map<String, MunicipalityEntity> municipalitiesByCode,
            Map<String, MunicipalityEntity> municipalitiesByStateAndName,
            Map<String, StateEntity> statesByInegiCode,
            Map<String, InstitutionEntity> institutionsByName,
            Map<String, EstablishmentTypeEntity> establishmentTypesByName,
            Map<String, MedicalUnitTypeEntity> medicalUnitTypesByName
    ) {
        entity.setName(healthUnitSummary.getName().trim());
        entity.setCareLevel(healthUnitSummary.getCareLevel());
        entity.setMunicipality(findOrCreateMunicipality(
                healthUnitSummary,
                municipalitiesByCode,
                municipalitiesByStateAndName,
                statesByInegiCode
        ));
        entity.setInstitution(findOrCreateInstitution(healthUnitSummary.getInstitutionName(), institutionsByName));
        entity.setEstablishmentType(findOrCreateEstablishmentType(healthUnitSummary.getEstablishmentTypeName(), establishmentTypesByName));
        entity.setMedicalUnitType(findOrCreateMedicalUnitType(healthUnitSummary.getMedicalUnitTypeName(), medicalUnitTypesByName));
    }

    private Map<String, MunicipalityEntity> loadMunicipalitiesByCode() {
        Map<String, MunicipalityEntity> result = new HashMap<>();
        for (MunicipalityEntity municipality : em.createQuery(
                "SELECT m FROM MunicipalityEntity m",
                MunicipalityEntity.class
        ).getResultList()) {
            result.put(municipality.getInegiCode(), municipality);
        }
        return result;
    }

    private Map<String, MunicipalityEntity> loadMunicipalitiesByStateAndName() {
        Map<String, MunicipalityEntity> result = new HashMap<>();
        for (MunicipalityEntity municipality : em.createQuery(
                "SELECT m FROM MunicipalityEntity m JOIN FETCH m.state",
                MunicipalityEntity.class
        ).getResultList()) {
            result.put(municipalityStateNameKey(municipality.getState().getInegiCode(), municipality.getName()), municipality);
        }
        return result;
    }

    private Map<String, StateEntity> loadStatesByInegiCode() {
        Map<String, StateEntity> result = new HashMap<>();
        for (StateEntity state : em.createQuery(
                "SELECT s FROM StateEntity s",
                StateEntity.class
        ).getResultList()) {
            result.put(state.getInegiCode(), state);
        }
        return result;
    }

    private Map<String, InstitutionEntity> loadInstitutionsByName() {
        Map<String, InstitutionEntity> result = new HashMap<>();
        for (InstitutionEntity institution : em.createQuery(
                "SELECT i FROM InstitutionEntity i",
                InstitutionEntity.class
        ).getResultList()) {
            result.put(normalizeNameKey(institution.getName()), institution);
        }
        return result;
    }

    private Map<String, EstablishmentTypeEntity> loadEstablishmentTypesByName() {
        Map<String, EstablishmentTypeEntity> result = new HashMap<>();
        for (EstablishmentTypeEntity establishmentType : em.createQuery(
                "SELECT e FROM EstablishmentTypeEntity e",
                EstablishmentTypeEntity.class
        ).getResultList()) {
            result.put(normalizeNameKey(establishmentType.getName()), establishmentType);
        }
        return result;
    }

    private Map<String, MedicalUnitTypeEntity> loadMedicalUnitTypesByName() {
        Map<String, MedicalUnitTypeEntity> result = new HashMap<>();
        for (MedicalUnitTypeEntity medicalUnitType : em.createQuery(
                "SELECT m FROM MedicalUnitTypeEntity m",
                MedicalUnitTypeEntity.class
        ).getResultList()) {
            result.put(normalizeNameKey(medicalUnitType.getName()), medicalUnitType);
        }
        return result;
    }

    private Map<String, HealthUnitEntity> loadHealthUnitsByClues() {
        Map<String, HealthUnitEntity> result = new HashMap<>();
        for (HealthUnitEntity healthUnit : em.createQuery(
                "SELECT h FROM HealthUnitEntity h",
                HealthUnitEntity.class
        ).getResultList()) {
            result.put(healthUnit.getClues(), healthUnit);
        }
        return result;
    }

    private MunicipalityEntity findOrCreateMunicipality(
            HealthUnitSummary healthUnitSummary,
            Map<String, MunicipalityEntity> municipalitiesByCode,
            Map<String, MunicipalityEntity> municipalitiesByStateAndName,
            Map<String, StateEntity> statesByInegiCode
    ) {
        String municipalityCode = String.format("%05d", healthUnitSummary.getMunicipalityId());
        MunicipalityEntity municipality = municipalitiesByCode.get(municipalityCode);
        if (municipality != null) {
            return municipality;
        }

        String stateCode = String.format("%02d", healthUnitSummary.getStateId());
        String stateNameKey = municipalityStateNameKey(stateCode, healthUnitSummary.getMunicipalityName());
        municipality = municipalitiesByStateAndName.get(stateNameKey);
        if (municipality != null) {
            municipality.setInegiCode(municipalityCode);
            municipalitiesByCode.put(municipalityCode, municipality);
            return municipality;
        }

        StateEntity state = statesByInegiCode.get(stateCode);
        if (state == null) {
            throw new IllegalArgumentException("No existe estado con inegiCode: " + stateCode);
        }

        municipality = new MunicipalityEntity();
        municipality.setName(normalizeRequiredName(healthUnitSummary.getMunicipalityName(), "municipio"));
        municipality.setInegiCode(municipalityCode);
        municipality.setState(state);
        em.persist(municipality);
        municipalitiesByCode.put(municipalityCode, municipality);
        municipalitiesByStateAndName.put(stateNameKey, municipality);
        return municipality;
    }

    private InstitutionEntity findOrCreateInstitution(String name, Map<String, InstitutionEntity> institutionsByName) {
        String normalizedName = normalizeRequiredName(name, "institución");
        String key = normalizeNameKey(normalizedName);
        InstitutionEntity institution = institutionsByName.get(key);
        if (institution != null) {
            return institution;
        }

        institution = new InstitutionEntity();
        institution.setName(normalizedName);
        em.persist(institution);
        institutionsByName.put(key, institution);
        return institution;
    }

    private EstablishmentTypeEntity findOrCreateEstablishmentType(
            String name,
            Map<String, EstablishmentTypeEntity> establishmentTypesByName
    ) {
        String normalizedName = normalizeRequiredName(name, "tipo de establecimiento");
        String key = normalizeNameKey(normalizedName);
        EstablishmentTypeEntity establishmentType = establishmentTypesByName.get(key);
        if (establishmentType != null) {
            return establishmentType;
        }

        establishmentType = new EstablishmentTypeEntity();
        establishmentType.setName(normalizedName);
        em.persist(establishmentType);
        establishmentTypesByName.put(key, establishmentType);
        return establishmentType;
    }

    private MedicalUnitTypeEntity findOrCreateMedicalUnitType(
            String name,
            Map<String, MedicalUnitTypeEntity> medicalUnitTypesByName
    ) {
        String normalizedName = normalizeRequiredName(name, "tipología médica");
        String key = normalizeNameKey(normalizedName);
        MedicalUnitTypeEntity medicalUnitType = medicalUnitTypesByName.get(key);
        if (medicalUnitType != null) {
            return medicalUnitType;
        }

        medicalUnitType = new MedicalUnitTypeEntity();
        medicalUnitType.setName(normalizedName);
        em.persist(medicalUnitType);
        medicalUnitTypesByName.put(key, medicalUnitType);
        return medicalUnitType;
    }

    private String normalizeRequiredName(String value, String fieldName) {
        if (value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException("El valor de " + fieldName + " está vacío.");
        }

        return value.trim();
    }

    private String normalizeNameKey(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized.toUpperCase(Locale.ROOT);
    }

    private String municipalityStateNameKey(String stateCode, String municipalityName) {
        return stateCode + "|" + normalizeNameKey(municipalityName);
    }
}
