package com.itesm.application.usecase.Upload.Establecimientos;

import com.itesm.application.dto.Uploader.Auxiliar.CsvData;
import com.itesm.domain.repository.CsvParserService;
import com.itesm.domain.repository.Upload.Establecimiento.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.transaction.Transactional;

import java.nio.file.Path;


@ApplicationScoped
public class EstablecimientoUseCase {

    @Inject
    CsvParserService csvParserService;

    @Inject
    StateRepository stateRepository;
    @Inject
    MunicipalityRepository municipalityRepository;
    @Inject
    InstitutionRepository institutionRepository;
    @Inject
    EstablishmentRepository establishmentRepository;
    @Inject
    MedicalUnitTypesRepository medicalUnitTypesRepository;
    @Inject
    HealthUnitSummaryRepository healthUnitSummaryRepository;


    @Transactional
    public void execute(String text) {

        CsvData data =
                csvParserService.parse(text);

        stateRepository.save(data.getStates());

        municipalityRepository.save(data.getMunicipalities());

        institutionRepository.save(data.getInstitutions());

        establishmentRepository.save(data.getEstablishments());

        medicalUnitTypesRepository.save(data.getMedicalUnitTypes());

        healthUnitSummaryRepository.save(data.getHealthUnitSummaries());
    }
}
