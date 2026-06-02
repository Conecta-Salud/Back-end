package com.itesm.application.usecase.municipality;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.domain.repository.Upload.Establecimiento.MunicipalityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindAllMunicipalitiesUseCase {

    private final MunicipalityRepository municipalityRepository;

    @Inject
    public FindAllMunicipalitiesUseCase(MunicipalityRepository municipalityRepository) {
        this.municipalityRepository = municipalityRepository;
    }

    public List<MunicipalityResponseDto> execute() {
        return municipalityRepository.findAllMunicipalities()
                .stream()
                .map(municipality -> new MunicipalityResponseDto(
                        municipality.getId(),
                        municipality.getName(),
                        municipality.getInegiCode()
                ))
                .collect(Collectors.toList());
    }
}
