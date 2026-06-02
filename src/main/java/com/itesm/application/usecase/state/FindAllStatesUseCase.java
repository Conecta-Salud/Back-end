package com.itesm.application.usecase.state;

import com.itesm.application.dto.state.StateResponseDto;
import com.itesm.domain.repository.Upload.Establecimiento.StateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FindAllStatesUseCase {

    private final StateRepository stateRepository;

    @Inject
    public FindAllStatesUseCase(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }
    public List<StateResponseDto> execute() {
        return stateRepository.findAllStates()
                .stream()
                .map(state -> new StateResponseDto(
                        state.getId(),
                        state.getName(),
                        state.getInegiCode()
                ))
                .collect(Collectors.toList());
    }
}
