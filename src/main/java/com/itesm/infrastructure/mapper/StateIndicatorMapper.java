package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.indicator.StateIndicator;
import com.itesm.infrastructure.persistence.entity.StateIndicatorEntity;

public class StateIndicatorMapper {

    private StateIndicatorMapper() {}

    public static StateIndicator toDomain(StateIndicatorEntity entity) {
        if (entity == null) {
            return null;
        }

        return new StateIndicator(
                entity.getId(),
                entity.getState().getId(),
                entity.getState().getName(),
                entity.getPeriod().getId(),
                entity.getPeriod().getPeriodYear(),
                entity.getTotalPopulation(),
                entity.getPercentageOver60(),
                entity.getHealthcareAccessDeficiency(),
                entity.getTotalPovertyPopulation()
        );
    }
}
