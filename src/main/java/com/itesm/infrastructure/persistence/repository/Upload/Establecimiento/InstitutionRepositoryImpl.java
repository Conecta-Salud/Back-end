package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.Institution;
import com.itesm.domain.repository.Upload.Establecimiento.InstitutionRepository;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.InstitutionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;



@ApplicationScoped
public class InstitutionRepositoryImpl implements InstitutionRepository, PanacheRepositoryBase<InstitutionEntity, Integer> {

    @Override
    @Transactional
    public void save(List<Institution> institutions) {
        List<InstitutionEntity> existingInstitutions = listAll();

        for (Institution institution : institutions) {
            if (institution.getName() == null || institution.getName().isBlank()) {
                continue;
            }

            String name = institution.getName().trim();
            InstitutionEntity existing = findByNormalizedName(existingInstitutions, name);
            if (existing != null) {
                continue;
            }

            InstitutionEntity entity = new InstitutionEntity();
            entity.setName(name);

            persist(entity);
            existingInstitutions.add(entity);
        }
    }

    private InstitutionEntity findByNormalizedName(List<InstitutionEntity> entities, String name) {
        String key = normalizeNameKey(name);
        for (InstitutionEntity entity : entities) {
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
