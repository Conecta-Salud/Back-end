package com.itesm.application.usecase.municipality;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.domain.models.municipality.Municipality;
import com.itesm.domain.repository.MunicipalityRepository;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindMunicipalitiesByStateUseCaseTest {

    private MunicipalityRepository municipalityRepository;
    private FindMunicipalitiesByStateUseCase useCase;

    @BeforeEach
    void setUp() {
        municipalityRepository = mock(MunicipalityRepository.class);
        useCase = new FindMunicipalitiesByStateUseCase(municipalityRepository);
    }

    @Test
    void execute_shouldReturnMappedMunicipalitiesForState() {
        when(municipalityRepository.findByStateId(1)).thenReturn(
                List.of(new Municipality(10, 1, "Guadalajara", "001"))
        );

        List<MunicipalityResponseDto> result = useCase.execute(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Guadalajara", result.get(0).getName());
    }

    @Test
    void execute_shouldThrowWhenStateIdIsNull() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(null));
        assertEquals("Se requiere un stateId", ex.getMessage());
    }
}
