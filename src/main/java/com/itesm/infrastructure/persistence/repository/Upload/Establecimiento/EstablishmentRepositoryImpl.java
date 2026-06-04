package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.Establishment;
import com.itesm.domain.repository.Upload.Establecimiento.EstablishmentRepository;
import com.itesm.infrastructure.persistence.entity.EstablishmentTypeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class EstablishmentRepositoryImpl implements EstablishmentRepository, PanacheRepositoryBase<EstablishmentTypeEntity, Integer> {

    @Override
    @Transactional
    public void save(List<Establishment> establishments) {
        List<EstablishmentTypeEntity> existingTypes = listAll();

        for (Establishment establishment : establishments) {
            if (establishment.getName() == null || establishment.getName().isBlank()) {
                continue;
            }

            String name = establishment.getName().trim();
            EstablishmentTypeEntity existing = findByNormalizedName(existingTypes, name);
            if (existing != null) {
                continue;
            }

            EstablishmentTypeEntity entity = new EstablishmentTypeEntity();
            entity.setName(name);

            persist(entity);
            existingTypes.add(entity);
        }
    }

    private EstablishmentTypeEntity findByNormalizedName(List<EstablishmentTypeEntity> entities, String name) {
        String key = normalizeNameKey(name);
        for (EstablishmentTypeEntity entity : entities) {
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
