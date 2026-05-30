package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.location.LocationSearchResult;
import com.itesm.domain.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LocationRepositoryImpl implements LocationRepository {

    private static final String SEARCH_LOCATIONS_SQL = """
        SELECT
            result.id,
            result.code,
            result.name,
            result.type,
            result.state_id,
            result.state_code,
            result.state_name,
            result.display_name
        FROM (
            SELECT
                s.id AS id,
                s.inegi_code AS code,
                s.name AS name,
                'state' AS type,
                NULL AS state_id,
                NULL AS state_code,
                NULL AS state_name,
                s.name AS display_name,
                CASE
                    WHEN s.name = :exactTerm THEN 1
                    WHEN s.name LIKE :startsTerm THEN 2
                    ELSE 3
                END AS relevance,
                1 AS type_order
            FROM states s
            WHERE s.name LIKE :containsTerm

            UNION ALL

            SELECT
                m.id AS id,
                m.inegi_code AS code,
                m.name AS name,
                'municipality' AS type,
                s.id AS state_id,
                s.inegi_code AS state_code,
                s.name AS state_name,
                CONCAT(m.name, ' (', s.name, ')') AS display_name,
                CASE
                    WHEN m.name = :exactTerm THEN 1
                    WHEN m.name LIKE :startsTerm THEN 2
                    ELSE 3
                END AS relevance,
                2 AS type_order
            FROM municipalities m
            INNER JOIN states s ON s.id = m.state_id
            WHERE m.name LIKE :containsTerm
        ) result
        ORDER BY
            result.relevance ASC,
            result.type_order ASC,
            CHAR_LENGTH(result.name) ASC,
            result.name ASC
        """;

    private final EntityManager em;

    public LocationRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<LocationSearchResult> searchLocations(String query, int limit) {
        String exactTerm = query.trim();
        String startsTerm = exactTerm + "%";
        String containsTerm = "%" + exactTerm + "%";

        Query nativeQuery = em.createNativeQuery(SEARCH_LOCATIONS_SQL)
                .setParameter("exactTerm", exactTerm)
                .setParameter("startsTerm", startsTerm)
                .setParameter("containsTerm", containsTerm)
                .setMaxResults(limit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = nativeQuery.getResultList();

        return rows.stream()
                .map(this::mapRow)
                .collect(Collectors.toList());
    }

    private LocationSearchResult mapRow(Object[] row) {
        return new LocationSearchResult(
                toInteger(row[0]),
                toStringValue(row[1]),
                toStringValue(row[2]),
                toStringValue(row[3]),
                toInteger(row[4]),
                toStringValue(row[5]),
                toStringValue(row[6]),
                toStringValue(row[7])
        );
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer integerValue) {
            return integerValue;
        }

        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }

        return Integer.valueOf(value.toString());
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }
}