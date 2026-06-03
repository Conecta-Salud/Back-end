package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicator.StateIndicator;
import com.itesm.domain.repository.StateIndicatorRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class StateIndicatorRepositoryImpl implements StateIndicatorRepository {

    @Override
    public Optional<StateIndicator> findByStateIdAndPeriodId(Integer stateId, Integer periodId) {
        return Optional.empty();
    }
}
