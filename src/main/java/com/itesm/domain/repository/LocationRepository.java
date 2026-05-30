package com.itesm.domain.repository;

import com.itesm.domain.models.location.LocationSearchResult;

import java.util.List;

public interface LocationRepository {
    List<LocationSearchResult> searchLocations(String query, int limit);
}
