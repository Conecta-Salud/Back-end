package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.period.Period;
import com.itesm.infrastructure.persistence.entity.PeriodEntity;

public class PeriodMapper {

    private PeriodMapper() {}

    public static Period toDomain(PeriodEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Period(
                entity.getId(),
                entity.getPeriodYear() == null ? null : entity.getPeriodYear().intValue(),
                entity.getStatus()
        );
    }
}
