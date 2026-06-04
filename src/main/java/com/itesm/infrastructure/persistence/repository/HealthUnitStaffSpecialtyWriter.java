package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HealthUnitStaffSpecialtyWriter {

    private final EntityManager em;

    public HealthUnitStaffSpecialtyWriter(EntityManager em) {
        this.em = em;
    }

    public Map<String, Integer> findSpecialtyIdsByCode(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Map.of();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT code, id
                        FROM specialties
                        WHERE code IN (:codes)
                        """)
                .setParameter("codes", codes)
                .getResultList();

        Map<String, Integer> result = new HashMap<>();
        for (Object row : rows) {
            Object[] columns = (Object[]) row;
            result.put(columns[0].toString(), toInteger(columns[1]));
        }

        return result;
    }

    @Transactional
    public int upsert(List<HealthUnitStaffSpecialtyDraft> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        for (HealthUnitStaffSpecialtyDraft value : values) {
            em.createNativeQuery("""
                            INSERT INTO health_unit_staff_specialties (
                                health_unit_staff_id,
                                specialty_id,
                                quantity
                            )
                            VALUES (
                                :healthUnitStaffId,
                                :specialtyId,
                                :quantity
                            )
                            ON DUPLICATE KEY UPDATE
                                quantity = VALUES(quantity)
                            """)
                    .setParameter("healthUnitStaffId", value.healthUnitStaffId())
                    .setParameter("specialtyId", value.specialtyId())
                    .setParameter("quantity", value.quantity())
                    .executeUpdate();
        }

        return values.size();
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(value.toString());
    }

    public record HealthUnitStaffSpecialtyDraft(
            Integer healthUnitStaffId,
            Integer specialtyId,
            Integer quantity
    ) {
    }
}
