package com.itesm.domain.repository;

import com.itesm.domain.models.comparison.TerritoryComparison;

import java.util.List;

public interface ComparisonRepository {
    List<TerritoryComparison> compareStates(Integer periodId, List<Integer> stateIds);
    List<TerritoryComparison> compareStatesByCodes(Integer periodId, List<String> stateCodes);
    List<TerritoryComparison> compareMunicipalities(Integer periodId, List<Integer> municipalityIds);
    List<TerritoryComparison> compareMunicipalitiesByCodes(Integer periodId, List<String> municipalityCodes);
}
