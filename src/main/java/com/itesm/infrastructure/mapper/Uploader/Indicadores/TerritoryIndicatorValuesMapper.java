package com.itesm.infrastructure.mapper.Uploader.Indicadores;

import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.TerritoryIndicatorValuesEntity;

public class TerritoryIndicatorValuesMapper {

    private TerritoryIndicatorValuesMapper() {}

    public static TerritoryIndicatorValues toDomain(TerritoryIndicatorValuesEntity entity){
        if (entity == null) {
            return null;
        }
        return new TerritoryIndicatorValues(
                entity.getId(),
                entity.getTerritoryLevel(),
                entity.getState() != null ? entity.getState().getId() : null,
                entity.getMunicipality() != null ? entity.getMunicipality().getId() : null,
                entity.getIndicator().getId(),
                entity.getValue(),
                entity.getAnalysisYear(),
                entity.getSourceYear(),
                entity.getDataSource().getId(),
                entity.getSourceFile(),
                entity.getAvailabilityStatus(),
                entity.getMethodologyNote()
        );
    }
}
