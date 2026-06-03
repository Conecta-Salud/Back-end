package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.upload.DataAvailabilityWriteDraft;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

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
                                :sourceYear,
                                :available,
                                :availabilityStatus,
                                %s
                            )
                            ON DUPLICATE KEY UPDATE
                                source_year = VALUES(source_year),
                                is_available = VALUES(is_available),
                                availability_status = VALUES(availability_status),
                                note = VALUES(note)
                            """.formatted(noteValue))
                    .setParameter("categoryId", value.categoryId())
                    .setParameter("indicatorId", value.indicatorId())
                    .setParameter("territoryLevel", value.territoryLevel())
                    .setParameter("analysisYear", value.analysisYear())
                    .setParameter("sourceYear", value.sourceYear())
                    .setParameter("available", value.available())
                    .setParameter("availabilityStatus", value.availabilityStatus());

            if (value.note() != null) {
                query.setParameter("note", value.note());
            }

            query.executeUpdate();
        }
    }
}
