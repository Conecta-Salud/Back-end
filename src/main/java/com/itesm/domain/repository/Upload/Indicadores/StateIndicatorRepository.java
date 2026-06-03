package com.itesm.domain.repository.Upload.Indicadores;

import com.itesm.domain.models.Uploader.indicator.StateIndicator;

import java.util.Optional;

public interface StateIndicatorRepository {
    Optional<StateIndicator> findByStateIdAndPeriodId(Integer stateId, Integer periodId);
}
