package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicator.StateIndicator;
import com.itesm.domain.repository.StateIndicatorRepository;
import com.itesm.infrastructure.mapper.StateIndicatorMapper;
import com.itesm.infrastructure.persistence.entity.StateIndicatorEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StateIndicatorRepositoryImpl implements StateIndicatorRepository, PanacheRepositoryBase<StateIndicatorEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Optional<StateIndicator> findByStateIdAndPeriodId(Integer stateId, Integer periodId) {
        EntityGraph<?> graph = em.getEntityGraph("StateIndicator.withStateAndPeriod");

        List<StateIndicatorEntity> result = em.createQuery(
                        "SELECT i FROM StateIndicatorEntity i " +
                                "WHERE i.state.id = :stateId " +
                                "AND i.period.id = :periodId",
                        StateIndicatorEntity.class
                )
                .setParameter("stateId", stateId)
                .setParameter("periodId", periodId)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(StateIndicatorMapper.toDomain(result.get(0)));
    }
}
