package com.itesm.domain.repository;

import com.itesm.domain.models.dashboard.CountryIndicatorsDashboard;
import com.itesm.domain.models.dashboard.HealthDashboard;

import java.util.Optional;

public interface CountryDashboardRepository {
    Optional<CountryIndicatorsDashboard> findIndicatorsByPeriod(Integer periodId);
    Optional<HealthDashboard> findHealthByPeriod(Integer periodId);
}
