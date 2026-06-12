package com.itesm.application.usecase.healthunit;

import com.itesm.application.dto.healthunit.HealthUnitDetailDto;
import com.itesm.domain.models.healthunit.HealthUnitDetail;
import com.itesm.domain.models.healthunit.HealthUnitInfrastructureSummary;
import com.itesm.domain.models.healthunit.HealthUnitStaffSummary;
import com.itesm.domain.repository.HealthUnitRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetHealthUnitDetailUseCaseTest {

    private HealthUnitRepository healthUnitRepository;
    private GetHealthUnitDetailUseCase useCase;

    @BeforeEach
    void setUp() {
        healthUnitRepository = mock(HealthUnitRepository.class);
        useCase = new GetHealthUnitDetailUseCase(healthUnitRepository);
    }

    @Test
    void execute_shouldReturnHealthUnitDetail() {
        HealthUnitDetail detail = new HealthUnitDetail(
                123,
                "ABC123",
                "Centro Médico",
                101,
                "Guadalajara",
                14,
                "Jalisco",
                "IMSS",
                "Hospital",
                "General",
                null,
                new HealthUnitStaffSummary(5L, 10L),
                new HealthUnitInfrastructureSummary(3L, 20L)
        );

        when(healthUnitRepository.findDetailByIdAndPeriodId(123, 2024)).thenReturn(Optional.of(detail));

        HealthUnitDetailDto result = useCase.execute(123, 2024);

        assertNotNull(result);
        assertEquals("ABC123", result.getClues());
        assertEquals("Centro Médico", result.getName());
        assertNotNull(result.getStaff());
        assertEquals(5L, result.getStaff().getTotalDoctors());
        assertNotNull(result.getInfrastructure());
        assertEquals(20L, result.getInfrastructure().getTotalHospitalBeds());
    }

    @Test
    void execute_shouldThrowWhenDetailNotFound() {
        when(healthUnitRepository.findDetailByIdAndPeriodId(999, 2024)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute(999, 2024));
        assertEquals("No se encontró la unidad de salud solicitada", ex.getMessage());
    }
}
