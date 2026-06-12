package com.itesm.application.usecase.healthunit;

import com.itesm.application.dto.healthunit.HealthUnitSummaryDto;
import com.itesm.domain.models.healthunit.CareLevel;
import com.itesm.domain.models.healthunit.HealthUnitSummary;
import com.itesm.domain.repository.HealthUnitRepository;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetHealthUnitsUseCaseTest {

    private HealthUnitRepository healthUnitRepository;
    private GetHealthUnitsUseCase useCase;

    @BeforeEach
    void setUp() {
        healthUnitRepository = mock(HealthUnitRepository.class);
        useCase = new GetHealthUnitsUseCase(healthUnitRepository);
    }

    @Test
    void execute_shouldReturnStateHealthUnits() {
        when(healthUnitRepository.findSummaryByStateId(14)).thenReturn(
                List.of(new HealthUnitSummary(1, "ABC123", "Centro 1", 101, "Guadalajara", 14, "Jalisco", "IMSS", "Hospital", "General", CareLevel.primary))
        );

        List<HealthUnitSummaryDto> result = useCase.execute(14, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC123", result.get(0).getClues());
        assertEquals(CareLevel.primary, result.get(0).getCareLevel());
    }

    @Test
    void execute_shouldReturnMunicipalityHealthUnits() {
        when(healthUnitRepository.findSummaryByMunicipalityId(101)).thenReturn(
                List.of(new HealthUnitSummary(2, "DEF456", "Centro 2", 101, "Guadalajara", 14, "Jalisco", "SSA", "Clínica", "Especializada", CareLevel.secondary))
        );

        List<HealthUnitSummaryDto> result = useCase.execute(null, 101);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DEF456", result.get(0).getClues());
        assertEquals("SSA", result.get(0).getInstitution());
    }

    @Test
    void execute_shouldThrowWhenNeitherStateNorMunicipalityProvided() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(null, null));
        assertEquals("Debes enviar estadoId o municipioId", ex.getMessage());
    }

    @Test
    void execute_shouldThrowWhenBothStateAndMunicipalityProvided() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(14, 101));
        assertEquals("Envía solo estadoId o municipioId, no ambos", ex.getMessage());
    }
}
