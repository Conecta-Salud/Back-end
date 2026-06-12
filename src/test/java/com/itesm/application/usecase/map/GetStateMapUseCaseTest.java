package com.itesm.application.usecase.map;

import com.itesm.application.dto.map.MapIndicatorResponseDto;
import com.itesm.domain.models.map.MapIndicator;
import com.itesm.domain.models.map.MapIndicatorType;
import com.itesm.domain.models.map.MapLevel;
import com.itesm.domain.models.map.ColorToken;
import com.itesm.domain.repository.MapRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetStateMapUseCaseTest {

    private MapRepository mapRepository;
    private GetStateMapUseCase useCase;

    @BeforeEach
    void setUp() {
        mapRepository = mock(MapRepository.class);
        useCase = new GetStateMapUseCase(mapRepository);
    }

    @Test
    void execute_shouldReturnMappedStateIndicators() {
        when(mapRepository.existsPeriodByYear(2024)).thenReturn(true);
        when(mapRepository.findStateIndicators(MapIndicatorType.MEDICAL_COVERAGE, 2024)).thenReturn(
                List.of(new MapIndicator("14", "Jalisco", BigDecimal.valueOf(2.8), MapIndicatorType.MEDICAL_COVERAGE, 2024, "%", "available", "note", "source"))
        );

        List<MapIndicatorResponseDto> result = useCase.execute("medical_coverage", 2024);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("14", result.get(0).getCode());
        assertEquals("Jalisco", result.get(0).getName());
        assertEquals(2024, result.get(0).getSourceYear());
    }

    @Test
    void execute_shouldThrowWhenYearIsMissing() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute("medical_coverage", null));
        assertEquals("El periodo es obligatorio", ex.getMessage());
    }

    @Test
    void execute_shouldThrowWhenPeriodDoesNotExist() {
        when(mapRepository.existsPeriodByYear(2024)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute("medical_coverage", 2024));
        assertTrue(ex.getMessage().contains("No existe periodo para el año solicitado"));
    }
}
