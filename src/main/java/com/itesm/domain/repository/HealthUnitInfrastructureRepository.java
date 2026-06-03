package com.itesm.domain.repository;

import com.itesm.domain.models.healthunit.HealthUnitInfrastructure;

import java.util.Optional;

public interface HealthUnitInfrastructureRepository {
    Optional<HealthUnitInfrastructure> findByHealthUnitIdAndPeriodId(Integer healthUnitId, Integer periodId);
    void save(HealthUnitInfrastructure infrastructure);
}
