package com.itesm.domain.repository;

import com.itesm.domain.models.map.MapIndicatorType;
import com.itesm.domain.models.map.MapIndicator;

import java.util.List;

public interface MapRepository {
    List<MapIndicator> findStateIndicators(MapIndicatorType indicatorType, Integer year);
    List<MapIndicator> findMunicipalityIndicators(String stateCode, MapIndicatorType indicatorType, Integer year);
    boolean existsPeriodByYear(Integer year);
    boolean existsStateByCode(String stateCode);
}
