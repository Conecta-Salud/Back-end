package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.healthunit.HealthUnitStaff;
import com.itesm.domain.repository.HealthUnitStaffRepository;
import com.itesm.infrastructure.mapper.HealthUnitStaffMapper;
import com.itesm.infrastructure.persistence.entity.HealthUnitStaffEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class HealthUnitStaffRepositoryImpl implements HealthUnitStaffRepository, PanacheRepositoryBase<HealthUnitStaffEntity, Integer> {

    @Override
    public Optional<HealthUnitStaff> findByHealthUnitIdAndPeriodId(Integer healthUnitId, Integer periodId) {
        return find("healthUnit.id = ?1 AND period.id = ?2", healthUnitId, periodId)
                .firstResultOptional()
                .map(HealthUnitStaffMapper::toDomain);
    }

    @Override
    @Transactional
    public void save(HealthUnitStaff staff) {
        HealthUnitStaffEntity entity = HealthUnitStaffMapper.toEntity(staff);
        persist(entity);
    }
}
