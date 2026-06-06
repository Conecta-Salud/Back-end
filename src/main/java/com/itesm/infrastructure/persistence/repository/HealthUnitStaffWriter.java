package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HealthUnitStaffWriter {

    private final EntityManager em;

    public HealthUnitStaffWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public int upsert(List<HealthUnitStaffDraft> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        for (HealthUnitStaffDraft value : values) {
            em.createNativeQuery("""
                            INSERT INTO health_unit_staff (
                                health_unit_id,
                                period_id,
                                total_doctors,
                                total_nurses,
                                data_source_id,
                                source_file
                            )
                            VALUES (
                                :healthUnitId,
                                :periodId,
                                :totalDoctors,
                                :totalNurses,
                                :dataSourceId,
                                :sourceFile
                            )
                            ON DUPLICATE KEY UPDATE
                                total_doctors = VALUES(total_doctors),
                                total_nurses = VALUES(total_nurses),
                                data_source_id = VALUES(data_source_id),
                                source_file = VALUES(source_file)
                            """)
                    .setParameter("healthUnitId", value.healthUnitId())
                    .setParameter("periodId", value.periodId())
                    .setParameter("totalDoctors", value.totalDoctors())
                    .setParameter("totalNurses", value.totalNurses())
                    .setParameter("dataSourceId", value.dataSourceId())
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
                        FROM health_unit_staff
                        WHERE period_id = :periodId
                          AND health_unit_id IN (:healthUnitIds)
                        """)
                .setParameter("periodId", periodId)
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
                        DELETE huss
                        FROM health_unit_staff_specialties huss
                        JOIN health_unit_staff hus ON hus.id = huss.health_unit_staff_id
                        WHERE hus.period_id = :periodId
                          AND hus.data_source_id = :dataSourceId
                        """)
                .setParameter("periodId", periodId)
                .setParameter("dataSourceId", dataSourceId)
                .executeUpdate();

        em.createNativeQuery("""
                        DELETE FROM health_unit_staff
                        WHERE period_id = :periodId
                          AND data_source_id = :dataSourceId
                        """)
                .setParameter("periodId", periodId)
                .setParameter("dataSourceId", dataSourceId)
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

    public record HealthUnitStaffDraft(
            Integer healthUnitId,
            Integer periodId,
            Integer totalDoctors,
            Integer totalNurses,
            Integer dataSourceId,
            String sourceFile
    ) {
    }
}
