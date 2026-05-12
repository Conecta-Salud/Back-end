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
import java.util.stream.Collectors;

@ApplicationScoped
public class CompareStatesUseCase {

    private final ComparisonRepository comparisonRepository;

    @Inject
    public CompareStatesUseCase(ComparisonRepository comparisonRepository) {
        this.comparisonRepository = comparisonRepository;
    }

    public List<TerritoryComparisonDto> execute(Integer periodId, List<Integer> stateIds) {

        if (periodId  == null) {
            throw new BadRequestException("El periodId es obligatorio");
        }

        if (stateIds  == null || stateIds .isEmpty()) {
            throw new BadRequestException("Debes enviar al menos un estado para comparar");
        }

        if (stateIds.size() > 5) {
            throw new BadRequestException("Solo puedes comparar hasta 5 estados");
        }

        return comparisonRepository.compareStates(periodId, stateIds )
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
                .collect(Collectors.toList());
    }
}
