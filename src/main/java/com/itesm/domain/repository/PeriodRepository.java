package com.itesm.domain.repository;

import com.itesm.domain.models.period.Period;
import java.util.List;

public interface PeriodRepository {
    List<Period> findAllPeriods();
}
