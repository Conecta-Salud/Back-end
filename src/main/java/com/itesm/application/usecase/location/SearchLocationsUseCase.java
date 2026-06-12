package com.itesm.application.usecase.location;

import com.itesm.application.dto.location.LocationSearchResultDto;
import com.itesm.domain.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class SearchLocationsUseCase {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 20;
    private static final int MIN_QUERY_LENGTH = 2;

    private final LocationRepository locationRepository;

    @Inject
    public SearchLocationsUseCase(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<LocationSearchResultDto> execute(String query, Integer limit) {
        String safeQuery = normalizeQuery(query);

        if (safeQuery.length() < MIN_QUERY_LENGTH) {
            return Collections.emptyList();
        }

        int safeLimit = normalizeLimit(limit);

        return locationRepository.searchLocations(safeQuery, safeLimit)
                .stream()
                .map(item -> new LocationSearchResultDto(
                        item.getId(),
                        item.getCode(),
                        item.getName(),
                        item.getType(),
                        item.getStateId(),
                        item.getStateCode(),
                        item.getStateName(),
                        item.getDisplayName()
                ))
                .toList();
    }

    private String normalizeQuery(String query) {
        return query == null ? "" : query.trim();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }

        return Math.min(limit, MAX_LIMIT);
    }
}
