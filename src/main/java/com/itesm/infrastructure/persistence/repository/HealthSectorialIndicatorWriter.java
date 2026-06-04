package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.catalog.AvailabilityStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class HealthSectorialIndicatorWriter {

    private static final String METHODOLOGY_NOTE = "Agregado territorial calculado desde BD Abiertos Sectorial DGIS.";

    private final EntityManager em;

    public HealthSectorialIndicatorWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public SectorialIndicatorWriteResult recalculate(
            Short analysisYear,
            Integer periodId,
            Integer dataSourceId,
            String sourceFile,
            Map<String, Integer> indicatorIds,
            Map<String, Integer> infrastructureTypeIds
    ) {
        if (analysisYear == null || periodId == null || dataSourceId == null) {
            return new SectorialIndicatorWriteResult(0, Map.of());
        }

        Set<Integer> sectorialIndicatorIds = Set.of(
                indicatorIds.get("total_doctors"),
                indicatorIds.get("total_nurses"),
                indicatorIds.get("hospital_beds"),
                indicatorIds.get("consulting_rooms"),
                indicatorIds.get("doctors_per_1000"),
                indicatorIds.get("beds_per_1000")
        );
        deleteExistingValues(analysisYear, dataSourceId, sectorialIndicatorIds);

        upsertStaffAggregate(analysisYear, periodId, dataSourceId, sourceFile, indicatorIds.get("total_doctors"), "total_doctors");
        upsertStaffAggregate(analysisYear, periodId, dataSourceId, sourceFile, indicatorIds.get("total_nurses"), "total_nurses");
        upsertInfrastructureAggregate(analysisYear, periodId, dataSourceId, sourceFile, indicatorIds.get("hospital_beds"), infrastructureTypeIds.get("total_camas_hospitalizacion"));
        upsertInfrastructureAggregate(analysisYear, periodId, dataSourceId, sourceFile, indicatorIds.get("consulting_rooms"), infrastructureTypeIds.get("total_consultorios"));
        upsertRate(analysisYear, dataSourceId, sourceFile, indicatorIds.get("doctors_per_1000"), indicatorIds.get("total_doctors"), indicatorIds.get("total_population"));
        upsertRate(analysisYear, dataSourceId, sourceFile, indicatorIds.get("beds_per_1000"), indicatorIds.get("hospital_beds"), indicatorIds.get("total_population"));

        AvailableLevelsResult availableLevels = findAvailableLevels(analysisYear, dataSourceId, sectorialIndicatorIds);
        return new SectorialIndicatorWriteResult(availableLevels.rowCount(), availableLevels.levelsByIndicatorCode());
    }

    private void deleteExistingValues(Short analysisYear, Integer dataSourceId, Set<Integer> indicatorIds) {
        em.createNativeQuery("""
                        DELETE FROM territory_indicator_values
                        WHERE analysis_year = :analysisYear
                          AND data_source_id = :dataSourceId
                          AND indicator_id IN (:indicatorIds)
                        """)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("indicatorIds", indicatorIds)
                .executeUpdate();
    }

    private void upsertStaffAggregate(
            Short analysisYear,
            Integer periodId,
            Integer dataSourceId,
            String sourceFile,
            Integer indicatorId,
            String staffColumn
    ) {
        upsertCountryStaff(analysisYear, periodId, dataSourceId, sourceFile, indicatorId, staffColumn);
        upsertStateStaff(analysisYear, periodId, dataSourceId, sourceFile, indicatorId, staffColumn);
        upsertMunicipalityStaff(analysisYear, periodId, dataSourceId, sourceFile, indicatorId, staffColumn);
    }

    private void upsertCountryStaff(Short analysisYear, Integer periodId, Integer dataSourceId, String sourceFile, Integer indicatorId, String staffColumn) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            'country', NULL, NULL, :indicatorId, COALESCE(SUM(hus.%s), 0),
                            :analysisYear, :analysisYear, :dataSourceId, :sourceFile,
                            :availabilityStatus, :methodologyNote
                        FROM health_unit_staff hus
                        WHERE hus.period_id = :periodId
                          AND hus.data_source_id = :dataSourceId
                        HAVING COUNT(hus.id) > 0
                        ON DUPLICATE KEY UPDATE
                            value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """.formatted(staffColumn))
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("periodId", periodId)
                .executeUpdate();
    }

    private void upsertStateStaff(Short analysisYear, Integer periodId, Integer dataSourceId, String sourceFile, Integer indicatorId, String staffColumn) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            'state', m.state_id, NULL, :indicatorId, COALESCE(SUM(hus.%s), 0),
                            :analysisYear, :analysisYear, :dataSourceId, :sourceFile,
                            :availabilityStatus, :methodologyNote
                        FROM health_unit_staff hus
                        JOIN health_units hu ON hu.id = hus.health_unit_id
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hus.period_id = :periodId
                          AND hus.data_source_id = :dataSourceId
                        GROUP BY m.state_id
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id), value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """.formatted(staffColumn))
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("periodId", periodId)
                .executeUpdate();
    }

    private void upsertMunicipalityStaff(Short analysisYear, Integer periodId, Integer dataSourceId, String sourceFile, Integer indicatorId, String staffColumn) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            'municipality', m.state_id, hu.municipality_id, :indicatorId, COALESCE(SUM(hus.%s), 0),
                            :analysisYear, :analysisYear, :dataSourceId, :sourceFile,
                            :availabilityStatus, :methodologyNote
                        FROM health_unit_staff hus
                        JOIN health_units hu ON hu.id = hus.health_unit_id
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hus.period_id = :periodId
                          AND hus.data_source_id = :dataSourceId
                        GROUP BY m.state_id, hu.municipality_id
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id), municipality_id = VALUES(municipality_id),
                            value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """.formatted(staffColumn))
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("periodId", periodId)
                .executeUpdate();
    }

    private void upsertInfrastructureAggregate(
            Short analysisYear,
            Integer periodId,
            Integer dataSourceId,
            String sourceFile,
            Integer indicatorId,
            Integer infrastructureTypeId
    ) {
        upsertCountryInfrastructure(analysisYear, periodId, dataSourceId, sourceFile, indicatorId, infrastructureTypeId);
        upsertStateInfrastructure(analysisYear, periodId, dataSourceId, sourceFile, indicatorId, infrastructureTypeId);
        upsertMunicipalityInfrastructure(analysisYear, periodId, dataSourceId, sourceFile, indicatorId, infrastructureTypeId);
    }

    private void upsertCountryInfrastructure(Short analysisYear, Integer periodId, Integer dataSourceId, String sourceFile, Integer indicatorId, Integer infrastructureTypeId) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            'country', NULL, NULL, :indicatorId, COALESCE(SUM(huid.quantity), 0),
                            :analysisYear, :analysisYear, :dataSourceId, :sourceFile,
                            :availabilityStatus, :methodologyNote
                        FROM health_unit_infrastructure hui
                        JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                        WHERE hui.period_id = :periodId
                          AND hui.data_source_id = :dataSourceId
                          AND huid.infrastructure_type_id = :infrastructureTypeId
                        HAVING COUNT(hui.id) > 0
                        ON DUPLICATE KEY UPDATE
                            value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("periodId", periodId)
                .setParameter("infrastructureTypeId", infrastructureTypeId)
                .executeUpdate();
    }

    private void upsertStateInfrastructure(Short analysisYear, Integer periodId, Integer dataSourceId, String sourceFile, Integer indicatorId, Integer infrastructureTypeId) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            'state', m.state_id, NULL, :indicatorId, COALESCE(SUM(huid.quantity), 0),
                            :analysisYear, :analysisYear, :dataSourceId, :sourceFile,
                            :availabilityStatus, :methodologyNote
                        FROM health_unit_infrastructure hui
                        JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                        JOIN health_units hu ON hu.id = hui.health_unit_id
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hui.period_id = :periodId
                          AND hui.data_source_id = :dataSourceId
                          AND huid.infrastructure_type_id = :infrastructureTypeId
                        GROUP BY m.state_id
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id), value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("periodId", periodId)
                .setParameter("infrastructureTypeId", infrastructureTypeId)
                .executeUpdate();
    }

    private void upsertMunicipalityInfrastructure(Short analysisYear, Integer periodId, Integer dataSourceId, String sourceFile, Integer indicatorId, Integer infrastructureTypeId) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            'municipality', m.state_id, hu.municipality_id, :indicatorId, COALESCE(SUM(huid.quantity), 0),
                            :analysisYear, :analysisYear, :dataSourceId, :sourceFile,
                            :availabilityStatus, :methodologyNote
                        FROM health_unit_infrastructure hui
                        JOIN health_unit_infrastructure_details huid ON huid.health_unit_infrastructure_id = hui.id
                        JOIN health_units hu ON hu.id = hui.health_unit_id
                        JOIN municipalities m ON m.id = hu.municipality_id
                        WHERE hui.period_id = :periodId
                          AND hui.data_source_id = :dataSourceId
                          AND huid.infrastructure_type_id = :infrastructureTypeId
                        GROUP BY m.state_id, hu.municipality_id
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id), municipality_id = VALUES(municipality_id),
                            value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("periodId", periodId)
                .setParameter("infrastructureTypeId", infrastructureTypeId)
                .executeUpdate();
    }

    private void upsertRate(Short analysisYear, Integer dataSourceId, String sourceFile, Integer rateIndicatorId, Integer numeratorIndicatorId, Integer populationIndicatorId) {
        em.createNativeQuery("""
                        INSERT INTO territory_indicator_values (
                            territory_level, state_id, municipality_id, indicator_id, value,
                            analysis_year, source_year, data_source_id, source_file,
                            availability_status, methodology_note
                        )
                        SELECT
                            n.territory_level,
                            n.state_id,
                            n.municipality_id,
                            :rateIndicatorId,
                            ROUND((n.value / p.value) * 1000, 4),
                            :analysisYear,
                            :analysisYear,
                            :dataSourceId,
                            :sourceFile,
                            :availabilityStatus,
                            :methodologyNote
                        FROM territory_indicator_values n
                        JOIN territory_indicator_values p
                          ON p.territory_level = n.territory_level
                         AND p.territory_key = n.territory_key
                         AND p.indicator_id = :populationIndicatorId
                         AND p.analysis_year = :analysisYear
                         AND p.value IS NOT NULL
                         AND p.value > 0
                        WHERE n.indicator_id = :numeratorIndicatorId
                          AND n.analysis_year = :analysisYear
                          AND n.data_source_id = :dataSourceId
                        ON DUPLICATE KEY UPDATE
                            state_id = VALUES(state_id), municipality_id = VALUES(municipality_id),
                            value = VALUES(value), source_year = VALUES(source_year),
                            data_source_id = VALUES(data_source_id), source_file = VALUES(source_file),
                            availability_status = VALUES(availability_status), methodology_note = VALUES(methodology_note)
                        """)
                .setParameter("rateIndicatorId", rateIndicatorId)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("sourceFile", sourceFile)
                .setParameter("availabilityStatus", AvailabilityStatus.available.name())
                .setParameter("methodologyNote", METHODOLOGY_NOTE)
                .setParameter("populationIndicatorId", populationIndicatorId)
                .setParameter("numeratorIndicatorId", numeratorIndicatorId)
                .executeUpdate();
    }

    private AvailableLevelsResult findAvailableLevels(Short analysisYear, Integer dataSourceId, Set<Integer> indicatorIds) {
        List<?> rows = em.createNativeQuery("""
                        SELECT i.code, tiv.territory_level, COUNT(*)
                        FROM territory_indicator_values tiv
                        JOIN indicators i ON i.id = tiv.indicator_id
                        WHERE tiv.analysis_year = :analysisYear
                          AND tiv.data_source_id = :dataSourceId
                          AND tiv.indicator_id IN (:indicatorIds)
                        GROUP BY i.code, tiv.territory_level
                        """)
                .setParameter("analysisYear", analysisYear)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("indicatorIds", indicatorIds)
                .getResultList();

        int rowCount = 0;
        Map<String, Set<String>> result = new HashMap<>();
        for (Object row : rows) {
            Object[] columns = (Object[]) row;
            result.computeIfAbsent(columns[0].toString(), ignored -> new HashSet<>())
                    .add(columns[1].toString());
            rowCount += ((Number) columns[2]).intValue();
        }

        return new AvailableLevelsResult(rowCount, result);
    }

    public record SectorialIndicatorWriteResult(
            int rowsUpserted,
            Map<String, Set<String>> availableLevelsByIndicatorCode
    ) {
    }

    private record AvailableLevelsResult(
            int rowCount,
            Map<String, Set<String>> levelsByIndicatorCode
    ) {
    }
}
