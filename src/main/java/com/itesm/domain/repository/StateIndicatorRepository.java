package com.itesm.domain.repository;

import com.itesm.domain.models.indicator.StateIndicator;

import java.util.Optional;

public interface StateIndicatorRepository {
    Optional<StateIndicator> findByStateIdAndPeriodId(Integer stateId, Integer periodId);
}
