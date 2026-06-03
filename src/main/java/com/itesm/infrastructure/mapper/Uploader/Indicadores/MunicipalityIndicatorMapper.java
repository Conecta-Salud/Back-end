package com.itesm.infrastructure.mapper.Uploader.Indicadores;


import com.itesm.domain.models.Uploader.indicator.MunicipalityIndicator;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.MunicipalityIndicatorEntity;

public class MunicipalityIndicatorMapper {

    private MunicipalityIndicatorMapper() {}

    public static MunicipalityIndicator toDomain(MunicipalityIndicatorEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MunicipalityIndicator(
                entity.getId(),
                entity.getMunicipality().getId(),
                entity.getPeriod().getId(),
                entity.getTotalPopulation(),
                entity.getPercentageOver60(),
                entity.getHealthcareAccessDeficiency(),
                entity.getTotalPopulation()
        );
    }
}
