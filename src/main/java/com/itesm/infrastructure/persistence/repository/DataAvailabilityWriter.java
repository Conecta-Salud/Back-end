package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.upload.DataAvailabilityWriteDraft;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class DataAvailabilityWriter {

    private final EntityManager em;

    public DataAvailabilityWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void upsert(List<DataAvailabilityWriteDraft> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (DataAvailabilityWriteDraft value : values) {
            String noteValue = value.note() == null ? "NULL" : ":note";
            String sourceYearValue = value.sourceYear() == null ? "NULL" : ":sourceYear";

            var query = em.createNativeQuery("""
                            INSERT INTO data_availability (
                                category_id,
                                indicator_id,
                                territory_level,
                                analysis_year,
                                source_year,
                                is_available,
                                availability_status,
                                note
                            )
                            VALUES (
                                :categoryId,
                                :indicatorId,
                                :territoryLevel,
                                :analysisYear,
                                %s,
                                :available,
                                :availabilityStatus,
                                %s
                            )
                            ON DUPLICATE KEY UPDATE
                                source_year = VALUES(source_year),
                                is_available = VALUES(is_available),
                                availability_status = VALUES(availability_status),
                                note = VALUES(note)
                            """.formatted(sourceYearValue, noteValue))
                    .setParameter("categoryId", value.categoryId())
                    .setParameter("indicatorId", value.indicatorId())
                    .setParameter("territoryLevel", value.territoryLevel())
                    .setParameter("analysisYear", value.analysisYear())
                    .setParameter("available", value.available())
                    .setParameter("availabilityStatus", value.availabilityStatus());

            if (value.sourceYear() != null) {
                query.setParameter("sourceYear", value.sourceYear());
            }

            if (value.note() != null) {
                query.setParameter("note", value.note());
            }

            query.executeUpdate();
        }
    }

    @Transactional
    public void deleteByIndicatorIdsAndAnalysisYear(Set<Integer> indicatorIds, Short analysisYear) {
        if (indicatorIds == null || indicatorIds.isEmpty() || analysisYear == null) {
            return;
        }

        em.createNativeQuery("""
                        DELETE FROM data_availability
                        WHERE indicator_id IN (:indicatorIds)
                          AND analysis_year = :analysisYear
                        """)
                .setParameter("indicatorIds", indicatorIds)
                .setParameter("analysisYear", analysisYear)
                .executeUpdate();
    }
}
