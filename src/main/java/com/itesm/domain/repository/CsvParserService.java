package com.itesm.domain.repository;

import com.itesm.application.dto.Uploader.Auxiliar.CsvData;
import java.nio.file.Path;

public interface CsvParserService {
    CsvData parse(String file);
}
