package com.itesm.application.usecase.Upload.Sectoriales;

import com.itesm.domain.repository.SectorialCsvParserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SectorialesUseCase {

    @Inject
    SectorialCsvParserService sectoriaParserService;

    @Transactional
    public void execute(String fileContent) {
        sectoriaParserService.parse(fileContent);
    }
}
