package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.upload.CatalogWriteResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MedicalUnitTypeCatalogWriter {

    private final EntityManager em;

    public MedicalUnitTypeCatalogWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public CatalogWriteResult ensure(String name) {
        String safeName = requireText(name, "medicalUnitTypeName");
        Optional<Integer> existing = findIdByName(safeName);

        if (existing.isPresent()) {
            return new CatalogWriteResult(existing.get(), false);
        }

        em.createNativeQuery("""
                        INSERT INTO medical_unit_types (name)
                        VALUES (:name)
                        """)
                .setParameter("name", safeName)
                .executeUpdate();

        Integer id = findIdByName(safeName)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_MEDICAL_UNIT_TYPE: no se encontró el tipo de unidad médica después de insertarlo"));

        return new CatalogWriteResult(id, true);
    }

    private Optional<Integer> findIdByName(String name) {
        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM medical_unit_types
                        WHERE name = :name
                        """)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return rows.stream().findFirst().map(this::toInteger);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + fieldName + " es obligatorio");
        }

        return value.trim();
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
