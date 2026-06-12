package com.itesm.application.usecase.location;

import com.itesm.application.dto.location.LocationSearchResultDto;
import com.itesm.domain.models.location.LocationSearchResult;
import com.itesm.domain.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchLocationsUseCaseTest {

    private LocationRepository locationRepository;
    private SearchLocationsUseCase useCase;

    @BeforeEach
    void setUp() {
        locationRepository = mock(LocationRepository.class);
        useCase = new SearchLocationsUseCase(locationRepository);
    }

    @Test
    void execute_shouldReturnResultsWhenQueryIsValid() {
        when(locationRepository.searchLocations("Guad", 5)).thenReturn(
                List.of(new LocationSearchResult(1, "GDL", "Guadalajara", "city", 14, "14", "Jalisco", "Guadalajara, Jalisco"))
        );

        List<LocationSearchResultDto> result = useCase.execute("Guad", 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("GDL", result.get(0).getCode());
        assertEquals("Guadalajara, Jalisco", result.get(0).getDisplayName());
    }

    @Test
    void execute_shouldReturnEmptyListWhenQueryTooShort() {
        List<LocationSearchResultDto> result = useCase.execute("a", 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(locationRepository, never()).searchLocations(anyString(), anyInt());
    }
}
