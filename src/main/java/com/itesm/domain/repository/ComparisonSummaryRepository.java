package com.itesm.domain.repository;

import com.itesm.domain.models.comparison.summary.ComparisonRawItem;

import java.util.List;

public interface ComparisonSummaryRepository {

    boolean existsPeriodById(Integer periodId);
    List<ComparisonRawItem> findStateComparisonItemsByCodes(Integer periodId, List<String> stateCodes);
    List<ComparisonRawItem> findMunicipalityComparisonItemsByCodes(Integer periodId, List<String> municipalityCodes);
}
