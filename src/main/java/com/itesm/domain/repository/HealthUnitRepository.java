package com.itesm.domain.repository;

import com.itesm.domain.models.healthunit.HealthUnitDetail;
import com.itesm.domain.models.healthunit.HealthUnitSummary;

import java.util.List;
import java.util.Optional;

public interface HealthUnitRepository {
    List<HealthUnitSummary> findSummaryByStateId(Integer stateId);
    List<HealthUnitSummary> findSummaryByMunicipalityId(Integer municipalityId);
    Optional<HealthUnitDetail> findDetailByIdAndPeriodId(Integer healthUnitId, Integer periodId);
}
