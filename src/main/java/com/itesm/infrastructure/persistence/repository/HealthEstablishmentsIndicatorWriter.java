package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.catalog.AvailabilityStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class HealthEstablishmentsIndicatorWriter {

    private static final String METHODOLOGY_NOTE = "Conteo de establecimientos de salud registrados en el catalogo DGIS.";

    private final EntityManager em;

    public HealthEstablishmentsIndicatorWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public int recalculate(
            Short sourceYear,
            Integer indicatorId,
            Integer dataSourceId,
            String sourceFile
    ) {
        if (sourceYear == null || indicatorId == null || dataSourceId == null) {
            return 0;
        }

        int conceptualRows = countConceptualRows(sourceYear);
        upsertCountry(sourceYear, indicatorId, dataSourceId, sourceFile);
        upsertStates(sourceYear, indicatorId, dataSourceId, sourceFile);
        upsertMunicipalities(sourceYear, indicatorId, dataSourceId, sourceFile);
        return conceptualRows;
    }

    private void upsertCountry(Short sourceYear, Integer indicatorId, Integer dataSourceId, String sourceFile) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level,
                            state_id,
                            municipality_id,
                            indicator_id,
                            value,
                            analysis_year,
                            source_year,
                            data_source_id,
                            source_file,
                            availability_status,
                            methodology_note
                        )
                        SELECT
                            'country',
                            NULL,
                            NULL,
                            :indicatorId,
                            COUNT(*),
                            :sourceYear,
                            :sourceYear,
                            :dataSourceId,
                            :sourceFile,
                            :availabilityStatus,
                            :methodologyNote
                        FROM health_units hu
                        WHERE hu.source_year = :sourceYear
                          AND hu.is_active = 1
                        ON DUPLICATE KEY UPDATE
                            value = VALUES(value),
                            source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id),
                            source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status),
                            methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("indicatorId", indicatorId)
                .setParameter("sourceYear", sourceYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .executeUpdate();
    }

    private void upsertStates(Short sourceYear, Integer indicatorId, Integer dataSourceId, String sourceFile) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level,
                            state_id,
                            municipality_id,
                            indicator_id,
                            value,
                            analysis_year,
                            source_year,
                            data_source_id,
                            source_file,
                            availability_status,
                            methodology_note
                        )
                        SELECT
                            'state',
                            m.state_id,
                            NULL,
                            :indicatorId,
                            COUNT(*),
                            :sourceYear,
                            :sourceYear,
                            :dataSourceId,
                            :sourceFile,
                            :availabilityStatus,
                            :methodologyNote
                        FROM health_units hu
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hu.source_year = :sourceYear
                          AND hu.is_active = 1
                        GROUP BY m.state_id
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id),
                            value = VALUES(value),
                            source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id),
                            source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status),
                            methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("indicatorId", indicatorId)
                .setParameter("sourceYear", sourceYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .executeUpdate();
    }

    private void upsertMunicipalities(Short sourceYear, Integer indicatorId, Integer dataSourceId, String sourceFile) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level,
                            state_id,
                            municipality_id,
                            indicator_id,
                            value,
                            analysis_year,
                            source_year,
                            data_source_id,
                            source_file,
                            availability_status,
                            methodology_note
                        )
                        SELECT
                            'municipality',
                            m.state_id,
                            hu.municipality_id,
                            :indicatorId,
                            COUNT(*),
                            :sourceYear,
                            :sourceYear,
                            :dataSourceId,
                            :sourceFile,
                            :availabilityStatus,
                            :methodologyNote
                        FROM health_units hu
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hu.source_year = :sourceYear
                          AND hu.is_active = 1
                        GROUP BY m.state_id, hu.municipality_id
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id),
                            municipality_id = VALUES(municipality_id),
                            value = VALUES(value),
                            source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id),
                            source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status),
                            methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("indicatorId", indicatorId)
                .setParameter("sourceYear", sourceYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .executeUpdate();
    }

    private int countConceptualRows(Short sourceYear) {
        long countryRows = activeUnitCount(sourceYear) > 0 ? 1 : 0;
        long stateRows = distinctCount(sourceYear, "m.state_id");
        long municipalityRows = distinctCount(sourceYear, "hu.municipality_id");

        return Math.toIntExact(countryRows + stateRows + municipalityRows);
    }

    private long activeUnitCount(Short sourceYear) {
        Object count = em.createNativeQuery("""
                        SELECT COUNT(*)
                        FROM health_units
                        WHERE source_year = :sourceYear
                          AND is_active = 1
                        """)
                .setParameter("sourceYear", sourceYear)
                .getSingleResult();

        return toLong(count);
    }

    private long distinctCount(Short sourceYear, String expression) {
        Object count = em.createNativeQuery("""
                        SELECT COUNT(DISTINCT %s)
                        FROM health_units hu
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hu.source_year = :sourceYear
                          AND hu.is_active = 1
                        """.formatted(expression))
                .setParameter("sourceYear", sourceYear)
                .getSingleResult();

        return toLong(count);
    }

    private long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            throw new IllegalArgumentException("Cannot convert null to long");
        }
        String normalizedValue = value.toString().trim();
        try {
            return Long.parseLong(normalizedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Cannot convert value to long: " + normalizedValue, exception);
        }
    }
}
