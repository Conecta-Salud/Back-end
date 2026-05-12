package com.itesm.domain.repository;

import com.itesm.domain.models.dashboard.HealthDashboard;

import java.util.Optional;

public interface HealthDashboardRepository {
    Optional<HealthDashboard> findHealthByStateAndPeriod(Integer stateId, Integer periodId);
    Optional<HealthDashboard> findHealthByMunicipalityAndPeriod(Integer municipalityId, Integer periodId);
}
