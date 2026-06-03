package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TerritoryCatalogWriter {

    private final EntityManager em;

    public TerritoryCatalogWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Integer ensureState(String inegiCode, String name) {
        String safeCode = requireText(inegiCode, "inegiCode");
        String safeName = requireText(name, "name");

        if ("00".equals(safeCode)) {
            throw new BadRequestException("INVALID_TERRITORY_CODE: Country code 00 must not be stored as state");
        }

        em.createNativeQuery("""
                        INSERT INTO states (name, inegi_code)
                        VALUES (:name, :inegiCode)
                        ON DUPLICATE KEY UPDATE
                            name = VALUES(name)
                        """)
                .setParameter("name", safeName)
                .setParameter("inegiCode", safeCode)
                .executeUpdate();

        return findStateIdByCode(safeCode)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_TERRITORY: State was not found after upsert"));
    }

    @Transactional
    public MunicipalityCatalogResult ensureMunicipality(
            String stateInegiCode,
            String stateName,
            String municipalityInegiCode,
            String municipalityName
    ) {
        Integer stateId = ensureState(stateInegiCode, stateName);
        String safeMunicipalityCode = requireText(municipalityInegiCode, "municipalityInegiCode");
        String safeMunicipalityName = requireText(municipalityName, "municipalityName");

        em.createNativeQuery("""
                        INSERT INTO municipalities (state_id, name, inegi_code)
                        VALUES (:stateId, :name, :inegiCode)
                        ON DUPLICATE KEY UPDATE
                            name = VALUES(name),
                            state_id = VALUES(state_id)
                        """)
                .setParameter("stateId", stateId)
                .setParameter("name", safeMunicipalityName)
                .setParameter("inegiCode", safeMunicipalityCode)
                .executeUpdate();

        Integer municipalityId = findMunicipalityIdByCode(safeMunicipalityCode)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_TERRITORY: Municipality was not found after upsert"));

        return new MunicipalityCatalogResult(stateId, municipalityId);
    }

    public Optional<String> findStateNameByCode(String inegiCode) {
        if (inegiCode == null || inegiCode.isBlank()) {
            return Optional.empty();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT name
                        FROM states
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode.trim())
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(Object::toString);
    }

    private Optional<Integer> findStateIdByCode(String inegiCode) {
        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM states
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode)
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(this::toInteger);
    }

    private Optional<Integer> findMunicipalityIdByCode(String inegiCode) {
        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM municipalities
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode)
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(this::toInteger);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + fieldName + " is required");
        }

        return value.trim();
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(value.toString());
    }

    public record MunicipalityCatalogResult(
            Integer stateId,
            Integer municipalityId
    ) {
    }
}
