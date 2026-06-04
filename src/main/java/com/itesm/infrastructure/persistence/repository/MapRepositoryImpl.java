package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicator.TerritoryIndicatorValueDto;
import com.itesm.domain.models.map.MapIndicator;
import com.itesm.domain.models.map.MapIndicatorType;
import com.itesm.domain.repository.MapRepository;
import com.itesm.domain.repository.TerritoryIndicatorQueryRepository;
import com.itesm.domain.service.DataAvailabilityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MapRepositoryImpl implements MapRepository {

    private final EntityManager em;
    private final TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository;
    private final DataAvailabilityService dataAvailabilityService;

    public MapRepositoryImpl(
            EntityManager em,
            TerritoryIndicatorQueryRepository territoryIndicatorQueryRepository,
            DataAvailabilityService dataAvailabilityService
    ) {
        this.em = em;
        this.territoryIndicatorQueryRepository = territoryIndicatorQueryRepository;
        this.dataAvailabilityService = dataAvailabilityService;
    }

    @Override
    public boolean existsPeriodByYear(Integer year) {
        Number count = (Number) em.createNativeQuery("""
                        SELECT COUNT(*)
                        FROM periods
                        WHERE period_year = :year
                        """)
                .setParameter("year", year)
                .getSingleResult();

        return count.longValue() > 0;
    }

    @Override
    public boolean existsStateByCode(String stateCode) {
        Number count = (Number) em.createNativeQuery("""
                        SELECT COUNT(*)
                        FROM states
                        WHERE inegi_code = :stateCode
                        """)
                .setParameter("stateCode", stateCode)
                .getSingleResult();

        return count.longValue() > 0;
    }

    @Override
    public List<MapIndicator> findStateIndicators(MapIndicatorType indicatorType, Integer year) {
        String indicatorCode = indicatorType.getIndicatorCode();

        if (!dataAvailabilityService.isIndicatorAvailable(indicatorCode, "state", year)) {
            return List.of();
        }

        return territoryIndicatorQueryRepository.findStateValues(indicatorCode, year)
                .stream()
                .map(value -> mapIndicator(value, indicatorType))
                .toList();
    }

    @Override
    public List<MapIndicator> findMunicipalityIndicators(
            String stateCode,
            MapIndicatorType indicatorType,
            Integer year
    ) {
        String indicatorCode = indicatorType.getIndicatorCode();

        if (!dataAvailabilityService.isIndicatorAvailable(indicatorCode, "municipality", year)) {
            return List.of();
        }

        return territoryIndicatorQueryRepository.findMapValuesByState(indicatorCode, year, stateCode)
                .stream()
                .map(value -> mapIndicator(value, indicatorType))
                .toList();
    }

    private MapIndicator mapIndicator(
            TerritoryIndicatorValueDto value,
            MapIndicatorType indicatorType
    ) {
        return new MapIndicator(
                value.getTerritoryCode(),
                value.getTerritoryName(),
                value.getValue(),
                indicatorType,
                value.getSourceYear(),
                value.getUnit(),
                value.getAvailabilityStatus(),
                value.getMethodologyNote(),
                value.getDataSourceName()
        );
    }
}
