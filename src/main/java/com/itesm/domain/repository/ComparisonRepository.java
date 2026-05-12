package com.itesm.domain.repository;

import com.itesm.domain.models.comparison.TerritoryComparison;

import java.util.List;

public interface ComparisonRepository {
    List<TerritoryComparison> compareStates(Integer periodId, List<Integer> stateIds);
    List<TerritoryComparison> compareMunicipalities(Integer periodId, List<Integer> municipalityIds);
}
