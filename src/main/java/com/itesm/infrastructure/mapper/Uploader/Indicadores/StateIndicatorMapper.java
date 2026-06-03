package com.itesm.infrastructure.mapper.Uploader.Indicadores;

import com.itesm.domain.models.Uploader.indicator.StateIndicator;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.StateIndicatorEntity;

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
