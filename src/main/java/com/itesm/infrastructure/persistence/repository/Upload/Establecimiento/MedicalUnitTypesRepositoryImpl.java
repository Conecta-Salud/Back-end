package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.MedicalUnitTypes;
import com.itesm.domain.repository.Upload.Establecimiento.MedicalUnitTypesRepository;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MedicalUnitTypeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;


@ApplicationScoped
public class MedicalUnitTypesRepositoryImpl implements MedicalUnitTypesRepository, PanacheRepositoryBase<MedicalUnitTypeEntity, Integer> {

    @Override
    @Transactional
    public void save(List<MedicalUnitTypes> medicalUnitTypes) {
        List<MedicalUnitTypeEntity> existingTypes = listAll();

        for (MedicalUnitTypes medicalUnitType : medicalUnitTypes) {
            if (medicalUnitType.getName() == null || medicalUnitType.getName().isBlank()) {
                continue;
            }

            String name = medicalUnitType.getName().trim();
            MedicalUnitTypeEntity existing = findByNormalizedName(existingTypes, name);
            if (existing != null) {
                continue;
            }

            MedicalUnitTypeEntity entity = new MedicalUnitTypeEntity();
            entity.setName(name);

            persist(entity);
            existingTypes.add(entity);
        }
    }

    private MedicalUnitTypeEntity findByNormalizedName(List<MedicalUnitTypeEntity> entities, String name) {
        String key = normalizeNameKey(name);
        for (MedicalUnitTypeEntity entity : entities) {
            if (normalizeNameKey(entity.getName()).equals(key)) {
                return entity;
            }
        }
        return null;
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
}
