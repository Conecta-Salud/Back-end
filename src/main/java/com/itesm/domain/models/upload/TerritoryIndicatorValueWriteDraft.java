package com.itesm.domain.models.upload;

import java.math.BigDecimal;

public record TerritoryIndicatorValueWriteDraft(
        String territoryLevel,
        Integer stateId,
        Integer municipalityId,
        Integer indicatorId,
        BigDecimal value,
        Short analysisYear,
        Short sourceYear,
        Integer dataSourceId,
        String sourceFile,
        String availabilityStatus,
        String methodologyNote
) {
}
