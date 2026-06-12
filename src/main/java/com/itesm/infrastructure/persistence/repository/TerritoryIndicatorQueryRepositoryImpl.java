package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicator.TerritoryIndicatorValueDto;
import com.itesm.domain.repository.TerritoryIndicatorQueryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TerritoryIndicatorQueryRepositoryImpl implements TerritoryIndicatorQueryRepository {

    private static final String STATE_LEVEL = "state";
    private static final String MUNICIPALITY_LEVEL = "municipality";
    private static final String ANALYSIS_YEAR_PARAMETER = "analysisYear";
    private static final String INDICATOR_CODE_PARAMETER = "indicatorCode";
    private static final String STATE_ID_PARAMETER = "stateId";
    private static final String MUNICIPALITY_ID_PARAMETER = "municipalityId";

    // Read model central de indicadores: dashboard, mapa, ranking y comparacion
    // deben leer agregados desde territory_indicator_values mediante este repositorio.
    private final EntityManager em;

    public TerritoryIndicatorQueryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Integer> findAnalysisYearByPeriodId(Integer periodId) {
        List<?> rows = em.createNativeQuery("""
                SELECT period_year
                FROM periods
                WHERE id = :periodId
                """)
                .setParameter("periodId", periodId)
                .setMaxResults(1)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(toInteger(rows.get(0)));
    }

    @Override
    public Optional<TerritoryIndicatorValueDto> findOne(
            String territoryLevel,
            Integer stateId,
            Integer municipalityId,
            Integer analysisYear,
            String indicatorCode
    ) {
        StringBuilder sql = new StringBuilder(baseSql());
        sql.append("""
                WHERE tiv.territory_level = :territoryLevel
                  AND tiv.analysis_year = :analysisYear
                  AND i.code = :indicatorCode
                """);

        if (STATE_LEVEL.equals(territoryLevel)) {
            sql.append(" AND tiv.state_id = :stateId ");
        } else if (MUNICIPALITY_LEVEL.equals(territoryLevel)) {
            sql.append(" AND tiv.municipality_id = :municipalityId ");
        } else {
            sql.append(" AND tiv.state_id IS NULL AND tiv.municipality_id IS NULL ");
        }

        Query query = em.createNativeQuery(sql.toString())
                .setParameter("territoryLevel", territoryLevel)
                .setParameter(ANALYSIS_YEAR_PARAMETER, analysisYear)
                .setParameter(INDICATOR_CODE_PARAMETER, indicatorCode);

        if (STATE_LEVEL.equals(territoryLevel)) {
            query.setParameter(STATE_ID_PARAMETER, stateId);
        } else if (MUNICIPALITY_LEVEL.equals(territoryLevel)) {
            query.setParameter(MUNICIPALITY_ID_PARAMETER, municipalityId);
        }

        List<?> rows = query.setMaxResults(1).getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapRow((Object[]) rows.get(0)));
    }

    @Override
    public List<TerritoryIndicatorValueDto> findByTerritoryAndYear(
            String territoryLevel,
            Integer stateId,
            Integer municipalityId,
            Integer analysisYear
    ) {
        StringBuilder sql = new StringBuilder(baseSql());
        sql.append("""
                WHERE tiv.territory_level = :territoryLevel
                  AND tiv.analysis_year = :analysisYear
                """);

        if (STATE_LEVEL.equals(territoryLevel)) {
            sql.append(" AND tiv.state_id = :stateId ");
        } else if (MUNICIPALITY_LEVEL.equals(territoryLevel)) {
            sql.append(" AND tiv.municipality_id = :municipalityId ");
        } else {
            sql.append(" AND tiv.state_id IS NULL AND tiv.municipality_id IS NULL ");
        }

        sql.append(" ORDER BY c.display_order ASC, i.display_order ASC, i.code ASC ");

        Query query = em.createNativeQuery(sql.toString())
                .setParameter("territoryLevel", territoryLevel)
                .setParameter(ANALYSIS_YEAR_PARAMETER, analysisYear);

        if (STATE_LEVEL.equals(territoryLevel)) {
            query.setParameter(STATE_ID_PARAMETER, stateId);
        } else if (MUNICIPALITY_LEVEL.equals(territoryLevel)) {
            query.setParameter(MUNICIPALITY_ID_PARAMETER, municipalityId);
        }

        return mapRows(query.getResultList());
    }

    @Override
    public List<TerritoryIndicatorValueDto> findStateValues(
            String indicatorCode,
            Integer analysisYear
    ) {
        String sql = baseSql() + """
                WHERE tiv.territory_level = 'state'
                  AND tiv.analysis_year = :analysisYear
                  AND i.code = :indicatorCode
                ORDER BY s.name ASC
                """;

        return mapRows(em.createNativeQuery(sql)
                .setParameter(ANALYSIS_YEAR_PARAMETER, analysisYear)
                .setParameter(INDICATOR_CODE_PARAMETER, indicatorCode)
                .getResultList());
    }

    @Override
    public List<TerritoryIndicatorValueDto> findMunicipalityValuesByState(
            String indicatorCode,
            Integer analysisYear,
            String stateCode
    ) {
        String sql = baseSql() + """
                WHERE tiv.territory_level = 'municipality'
                  AND tiv.analysis_year = :analysisYear
                  AND i.code = :indicatorCode
                  AND s.inegi_code = :stateCode
                ORDER BY m.name ASC
                """;

        return mapRows(em.createNativeQuery(sql)
                .setParameter(ANALYSIS_YEAR_PARAMETER, analysisYear)
                .setParameter(INDICATOR_CODE_PARAMETER, indicatorCode)
                .setParameter("stateCode", stateCode)
                .getResultList());
    }

    @Override
    public List<TerritoryIndicatorValueDto> findMapValuesByState(
            String indicatorCode,
            Integer analysisYear,
            String stateCode
    ) {
        if (stateCode == null || stateCode.isBlank()) {
            return findStateValues(indicatorCode, analysisYear);
        }

        return findMunicipalityValuesByState(indicatorCode, analysisYear, stateCode.trim());
    }

    private String baseSql() {
        // Mantener aqui los joins comunes reduce el riesgo de que endpoints similares
        // devuelvan metadata distinta para el mismo indicador.
        return """
                SELECT
                    tiv.id,
                    tiv.territory_level,
                    s.id AS state_id,
                    s.inegi_code AS state_code,
                    s.name AS state_name,
                    m.id AS municipality_id,
                    m.inegi_code AS municipality_code,
                    m.name AS municipality_name,
                    i.code AS indicator_code,
                    i.name AS indicator_name,
                    c.code AS category_code,
                    c.name AS category_name,
                    tiv.value,
                    tiv.analysis_year,
                    tiv.source_year,
                    i.unit,
                    i.value_type,
                    ds.code AS data_source_code,
                    ds.name AS data_source_name,
                    ds.institution AS data_source_institution,
                    ds.official_url AS data_source_official_url,
                    tiv.availability_status,
                    tiv.methodology_note,
                    tiv.source_file
                FROM territory_indicator_values tiv
                JOIN indicators i ON i.id = tiv.indicator_id
                JOIN indicator_categories c ON c.id = i.category_id
                JOIN data_sources ds ON ds.id = tiv.data_source_id
                LEFT JOIN municipalities m ON m.id = tiv.municipality_id
                LEFT JOIN states s ON s.id = COALESCE(tiv.state_id, m.state_id)
                """;
    }

    private List<TerritoryIndicatorValueDto> mapRows(List<?> rows) {
        return rows.stream()
                .map(row -> mapRow((Object[]) row))
                .toList();
    }

    private TerritoryIndicatorValueDto mapRow(Object[] row) {
        return new TerritoryIndicatorValueDto(
                toLongNullable(row[0]),
                toString(row[1]),
                toInteger(row[2]),
                toString(row[3]),
                toString(row[4]),
                toInteger(row[5]),
                toString(row[6]),
                toString(row[7]),
                toString(row[8]),
                toString(row[9]),
                toString(row[10]),
                toString(row[11]),
                toBigDecimal(row[12]),
                toInteger(row[13]),
                toInteger(row[14]),
                toString(row[15]),
                toString(row[16]),
                toString(row[17]),
                toString(row[18]),
                toString(row[19]),
                toString(row[20]),
                toString(row[21]),
                toString(row[22]),
                toString(row[23])
        );
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
        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long toLongNullable(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof BigInteger integer) {
            return new BigDecimal(integer);
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }
}
