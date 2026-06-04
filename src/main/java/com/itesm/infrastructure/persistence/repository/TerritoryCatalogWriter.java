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

        Optional<StateCatalogRow> existing = findStateByCode(safeCode);
        if (existing.isPresent()) {
            StateCatalogRow state = existing.get();

            if (!safeName.equals(state.name())) {
                updateStateName(state.id(), safeName);
            }

            return state.id();
        }

        insertState(safeCode, safeName);
        return findStateIdByCode(safeCode)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_TERRITORY: no se encontró el estado después de insertarlo"));
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

        Optional<MunicipalityCatalogRow> existing = findMunicipalityByCode(safeMunicipalityCode);
        if (existing.isPresent()) {
            MunicipalityCatalogRow municipality = existing.get();

            if (!safeMunicipalityName.equals(municipality.name()) || !stateId.equals(municipality.stateId())) {
                updateMunicipality(municipality.id(), stateId, safeMunicipalityName);
            }

            return new MunicipalityCatalogResult(stateId, municipality.id());
        }

        insertMunicipality(stateId, safeMunicipalityCode, safeMunicipalityName);
        Integer municipalityId = findMunicipalityIdByCode(safeMunicipalityCode)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_TERRITORY: no se encontró el municipio después de insertarlo"));

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

    public Optional<Integer> findStateIdByCode(String inegiCode) {
        if (inegiCode == null || inegiCode.isBlank()) {
            return Optional.empty();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM states
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode.trim())
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(this::toInteger);
    }

    public Optional<Integer> findMunicipalityIdByCode(String inegiCode) {
        if (inegiCode == null || inegiCode.isBlank()) {
            return Optional.empty();
        }

        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM municipalities
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode.trim())
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(this::toInteger);
    }

    private Optional<StateCatalogRow> findStateByCode(String inegiCode) {
        List<?> rows = em.createNativeQuery("""
                        SELECT id, name
                        FROM states
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode)
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(row -> {
                    Object[] columns = (Object[]) row;
                    return new StateCatalogRow(toInteger(columns[0]), columns[1].toString());
                });
    }

    private Optional<MunicipalityCatalogRow> findMunicipalityByCode(String inegiCode) {
        List<?> rows = em.createNativeQuery("""
                        SELECT id, state_id, name
                        FROM municipalities
                        WHERE inegi_code = :inegiCode
                        """)
                .setParameter("inegiCode", inegiCode)
                .setMaxResults(1)
                .getResultList();

        return rows.stream()
                .findFirst()
                .map(row -> {
                    Object[] columns = (Object[]) row;
                    return new MunicipalityCatalogRow(
                            toInteger(columns[0]),
                            toInteger(columns[1]),
                            columns[2].toString()
                    );
                });
    }

    private void insertState(String inegiCode, String name) {
        em.createNativeQuery("""
                        INSERT INTO states (name, inegi_code)
                        VALUES (:name, :inegiCode)
                        """)
                .setParameter("name", name)
                .setParameter("inegiCode", inegiCode)
                .executeUpdate();
    }

    private void updateStateName(Integer stateId, String name) {
        em.createNativeQuery("""
                        UPDATE states
                        SET name = :name
                        WHERE id = :stateId
                        """)
                .setParameter("name", name)
                .setParameter("stateId", stateId)
                .executeUpdate();
    }

    private void insertMunicipality(Integer stateId, String inegiCode, String name) {
        em.createNativeQuery("""
                        INSERT INTO municipalities (state_id, name, inegi_code)
                        VALUES (:stateId, :name, :inegiCode)
                        """)
                .setParameter("stateId", stateId)
                .setParameter("name", name)
                .setParameter("inegiCode", inegiCode)
                .executeUpdate();
    }

    private void updateMunicipality(Integer municipalityId, Integer stateId, String name) {
        em.createNativeQuery("""
                        UPDATE municipalities
                        SET state_id = :stateId,
                            name = :name
                        WHERE id = :municipalityId
                        """)
                .setParameter("stateId", stateId)
                .setParameter("name", name)
                .setParameter("municipalityId", municipalityId)
                .executeUpdate();
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + fieldName + " es obligatorio");
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

    private record StateCatalogRow(
            Integer id,
            String name
    ) {
    }

    private record MunicipalityCatalogRow(
            Integer id,
            Integer stateId,
            String name
    ) {
    }
}
