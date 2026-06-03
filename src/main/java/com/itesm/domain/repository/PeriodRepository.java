package com.itesm.domain.repository;

import com.itesm.domain.models.period.Period;

import java.util.List;
import java.util.Optional;

public interface PeriodRepository {
    List<Period> findAllPeriods();
    Optional<Period> findPeriodById(Integer id);
}
