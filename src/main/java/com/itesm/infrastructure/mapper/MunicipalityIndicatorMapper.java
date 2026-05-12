package com.itesm.infrastructure.mapper;


import com.itesm.domain.models.indicator.MunicipalityIndicator;
import com.itesm.infrastructure.persistence.entity.MunicipalityIndicatorEntity;

public class MunicipalityIndicatorMapper {

    private MunicipalityIndicatorMapper() {}

    public static MunicipalityIndicator toDomain(MunicipalityIndicatorEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MunicipalityIndicator(
                entity.getId(),
                entity.getMunicipality().getId(),
                entity.getMunicipality().getName(),
                entity.getMunicipality().getState().getId(),
                entity.getMunicipality().getState().getName(),
                entity.getPeriod().getId(),
                entity.getPeriod().getPeriodYear(),
                entity.getTotalPopulation(),
                entity.getPercentageOver60(),
                entity.getHealthcareAccessDeficiency(),
                entity.getTotalPovertyPopulation()
        );
    }
}
