package com.itesm.application.usecase.municipality;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.domain.repository.MunicipalityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindMunicipalitiesByStateUseCase {

    private final MunicipalityRepository municipalityRepository;

    @Inject
    public FindMunicipalitiesByStateUseCase(MunicipalityRepository municipalityRepository) {
        this.municipalityRepository = municipalityRepository;
    }

    public List<MunicipalityResponseDto> execute(Integer stateId) {
        if (stateId == null) {
            throw new BadRequestException("Se requiere un stateId");
        }

        return municipalityRepository.findByStateId(stateId)
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
