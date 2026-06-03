package com.itesm.domain.models.upload;

public record DataAvailabilityWriteDraft(
        Integer categoryId,
        Integer indicatorId,
        String territoryLevel,
        Short analysisYear,
        Short sourceYear,
        boolean available,
        String availabilityStatus,
        String note
) {
}
