package com.itesm.application.usecase.Upload.Indicadores;

import com.itesm.application.dto.Uploader.Auxiliar.CsvIndicatorData;
import com.itesm.domain.repository.IndicatorCsvParserService;
import com.itesm.domain.repository.Upload.Indicadores.TerritoryIndicatorValuesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class IndicadoresUseCase {

    @Inject
    IndicatorCsvParserService indicatorCsvParserService;

    @Inject
    TerritoryIndicatorValuesRepository territoryIndicatorValuesRepository;

    @Transactional
    public void execute(String text) {
        CsvIndicatorData data = indicatorCsvParserService.parse(text);
        territoryIndicatorValuesRepository.save(data.getIndicatorValues());
    }
}
