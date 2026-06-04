package com.itesm.infrastructure.persistence.repository;

import com.itesm.application.dto.availability.DataAvailabilityItemDto;
import com.itesm.domain.repository.DataAvailabilityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

@ApplicationScoped
public class DataAvailabilityRepositoryImpl implements DataAvailabilityRepository {

    private final EntityManager em;

    public DataAvailabilityRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Integer> findAvailableAnalysisYears() {
        List<?> rows = em.createNativeQuery("""
                SELECT DISTINCT analysis_year
                FROM data_availability
                WHERE analysis_year IS NOT NULL
                ORDER BY analysis_year DESC
                """)
                .getResultList();

        return rows.stream()
                .map(this::toInteger)
                .toList();
    }

    @Override
    public List<DataAvailabilityItemDto> findAvailability(
            String territoryLevel,
            Integer analysisYear,
            String categoryCode
    ) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    c.code AS category_code,
                    c.name AS category_name,
                    i.code AS indicator_code,
                    i.name AS indicator_name,
                    da.territory_level,
                    da.analysis_year,
                    da.source_year,
                    da.is_available,
                    da.availability_status,
                    da.note
                FROM data_availability da
                JOIN indicator_categories c ON c.id = da.category_id
                LEFT JOIN indicators i ON i.id = da.indicator_id
                WHERE 1 = 1
                """);

        if (hasText(territoryLevel)) {
            sql.append(" AND da.territory_level = :territoryLevel ");
        }

        if (analysisYear != null) {
            sql.append(" AND da.analysis_year = :analysisYear ");
        }

        if (hasText(categoryCode)) {
            sql.append(" AND c.code = :categoryCode ");
        }

        sql.append("""
                ORDER BY
                    c.display_order ASC,
                    i.display_order ASC,
                    da.territory_level ASC,
                    da.analysis_year ASC
                """);

        Query query = em.createNativeQuery(sql.toString());

        if (hasText(territoryLevel)) {
            query.setParameter("territoryLevel", territoryLevel.trim());
        }

        if (analysisYear != null) {
            query.setParameter("analysisYear", analysisYear);
        }

        if (hasText(categoryCode)) {
            query.setParameter("categoryCode", categoryCode.trim());
        }

        List<?> rows = query.getResultList();

        return rows.stream()
                .map(row -> toItem((Object[]) row))
                .toList();
    }

    private DataAvailabilityItemDto toItem(Object[] row) {
        return new DataAvailabilityItemDto(
                toString(row[0]),
                toString(row[1]),
                toString(row[2]),
                toString(row[3]),
                toString(row[4]),
                toInteger(row[5]),
                toInteger(row[6]),
                toBoolean(row[7]),
                toString(row[8]),
                toString(row[9])
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String toString(Object value) {
        return value == null ? null : value.toString();
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
