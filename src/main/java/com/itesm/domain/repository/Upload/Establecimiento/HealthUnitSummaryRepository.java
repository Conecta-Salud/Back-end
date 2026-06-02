package com.itesm.domain.repository.Upload.Establecimiento;

import com.itesm.domain.models.healthunit.HealthUnitSummary;

import java.util.List;

public interface HealthUnitSummaryRepository {
    void save(List<HealthUnitSummary> HealthUnitSummary);
}
