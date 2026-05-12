package com.itesm.application.usecase.period;

import com.itesm.application.dto.period.PeriodResponseDto;
import com.itesm.domain.repository.PeriodRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindAllPeriodsUseCase {

    private final PeriodRepository periodRepository;

    @Inject
    public FindAllPeriodsUseCase(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    public List<PeriodResponseDto> execute() {
        return periodRepository.findAllPeriods()
                .stream()
                .map(period -> new PeriodResponseDto(
                        period.getId(),
                        period.getPeriodYear(),
                        period.getStatus()
                ))
                .collect(Collectors.toList());
    }
}