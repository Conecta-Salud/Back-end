package com.itesm.domain.repository.Upload.Indicadores;

import com.itesm.domain.models.Uploader.Auxiliar.TerritoryLevel;
import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;

import java.util.Optional;

public interface TerritoryIndicatorValuesRepository {
    Optional<TerritoryIndicatorValues> findStateIndicator(Integer stateId, Integer indicatorId, Short analysisYear);
    Optional<TerritoryIndicatorValues> findMunicipalityIndicator(Integer municipalityId, Integer indicatorId, Short analysisYear);
    Optional<TerritoryIndicatorValues> findCountryIndicator(Integer indicatorId, Short analysisYear);
    Optional<TerritoryIndicatorValues> findStateIndicatorByCode(String indicatorCode, Integer stateId, Short analysisYear);
    Optional<TerritoryIndicatorValues> findMunicipalityIndicatorByCode(String indicatorCode, Integer municipalityId, Short analysisYear);
}
