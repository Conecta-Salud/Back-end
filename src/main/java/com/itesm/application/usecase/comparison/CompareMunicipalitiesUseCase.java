package com.itesm.application.usecase.comparison;

import com.itesm.application.dto.comparison.TerritoryComparisonDto;
import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.application.dto.dashboard.HealthDashboardDto;
import com.itesm.domain.repository.ComparisonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class CompareMunicipalitiesUseCase {

    private final ComparisonRepository comparisonRepository;

    @Inject
    public CompareMunicipalitiesUseCase(ComparisonRepository comparisonRepository) {
        this.comparisonRepository = comparisonRepository;
    }

    public List<TerritoryComparisonDto> execute(Integer periodId, List<Integer> municipalityIds) {

        if (periodId == null) {
            throw new BadRequestException("El periodId es obligatorio");
        }

        if (municipalityIds == null || municipalityIds.isEmpty()) {
            throw new BadRequestException("Debes enviar al menos un municipio para comparar");
        }

        if (municipalityIds.size() > 5) {
            throw new BadRequestException("Solo puedes comparar hasta 5 municipios");
        }

        return comparisonRepository.compareMunicipalities(periodId, municipalityIds)
                .stream()
                .map(item -> new TerritoryComparisonDto(
                        item.getId(),
                        item.getName(),
                        item.getType(),
                        new PeriodDto(
                                item.getPeriodId(),
                                item.getPeriodYear()
                        ),
                        new DashboardIndicatorsDto(
                                item.getTotalPopulation(),
                                item.getPercentageOver60(),
                                item.getHealthcareAccessDeficiency(),
                                item.getTotalPovertyPopulation()
                        ),
                        new HealthDashboardDto(
                                item.getTotalHealthUnits(),
                                item.getTotalDoctors(),
                                item.getTotalNurses(),
                                item.getTotalConsultingRooms(),
                                item.getTotalHospitalBeds()
                        )
                ))
                .toList();
    }
}
