package com.itesm.domain.repository;

import com.itesm.domain.models.indicator.MunicipalityIndicator;

import java.util.Optional;

public interface MunicipalityIndicatorRepository {
    Optional<MunicipalityIndicator> findByMunicipalityIdAndPeriodId(Integer municipalityId, Integer periodId);
}
