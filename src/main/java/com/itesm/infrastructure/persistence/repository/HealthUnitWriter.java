package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HealthUnitWriter {

    private final EntityManager em;

    public HealthUnitWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void markInactiveBySourceYear(Short sourceYear) {
        if (sourceYear == null) {
            return;
        }

        em.createNativeQuery("""
                        UPDATE health_units
                        SET is_active = 0
                        WHERE source_year = :sourceYear
                        """)
                .setParameter("sourceYear", sourceYear)
                .executeUpdate();
    }

    @Transactional
    public int upsert(List<HealthUnitWriteDraft> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        for (HealthUnitWriteDraft value : values) {
            em.createNativeQuery("""
                            INSERT INTO health_units (
                                clues,
                                name,
                                municipality_id,
                                institution_id,
                                establishment_type_id,
                                medical_unit_type_id,
                                care_level,
                                source_year,
                                operation_status,
                                locality_name,
                                address,
                                latitude,
                                longitude,
                                is_active
                            )
                            VALUES (
                                :clues,
                                :name,
                                :municipalityId,
                                :institutionId,
                                :establishmentTypeId,
                                :medicalUnitTypeId,
                                :careLevel,
                                :sourceYear,
                                :operationStatus,
                                :localityName,
                                NULL,
                                :latitude,
                                :longitude,
                                :active
                            )
                            ON DUPLICATE KEY UPDATE
                                name = VALUES(name),
                                municipality_id = VALUES(municipality_id),
                                institution_id = VALUES(institution_id),
                                establishment_type_id = VALUES(establishment_type_id),
                                medical_unit_type_id = VALUES(medical_unit_type_id),
                                care_level = VALUES(care_level),
                                source_year = VALUES(source_year),
                                operation_status = VALUES(operation_status),
                                locality_name = VALUES(locality_name),
                                address = VALUES(address),
                                latitude = VALUES(latitude),
                                longitude = VALUES(longitude),
                                is_active = VALUES(is_active)
                            """)
                    .setParameter("clues", value.clues())
                    .setParameter("name", value.name())
                    .setParameter("municipalityId", value.municipalityId())
                    .setParameter("institutionId", value.institutionId())
                    .setParameter("establishmentTypeId", value.establishmentTypeId())
                    .setParameter("medicalUnitTypeId", value.medicalUnitTypeId())
                    .setParameter("careLevel", value.careLevel())
                    .setParameter("sourceYear", value.sourceYear())
                    .setParameter("operationStatus", value.operationStatus())
                    .setParameter("localityName", value.localityName())
                    .setParameter("latitude", value.latitude())
                    .setParameter("longitude", value.longitude())
                    .setParameter("active", value.active())
                    .executeUpdate();
        }

        return values.size();
    }

    public Optional<Integer> findIdByClues(String clues) {
        if (clues == null || clues.isBlank()) {
            return Optional.empty();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM health_units
                        WHERE clues = :clues
                        """)
                .setParameter("clues", clues.trim())
                .setMaxResults(1)
                .getResultList();

        return rows.stream().findFirst().map(this::toInteger);
    }

    @Transactional
    public Integer ensureMinimal(HealthUnitWriteDraft value) {
        Optional<Integer> existing = findIdByClues(value.clues());
        if (existing.isPresent()) {
            return existing.get();
        }

        em.createNativeQuery("""
                        INSERT INTO health_units (
                            clues,
                            name,
                            municipality_id,
                            institution_id,
                            establishment_type_id,
                            medical_unit_type_id,
                            care_level,
                            source_year,
                            operation_status,
                            locality_name,
                            address,
                            latitude,
                            longitude,
                            is_active
                        )
                        VALUES (
                            :clues,
                            :name,
                            :municipalityId,
                            :institutionId,
                            :establishmentTypeId,
                            :medicalUnitTypeId,
                            :careLevel,
                            :sourceYear,
                            :operationStatus,
                            :localityName,
                            NULL,
                            :latitude,
                            :longitude,
                            :active
                        )
                        """)
                .setParameter("clues", value.clues())
                .setParameter("name", value.name())
                .setParameter("municipalityId", value.municipalityId())
                .setParameter("institutionId", value.institutionId())
                .setParameter("establishmentTypeId", value.establishmentTypeId())
                .setParameter("medicalUnitTypeId", value.medicalUnitTypeId())
                .setParameter("careLevel", value.careLevel())
                .setParameter("sourceYear", value.sourceYear())
                .setParameter("operationStatus", value.operationStatus())
                .setParameter("localityName", value.localityName())
                .setParameter("latitude", value.latitude())
                .setParameter("longitude", value.longitude())
                .setParameter("active", value.active())
                .executeUpdate();

        return findIdByClues(value.clues())
                .orElseThrow(() -> new jakarta.ws.rs.NotFoundException("UNKNOWN_HEALTH_UNIT: Health unit was not found after insert"));
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(value.toString());
    }

    public record HealthUnitWriteDraft(
            String clues,
            String name,
            Integer municipalityId,
            Integer institutionId,
            Integer establishmentTypeId,
            Integer medicalUnitTypeId,
            String careLevel,
            Short sourceYear,
            String operationStatus,
            String localityName,
            BigDecimal latitude,
            BigDecimal longitude,
            boolean active
    ) {
    }
}
