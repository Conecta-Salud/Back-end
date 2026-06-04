package com.itesm.application.dto.availability;

import java.util.List;

public class DataAvailabilityResponseDto {

    private final List<Integer> years;
    private final List<DataAvailabilityItemDto> items;

    public DataAvailabilityResponseDto(
            List<Integer> years,
            List<DataAvailabilityItemDto> items
    ) {
        this.years = years;
        this.items = items;
    }

    public List<Integer> getYears() {
        return years;
    }

    public List<DataAvailabilityItemDto> getItems() {
        return items;
    }
}
