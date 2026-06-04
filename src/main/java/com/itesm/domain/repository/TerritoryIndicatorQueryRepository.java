package com.itesm.domain.repository;

import com.itesm.domain.models.indicator.TerritoryIndicatorValueDto;

import java.util.List;
import java.util.Optional;

public interface TerritoryIndicatorQueryRepository {

    Optional<Integer> findAnalysisYearByPeriodId(Integer periodId);

    Optional<TerritoryIndicatorValueDto> findOne(
            String territoryLevel,
            Integer stateId,
            Integer municipalityId,
            Integer analysisYear,
            String indicatorCode
    );

    List<TerritoryIndicatorValueDto> findByTerritoryAndYear(
            String territoryLevel,
            Integer stateId,
            Integer municipalityId,
            Integer analysisYear
    );

    List<TerritoryIndicatorValueDto> findStateValues(
            String indicatorCode,
            Integer analysisYear
    );

    List<TerritoryIndicatorValueDto> findMunicipalityValuesByState(
            String indicatorCode,
            Integer analysisYear,
            String stateCode
    );

    List<TerritoryIndicatorValueDto> findMapValuesByState(
            String indicatorCode,
            Integer analysisYear,
            String stateCode
    );
}
