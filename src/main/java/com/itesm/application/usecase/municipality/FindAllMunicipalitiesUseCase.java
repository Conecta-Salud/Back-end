package com.itesm.application.usecase.municipality;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.domain.repository.MunicipalityRepository;
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
                        municipality.getStateId(),
                        municipality.getName(),
                        municipality.getInegiCode(),
                        municipality.getLatitude(),
                        municipality.getLongitude()
                ))
                .collect(Collectors.toList());
    }
}
