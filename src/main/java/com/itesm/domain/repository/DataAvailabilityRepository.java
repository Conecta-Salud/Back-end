package com.itesm.domain.repository;

import com.itesm.application.dto.availability.DataAvailabilityItemDto;

import java.util.List;

public interface DataAvailabilityRepository {

    List<Integer> findAvailableAnalysisYears();

    List<DataAvailabilityItemDto> findAvailability(
            String territoryLevel,
            Integer analysisYear,
            String categoryCode
    );
}
