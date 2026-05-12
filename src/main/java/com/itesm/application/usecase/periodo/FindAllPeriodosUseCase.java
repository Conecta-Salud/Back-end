package com.itesm.application.usecase.periodo;

import com.itesm.application.dto.periodo.PeriodoResponseDto;
import com.itesm.domain.repository.PeriodoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindAllPeriodosUseCase {

    private final PeriodoRepository periodoRepository;

    @Inject
    public FindAllPeriodosUseCase(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    public List<PeriodoResponseDto> execute() {
        return periodoRepository.getAllPeriodos()
                .stream()
                .map(periodo -> new PeriodoResponseDto(
                        periodo.getId(),
                        periodo.getAnio(),
                        periodo.getEstatus()
                ))
                .collect(Collectors.toList());
    }
}