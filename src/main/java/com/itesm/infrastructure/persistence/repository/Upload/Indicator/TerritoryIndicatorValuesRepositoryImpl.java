package com.itesm.infrastructure.persistence.repository.Upload.Indicator;


import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;
import com.itesm.domain.repository.Upload.Indicadores.TerritoryIndicatorValuesRepository;
import com.itesm.infrastructure.mapper.Uploader.Indicadores.TerritoryIndicatorValuesMapper;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.TerritoryIndicatorValuesEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TerritoryIndicatorValuesRepositoryImpl implements TerritoryIndicatorValuesRepository, PanacheRepositoryBase<TerritoryIndicatorValuesEntity, Integer> {
    @Inject
    EntityManager em;

    @Override
    public Optional<TerritoryIndicatorValues> findStateIndicator(
            Integer stateId,
            Integer indicatorId,
            Short analysisYear) {

        List<TerritoryIndicatorValuesEntity> result = em.createQuery(
                        """
                        SELECT t
                        FROM TerritoryIndicatorValuesEntity t
                        WHERE t.state.id = :stateId
                        AND t.indicator.id = :indicatorId
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("stateId", stateId)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .getResultList();

        return result.stream()
                .findFirst()
                .map(TerritoryIndicatorValuesMapper::toDomain);
    }

    @Override
    public Optional<TerritoryIndicatorValues> findMunicipalityIndicator(
            Integer municipalityId,
            Integer indicatorId,
            Short analysisYear) {

        List<TerritoryIndicatorValuesEntity> result = em.createQuery(
                        """
                        SELECT t
                        FROM TerritoryIndicatorValuesEntity t
                        WHERE t.municipality.id = :municipalityId
                        AND t.indicator.id = :indicatorId
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("municipalityId", municipalityId)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .getResultList();

        return result.stream()
                .findFirst()
                .map(TerritoryIndicatorValuesMapper::toDomain);
    }
}
