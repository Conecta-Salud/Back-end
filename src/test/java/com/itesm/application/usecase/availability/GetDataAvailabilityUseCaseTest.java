package com.itesm.application.usecase.availability;

import com.itesm.application.dto.availability.DataAvailabilityItemDto;
import com.itesm.application.dto.availability.DataAvailabilityResponseDto;
import com.itesm.domain.repository.DataAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetDataAvailabilityUseCaseTest {

    private DataAvailabilityRepository repository;
    private GetDataAvailabilityUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(DataAvailabilityRepository.class);
        useCase = new GetDataAvailabilityUseCase(repository);
    }

    @Test
    void execute_shouldReturnAvailabilityResponse() {
        when(repository.findAvailableAnalysisYears()).thenReturn(List.of(2023, 2024));
        when(repository.findAvailability("state", 2024, "health")).thenReturn(
                List.of(new DataAvailabilityItemDto("CAT", "Categoria", "health", "Salud", "state", 2024, 2024, true, "available", "note"))
        );

        DataAvailabilityResponseDto response = useCase.execute("state", 2024, "health");

        assertNotNull(response);
        assertEquals(2, response.getYears().size());
        assertEquals(1, response.getItems().size());
        assertEquals("CAT", response.getItems().get(0).getCategoryCode());
    }
}
