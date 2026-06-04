package com.itesm.domain.service;

import com.itesm.domain.models.availability.DataAvailabilityInfo;

import java.util.Optional;

public interface DataAvailabilityService {

    boolean isIndicatorAvailable(
            String indicatorCode,
            String territoryLevel,
            Integer analysisYear
    );

    Optional<String> findAvailabilityNote(
            String indicatorCode,
            String territoryLevel,
            Integer analysisYear
    );

    Optional<DataAvailabilityInfo> findAvailability(
            String indicatorCode,
            String territoryLevel,
            Integer analysisYear
    );
}
