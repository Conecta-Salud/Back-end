package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HealthUnitInfrastructureWriter {

    private static final String PERIOD_ID_PARAMETER = "periodId";
    private static final String DATA_SOURCE_ID_PARAMETER = "dataSourceId";

    private final EntityManager em;

    public HealthUnitInfrastructureWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public int upsert(List<HealthUnitInfrastructureDraft> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        for (HealthUnitInfrastructureDraft value : values) {
            em.createNativeQuery("""
                            INSERT INTO health_unit_infrastructure (
                                health_unit_id,
                                period_id,
                                data_source_id,
                                source_file
                            )
                            VALUES (
                                :healthUnitId,
                                :periodId,
                                :dataSourceId,
                                :sourceFile
                            )
                            ON DUPLICATE KEY UPDATE
                                data_source_id = VALUES(data_source_id),
                                source_file = VALUES(source_file)
                            """)
                    .setParameter("healthUnitId", value.healthUnitId())
                    .setParameter(PERIOD_ID_PARAMETER, value.periodId())
                    .setParameter(DATA_SOURCE_ID_PARAMETER, value.dataSourceId())
                    .setParameter("sourceFile", value.sourceFile())
                    .executeUpdate();
        }

        return values.size();
    }

    public Map<Integer, Integer> findIdsByHealthUnitIds(Integer periodId, Set<Integer> healthUnitIds) {
        if (periodId == null || healthUnitIds == null || healthUnitIds.isEmpty()) {
            return Map.of();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT health_unit_id, id
                        FROM health_unit_infrastructure
                        WHERE period_id = :periodId
                          AND health_unit_id IN (:healthUnitIds)
                        """)
                .setParameter(PERIOD_ID_PARAMETER, periodId)
                .setParameter("healthUnitIds", healthUnitIds)
                .getResultList();

        Map<Integer, Integer> result = new HashMap<>();
        for (Object row : rows) {
            Object[] columns = (Object[]) row;
            result.put(toInteger(columns[0]), toInteger(columns[1]));
        }

        return result;
    }

    @Transactional
    public void deleteByPeriodAndDataSource(Integer periodId, Integer dataSourceId) {
        if (periodId == null || dataSourceId == null) {
            return;
        }

        em.createNativeQuery("""
                        DELETE huid
                        FROM health_unit_infrastructure_details huid
                        JOIN health_unit_infrastructure hui ON hui.id = huid.health_unit_infrastructure_id
                        WHERE hui.period_id = :periodId
                          AND hui.data_source_id = :dataSourceId
                        """)
                .setParameter(PERIOD_ID_PARAMETER, periodId)
                .setParameter(DATA_SOURCE_ID_PARAMETER, dataSourceId)
                .executeUpdate();

        em.createNativeQuery("""
                        DELETE FROM health_unit_infrastructure
                        WHERE period_id = :periodId
                          AND data_source_id = :dataSourceId
                        """)
                .setParameter(PERIOD_ID_PARAMETER, periodId)
                .setParameter(DATA_SOURCE_ID_PARAMETER, dataSourceId)
                .executeUpdate();
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

    public record HealthUnitInfrastructureDraft(
            Integer healthUnitId,
            Integer periodId,
            Integer dataSourceId,
            String sourceFile
    ) {
    }
}
