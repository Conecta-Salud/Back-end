package com.itesm.application.usecase.map;

import com.itesm.application.dto.map.MapIndicatorResponseDto;
import com.itesm.domain.models.map.MapIndicator;
import com.itesm.domain.models.map.MapIndicatorType;
import com.itesm.domain.repository.MapRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetMunicipalityMapUseCaseTest {

    private MapRepository mapRepository;
    private GetMunicipalityMapUseCase useCase;

    @BeforeEach
    void setUp() {
        mapRepository = mock(MapRepository.class);
        useCase = new GetMunicipalityMapUseCase(mapRepository);
    }

    @Test
    void execute_shouldReturnMappedMunicipalityIndicators() {
        when(mapRepository.existsPeriodByYear(2024)).thenReturn(true);
        when(mapRepository.existsStateByCode("14")).thenReturn(true);
        when(mapRepository.findMunicipalityIndicators("14", MapIndicatorType.MEDICAL_COVERAGE, 2024)).thenReturn(
                List.of(new MapIndicator("14001", "Guadalajara", BigDecimal.valueOf(2.9), MapIndicatorType.MEDICAL_COVERAGE, 2024, "%", "available", "note", "source"))
        );

        List<MapIndicatorResponseDto> result = useCase.execute("14", "medical_coverage", 2024);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("14001", result.get(0).getCode());
        assertEquals("Guadalajara", result.get(0).getName());
    }

    @Test
    void execute_shouldThrowWhenStateCodeIsBlank() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute("", "medical_coverage", 2024));
        assertEquals("El stateCode es obligatoria", ex.getMessage());
    }

    @Test
    void execute_shouldThrowWhenStateDoesNotExist() {
        when(mapRepository.existsPeriodByYear(2024)).thenReturn(true);
        when(mapRepository.existsStateByCode("14")).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute("14", "medical_coverage", 2024));
        assertTrue(ex.getMessage().contains("No existe estado con clave INEGI"));
    }
}
