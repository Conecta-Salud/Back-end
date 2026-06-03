package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.healthunit.HealthUnitInfrastructure;
import com.itesm.domain.repository.HealthUnitInfrastructureRepository;
import com.itesm.infrastructure.mapper.HealthUnitInfrastructureMapper;
import com.itesm.infrastructure.persistence.entity.HealthUnitInfrastructureEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class HealthUnitInfrastructureRepositoryImpl implements HealthUnitInfrastructureRepository, PanacheRepositoryBase<HealthUnitInfrastructureEntity, Integer> {

    @Override
    public Optional<HealthUnitInfrastructure> findByHealthUnitIdAndPeriodId(Integer healthUnitId, Integer periodId) {
        return find("healthUnit.id = ?1 AND period.id = ?2", healthUnitId, periodId)
                .firstResultOptional()
                .map(HealthUnitInfrastructureMapper::toDomain);
    }

    @Override
    @Transactional
    public void save(HealthUnitInfrastructure infrastructure) {
        HealthUnitInfrastructureEntity entity = HealthUnitInfrastructureMapper.toEntity(infrastructure);
        persist(entity);
    }
}
