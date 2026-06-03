package com.itesm.domain.repository;

import com.itesm.domain.models.healthunit.HealthUnitStaff;

import java.util.Optional;

public interface HealthUnitStaffRepository {
    Optional<HealthUnitStaff> findByHealthUnitIdAndPeriodId(Integer healthUnitId, Integer periodId);
    void save(HealthUnitStaff staff);
}
