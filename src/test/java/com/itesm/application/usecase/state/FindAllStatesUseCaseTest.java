package com.itesm.application.usecase.state;

import com.itesm.application.dto.state.StateResponseDto;
import com.itesm.domain.models.state.State;
import com.itesm.domain.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllStatesUseCaseTest {

    private StateRepository stateRepository;
    private FindAllStatesUseCase useCase;

    @BeforeEach
    void setUp() {
        stateRepository = mock(StateRepository.class);
        useCase = new FindAllStatesUseCase(stateRepository);
    }

    @Test
    void execute_shouldReturnMappedStateResponses() {
        when(stateRepository.findAllStates()).thenReturn(
                List.of(
                        new State(1, "Jalisco", "14"),
                        new State(2, "Nuevo León", "19")
                )
        );

        List<StateResponseDto> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());

        StateResponseDto first = result.get(0);
        assertEquals(1, first.getId());
        assertEquals("Jalisco", first.getName());
        assertEquals("14", first.getInegiCode());

        StateResponseDto second = result.get(1);
        assertEquals(2, second.getId());
        assertEquals("Nuevo León", second.getName());
        assertEquals("19", second.getInegiCode());
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoStates() {
        when(stateRepository.findAllStates()).thenReturn(List.of());

        List<StateResponseDto> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
