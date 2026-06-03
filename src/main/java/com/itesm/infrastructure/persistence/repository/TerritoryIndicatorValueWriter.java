package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.upload.TerritoryIndicatorValueWriteDraft;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class TerritoryIndicatorValueWriter {

    private final EntityManager em;

    public TerritoryIndicatorValueWriter(EntityManager em) {
        this.em = em;
    }

    public Map<String, IndicatorMetadata> findIndicatorMetadata(Set<String> indicatorCodes) {
        if (indicatorCodes == null || indicatorCodes.isEmpty()) {
            return Map.of();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT id, code, category_id
                        FROM indicators
                        WHERE code IN (:indicatorCodes)
                          AND is_active = 1
                        """)
                .setParameter("indicatorCodes", indicatorCodes)
                .getResultList();

        Map<String, IndicatorMetadata> result = new HashMap<>();
        for (Object row : rows) {
            Object[] columns = (Object[]) row;
            String code = columns[1].toString();
            result.put(code, new IndicatorMetadata(
                    toInteger(columns[0]),
                    code,
                    toInteger(columns[2])
            ));
        }

        return result;
    }

    public Map<String, Integer> findStateIdsByInegiCode() {
        List<?> rows = em.createNativeQuery("""
                        SELECT id, inegi_code
                        FROM states
                        """)
                .getResultList();

        Map<String, Integer> result = new HashMap<>();
        for (Object row : rows) {
            Object[] columns = (Object[]) row;
            result.put(columns[1].toString(), toInteger(columns[0]));
        }

        return result;
    }

    public Map<String, MunicipalityMetadata> findMunicipalitiesByInegiCode() {
        List<?> rows = em.createNativeQuery("""
                        SELECT m.id, m.inegi_code, s.inegi_code AS state_inegi_code
                        FROM municipalities m
                        JOIN states s ON s.id = m.state_id
                        """)
                .getResultList();

        Map<String, MunicipalityMetadata> result = new HashMap<>();
        for (Object row : rows) {
            Object[] columns = (Object[]) row;
            String municipalityCode = columns[1].toString();
            result.put(municipalityCode, new MunicipalityMetadata(
                    toInteger(columns[0]),
                    municipalityCode,
                    columns[2].toString()
            ));
        }

        return result;
    }

    @Transactional
    public void deleteExistingPopulationValues(Integer dataSourceId, Set<Integer> indicatorIds, List<Short> analysisYears) {
        if (dataSourceId == null || indicatorIds == null || indicatorIds.isEmpty()
                || analysisYears == null || analysisYears.isEmpty()) {
            return;
        }

        em.createNativeQuery("""
                        DELETE FROM territory_indicator_values
                        WHERE data_source_id = :dataSourceId
                          AND indicator_id IN (:indicatorIds)
                          AND analysis_year IN (:analysisYears)
                        """)
                .setParameter("dataSourceId", dataSourceId)
                .setParameter("indicatorIds", indicatorIds)
                .setParameter("analysisYears", analysisYears)
                .executeUpdate();
    }

    @Transactional
    public void upsert(List<TerritoryIndicatorValueWriteDraft> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        for (TerritoryIndicatorValueWriteDraft value : values) {
            String stateValue = value.stateId() == null ? "NULL" : ":stateId";
            String municipalityValue = value.municipalityId() == null ? "NULL" : ":municipalityId";
            String methodologyNoteValue = value.methodologyNote() == null ? "NULL" : ":methodologyNote";

            var query = em.createNativeQuery("""
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
                            VALUES (
                                :territoryLevel,
                                %s,
                                %s,
                                :indicatorId,
                                :indicatorValue,
                                :analysisYear,
                                :sourceYear,
                                :dataSourceId,
                                :sourceFile,
                                :availabilityStatus,
                                %s
                            )
                            ON DUPLICATE KEY UPDATE
                                state_id = VALUES(state_id),
                                municipality_id = VALUES(municipality_id),
                                value = VALUES(value),
                                source_year = VALUES(source_year),
                                data_source_id = VALUES(data_source_id),
                                source_file = VALUES(source_file),
                                availability_status = VALUES(availability_status),
                                methodology_note = VALUES(methodology_note)
                            """.formatted(stateValue, municipalityValue, methodologyNoteValue))
                    .setParameter("territoryLevel", value.territoryLevel())
                    .setParameter("indicatorId", value.indicatorId())
                    .setParameter("indicatorValue", value.value())
                    .setParameter("analysisYear", value.analysisYear())
                    .setParameter("sourceYear", value.sourceYear())
                    .setParameter("dataSourceId", value.dataSourceId())
                    .setParameter("sourceFile", value.sourceFile())
                    .setParameter("availabilityStatus", value.availabilityStatus());

            if (value.stateId() != null) {
                query.setParameter("stateId", value.stateId());
            }

            if (value.municipalityId() != null) {
                query.setParameter("municipalityId", value.municipalityId());
            }

            if (value.methodologyNote() != null) {
                query.setParameter("methodologyNote", value.methodologyNote());
            }

            query.executeUpdate();
        }
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(value.toString());
    }

    public record IndicatorMetadata(Integer id, String code, Integer categoryId) {
    }

    public record MunicipalityMetadata(Integer id, String inegiCode, String stateInegiCode) {
    }
}
