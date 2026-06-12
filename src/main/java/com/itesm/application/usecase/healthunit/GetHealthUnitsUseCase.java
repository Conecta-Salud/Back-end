package com.itesm.application.usecase.healthunit;

import com.itesm.application.dto.healthunit.HealthUnitSummaryDto;
import com.itesm.domain.repository.HealthUnitRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class GetHealthUnitsUseCase {

    private final HealthUnitRepository healthUnitRepository;

    @Inject
    public GetHealthUnitsUseCase(HealthUnitRepository healthUnitRepository) {
        this.healthUnitRepository = healthUnitRepository;
    }

    public List<HealthUnitSummaryDto> execute(Integer stateId, Integer municipalityId) {

        if (stateId == null && municipalityId == null) {
            throw new BadRequestException("Debes enviar estadoId o municipioId");
        }

        if (stateId != null && municipalityId != null) {
            throw new BadRequestException("Envía solo estadoId o municipioId, no ambos");
        }

        return (municipalityId != null
                ? healthUnitRepository.findSummaryByMunicipalityId(municipalityId)
                : healthUnitRepository.findSummaryByStateId(stateId)
        )
                .stream()
                .map(healthUnit -> new HealthUnitSummaryDto(
                        healthUnit.getId(),
                        healthUnit.getClues(),
                        healthUnit.getName(),
                        healthUnit.getMunicipalityId(),
                        healthUnit.getMunicipalityName(),
                        healthUnit.getStateId(),
                        healthUnit.getStateName(),
                        healthUnit.getInstitutionName(),
                        healthUnit.getEstablishmentTypeName(),
                        healthUnit.getMedicalUnitTypeName(),
                        healthUnit.getCareLevel()
                ))
                .toList();
    }
}
