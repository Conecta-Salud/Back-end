package com.itesm.application.usecase.map;

import com.itesm.application.dto.comparison.TerritoryComparisonDto;
import com.itesm.application.dto.dashboard.DashboardIndicatorsDto;
import com.itesm.application.dto.dashboard.HealthDashboardDto;
import com.itesm.application.dto.dashboard.PeriodDto;
import com.itesm.domain.repository.ComparisonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CompareStatesByCodesUseCase {

    private final ComparisonRepository comparisonRepository;

    @Inject
    public CompareStatesByCodesUseCase(ComparisonRepository comparisonRepository) {
        this.comparisonRepository = comparisonRepository;
    }

    public List<TerritoryComparisonDto> execute(Integer periodId, List<String> stateCodes) {
        if (periodId == null) {
            throw new BadRequestException("periodId is required");
        }

        if (stateCodes == null || stateCodes.isEmpty()) {
            throw new BadRequestException("At least one state code is required for comparison");
        }

        if (stateCodes.size() > 5) {
            throw new BadRequestException("You can compare up to 5 states");
        }

        return comparisonRepository.compareStatesByCodes(periodId, stateCodes)
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
