package com.itesm.infrastructure.persistence.repository.Upload.Indicator;

import com.itesm.domain.models.Uploader.indicator.MunicipalityIndicator;
import com.itesm.domain.repository.Upload.Indicadores.MunicipalityIndicatorRepository;
import com.itesm.infrastructure.mapper.Uploader.Indicadores.MunicipalityIndicatorMapper;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.MunicipalityIndicatorEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MunicipalityIndicatorRepositoryImpl implements MunicipalityIndicatorRepository, PanacheRepositoryBase<MunicipalityIndicatorEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Optional<MunicipalityIndicator> findByMunicipalityIdAndPeriodId(Integer municipalityId, Integer periodId) {
        EntityGraph<?> graph = em.getEntityGraph("MunicipalityIndicator.withMunicipalityStateAndPeriod");

        List<MunicipalityIndicatorEntity> result = em.createQuery(
                        "SELECT i FROM MunicipalityIndicatorEntity i " +
                                "WHERE i.municipality.id = :municipalityId " +
                                "AND i.period.id = :periodId",
                        MunicipalityIndicatorEntity.class
                )
                .setParameter("municipalityId", municipalityId)
                .setParameter("periodId", periodId)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(MunicipalityIndicatorMapper.toDomain(result.get(0)));
    }
}
