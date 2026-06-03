package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicator.MunicipalityIndicator;
import com.itesm.domain.repository.MunicipalityIndicatorRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class MunicipalityIndicatorRepositoryImpl implements MunicipalityIndicatorRepository {

    @Override
    public Optional<MunicipalityIndicator> findByMunicipalityIdAndPeriodId(Integer municipalityId, Integer periodId) {
        return Optional.empty();
    }
}
