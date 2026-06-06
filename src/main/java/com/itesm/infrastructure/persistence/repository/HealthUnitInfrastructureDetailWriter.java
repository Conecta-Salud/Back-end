package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HealthUnitInfrastructureDetailWriter {

    private final EntityManager em;

    public HealthUnitInfrastructureDetailWriter(EntityManager em) {
        this.em = em;
    }

    public Map<String, Integer> findInfrastructureTypeIdsByCode(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Map.of();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT code, id
                        FROM infrastructure_types
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
    public int upsert(List<HealthUnitInfrastructureDetailDraft> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        for (HealthUnitInfrastructureDetailDraft value : values) {
            em.createNativeQuery("""
                            INSERT INTO health_unit_infrastructure_details (
                                health_unit_infrastructure_id,
                                infrastructure_type_id,
                                quantity
                            )
                            VALUES (
                                :healthUnitInfrastructureId,
                                :infrastructureTypeId,
                                :quantity
                            )
                            ON DUPLICATE KEY UPDATE
                                quantity = VALUES(quantity)
                            """)
                    .setParameter("healthUnitInfrastructureId", value.healthUnitInfrastructureId())
                    .setParameter("infrastructureTypeId", value.infrastructureTypeId())
                    .setParameter("quantity", value.quantity())
                    .executeUpdate();
        }

        return values.size();
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

    public record HealthUnitInfrastructureDetailDraft(
            Integer healthUnitInfrastructureId,
            Integer infrastructureTypeId,
            Integer quantity
    ) {
    }
}
