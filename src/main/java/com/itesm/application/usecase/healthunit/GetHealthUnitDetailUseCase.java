package com.itesm.application.usecase.healthunit;

import com.itesm.application.dto.healthunit.*;
import com.itesm.domain.models.healthunit.HealthUnitDetail;
import com.itesm.domain.repository.HealthUnitRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GetHealthUnitDetailUseCase {

    private final HealthUnitRepository healthUnitRepository;

    @Inject
    public GetHealthUnitDetailUseCase(HealthUnitRepository healthUnitRepository) {
        this.healthUnitRepository = healthUnitRepository;
    }

    public HealthUnitDetailDto execute(Integer healthUnitId, Integer periodId) {

        if (periodId == null) {
            throw new BadRequestException("El periodId es obligatorio");
        }

        HealthUnitDetail healthUnit = healthUnitRepository
                .findDetailByIdAndPeriodId(healthUnitId, periodId)
                .orElseThrow(() -> new NotFoundException("No se encontró la unidad de salud solicitada"));

        return new HealthUnitDetailDto(
                healthUnit.getId(),
                healthUnit.getClues(),
                healthUnit.getName(),
                new HealthUnitTerritoryDto(
                        healthUnit.getMunicipalityId(),
                        healthUnit.getMunicipalityName(),
                        healthUnit.getStateId(),
                        healthUnit.getStateName()
                ),
                new HealthUnitClassificationDto(
                        healthUnit.getInstitutionName(),
                        healthUnit.getEstablishmentTypeName(),
                        healthUnit.getMedicalUnitTypeName(),
                        healthUnit.getCareLevel()
                ),
                new HealthUnitStaffDto(
                        healthUnit.getStaff().getTotalDoctors(),
                        healthUnit.getStaff().getTotalNurses()
                ),
                new HealthUnitInfrastructureDto(
                        healthUnit.getInfrastructure().getTotalConsultingRooms(),
                        healthUnit.getInfrastructure().getTotalHospitalBeds()
                )
        );
    }
}
