package com.itesm.application.usecase.period;

import com.itesm.application.dto.period.PeriodResponseDto;
import com.itesm.domain.models.period.Period;
import com.itesm.domain.models.period.PeriodStatus;
import com.itesm.domain.repository.PeriodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllPeriodsUseCaseTest {

    private PeriodRepository periodRepository;
    private FindAllPeriodsUseCase useCase;

    @BeforeEach
    void setUp() {
        periodRepository = mock(PeriodRepository.class);
        useCase = new FindAllPeriodsUseCase(periodRepository);
    }

    @Test
    void execute_shouldReturnPeriodsMapped() {
        when(periodRepository.findAllPeriods()).thenReturn(
                List.of(
                        new Period(1, 2024, PeriodStatus.open),
                        new Period(2, 2023, PeriodStatus.published)
                )
        );

        List<PeriodResponseDto> result = useCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2024, result.get(0).getPeriodYear());
        assertEquals(PeriodStatus.open, result.get(0).getStatus());
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoPeriods() {
        when(periodRepository.findAllPeriods()).thenReturn(List.of());

        List<PeriodResponseDto> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
