package com.itesm.domain.repository;

import com.itesm.application.dto.Uploader.Auxiliar.CsvIndicatorData;

public interface IndicatorCsvParserService {
    CsvIndicatorData parse(String fileOrContent);
}
