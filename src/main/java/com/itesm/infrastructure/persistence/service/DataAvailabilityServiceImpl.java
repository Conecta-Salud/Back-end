package com.itesm.infrastructure.persistence.service;

import com.itesm.domain.models.availability.DataAvailabilityInfo;
import com.itesm.domain.service.DataAvailabilityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DataAvailabilityServiceImpl implements DataAvailabilityService {

    private final EntityManager em;

    public DataAvailabilityServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean isIndicatorAvailable(
            String indicatorCode,
            String territoryLevel,
            Integer analysisYear
    ) {
        return findAvailability(indicatorCode, territoryLevel, analysisYear)
                .map(DataAvailabilityInfo::isAvailable)
                .orElse(false);
    }

    @Override
    public Optional<String> findAvailabilityNote(
            String indicatorCode,
            String territoryLevel,
            Integer analysisYear
    ) {
        return findAvailability(indicatorCode, territoryLevel, analysisYear)
                .map(DataAvailabilityInfo::getNote)
                .filter(note -> note != null && !note.isBlank());
    }

    @Override
    public Optional<DataAvailabilityInfo> findAvailability(
            String indicatorCode,
            String territoryLevel,
            Integer analysisYear
    ) {
        List<?> rows = em.createNativeQuery("""
                SELECT
                    da.is_available,
                    da.availability_status,
                    da.note
                FROM data_availability da
                JOIN indicators i ON i.id = da.indicator_id
                WHERE i.code = :indicatorCode
                  AND da.territory_level = :territoryLevel
                  AND da.analysis_year = :analysisYear
                ORDER BY da.source_year DESC
                """)
                .setParameter("indicatorCode", indicatorCode)
                .setParameter("territoryLevel", territoryLevel)
                .setParameter("analysisYear", analysisYear)
                .setMaxResults(1)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = (Object[]) rows.get(0);
        boolean available = toBoolean(row[0]) && isAvailableStatus(row[1]);

        return Optional.of(new DataAvailabilityInfo(
                available,
                row[1] == null ? null : row[1].toString(),
                row[2] == null ? null : row[2].toString()
        ));
    }

    private boolean isAvailableStatus(Object value) {
        if (value == null) {
            return false;
        }

        String status = value.toString();
        return !"not_available".equals(status) && !"not_applicable".equals(status);
    }

    private boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }
}
