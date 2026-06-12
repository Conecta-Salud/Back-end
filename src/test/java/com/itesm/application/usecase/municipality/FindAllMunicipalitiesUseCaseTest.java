package com.itesm.application.usecase.municipality;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.domain.models.municipality.Municipality;
import com.itesm.domain.repository.MunicipalityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllMunicipalitiesUseCaseTest {

    private MunicipalityRepository municipalityRepository;
    private FindAllMunicipalitiesUseCase useCase;

    @BeforeEach
    void setUp() {
        municipalityRepository = mock(MunicipalityRepository.class);
        useCase = new FindAllMunicipalitiesUseCase(municipalityRepository);
    }

    @Test
    void execute_shouldReturnMappedMunicipalities() {
        when(municipalityRepository.findAllMunicipalities()).thenReturn(
                List.of(
                        new Municipality(10, 1, "Guadalajara", "001"),
                        new Municipality(11, 1, "Zapopan", "002")
                )
        );

        List<MunicipalityResponseDto> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals("Guadalajara", result.get(0).getName());
        assertEquals("001", result.get(0).getInegiCode());
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoMunicipalities() {
        when(municipalityRepository.findAllMunicipalities()).thenReturn(List.of());

        List<MunicipalityResponseDto> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
