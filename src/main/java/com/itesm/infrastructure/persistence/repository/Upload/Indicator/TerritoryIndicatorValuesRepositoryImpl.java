package com.itesm.infrastructure.persistence.repository.Upload.Indicator;

import com.itesm.domain.models.Uploader.Auxiliar.TerritoryLevel;
import com.itesm.domain.models.Uploader.indicator.TerritoryIndicatorValues;
import com.itesm.domain.repository.Upload.Indicadores.TerritoryIndicatorValuesRepository;
import com.itesm.infrastructure.mapper.Uploader.Indicadores.TerritoryIndicatorValuesMapper;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.DataSourceEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.IndicatorsEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Indicadores.TerritoryIndicatorValuesEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TerritoryIndicatorValuesRepositoryImpl implements TerritoryIndicatorValuesRepository, PanacheRepositoryBase<TerritoryIndicatorValuesEntity, Long> {
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
                        WHERE t.territoryLevel = :territoryLevel
                        AND t.state.id = :stateId
                        AND t.indicator.id = :indicatorId
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("territoryLevel", TerritoryLevel.state)
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
                        WHERE t.territoryLevel = :territoryLevel
                        AND t.municipality.id = :municipalityId
                        AND t.indicator.id = :indicatorId
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("territoryLevel", TerritoryLevel.municipality)
                .setParameter("municipalityId", municipalityId)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .getResultList();

        return result.stream()
                .findFirst()
                .map(TerritoryIndicatorValuesMapper::toDomain);
    }

    @Override
    public Optional<TerritoryIndicatorValues> findCountryIndicator(
            Integer indicatorId,
            Short analysisYear) {

        List<TerritoryIndicatorValuesEntity> result = em.createQuery(
                        """
                        SELECT t
                        FROM TerritoryIndicatorValuesEntity t
                        WHERE t.territoryLevel = :territoryLevel
                        AND t.indicator.id = :indicatorId
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("territoryLevel", TerritoryLevel.country)
                .setParameter("indicatorId", indicatorId)
                .setParameter("analysisYear", analysisYear)
                .getResultList();

        return result.stream()
                .findFirst()
                .map(TerritoryIndicatorValuesMapper::toDomain);
    }

    @Override
    public Optional<TerritoryIndicatorValues> findStateIndicatorByCode(
            String indicatorCode,
            Integer stateId,
            Short analysisYear) {

        List<TerritoryIndicatorValuesEntity> result = em.createQuery(
                        """
                        SELECT t
                        FROM TerritoryIndicatorValuesEntity t
                        JOIN t.indicator i
                        WHERE t.territoryLevel = :territoryLevel
                        AND t.state.id = :stateId
                        AND i.code = :indicatorCode
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("territoryLevel", TerritoryLevel.state)
                .setParameter("stateId", stateId)
                .setParameter("indicatorCode", indicatorCode)
                .setParameter("analysisYear", analysisYear)
                .getResultList();

        return result.stream()
                .findFirst()
                .map(TerritoryIndicatorValuesMapper::toDomain);
    }

    @Override
    public Optional<TerritoryIndicatorValues> findMunicipalityIndicatorByCode(
            String indicatorCode,
            Integer municipalityId,
            Short analysisYear) {

        List<TerritoryIndicatorValuesEntity> result = em.createQuery(
                        """
                        SELECT t
                        FROM TerritoryIndicatorValuesEntity t
                        JOIN t.indicator i
                        WHERE t.territoryLevel = :territoryLevel
                        AND t.municipality.id = :municipalityId
                        AND i.code = :indicatorCode
                        AND t.analysisYear = :analysisYear
                        """,
                        TerritoryIndicatorValuesEntity.class
                )
                .setParameter("territoryLevel", TerritoryLevel.municipality)
                .setParameter("municipalityId", municipalityId)
                .setParameter("indicatorCode", indicatorCode)
                .setParameter("analysisYear", analysisYear)
                .getResultList();

        return result.stream()
                .findFirst()
                .map(TerritoryIndicatorValuesMapper::toDomain);
    }

    @Override
    @Transactional
    public void save(List<TerritoryIndicatorValues> territoryIndicatorValues) {
        for (TerritoryIndicatorValues territoryIndicatorValue : territoryIndicatorValues) {
            TerritoryIndicatorValuesEntity entity = new TerritoryIndicatorValuesEntity();

            entity.setTerritoryLevel(territoryIndicatorValue.getTerritoryLevel());

            if (territoryIndicatorValue.getStateId() != null) {
                StateEntity stateEntity = em.find(StateEntity.class, territoryIndicatorValue.getStateId());
                entity.setState(stateEntity);
            }

            if (territoryIndicatorValue.getMunicipalityId() != null) {
                MunicipalityEntity municipalityEntity = em.find(MunicipalityEntity.class, territoryIndicatorValue.getMunicipalityId());
                entity.setMunicipality(municipalityEntity);
            }

            IndicatorsEntity indicatorEntity = em.find(IndicatorsEntity.class, territoryIndicatorValue.getIndicatorId());
            entity.setIndicator(indicatorEntity);

            entity.setValue(territoryIndicatorValue.getValue());
            entity.setAnalysisYear(territoryIndicatorValue.getAnalysisYear());
            entity.setSourceYear(territoryIndicatorValue.getSourceYear());

            DataSourceEntity dataSourceEntity = em.find(DataSourceEntity.class, territoryIndicatorValue.getDataSourceId());
            entity.setDataSource(dataSourceEntity);

            entity.setSourceFile(territoryIndicatorValue.getSourceFile());
            entity.setAvailabilityStatus(territoryIndicatorValue.getAvailabilityStatus());
            entity.setMethodologyNote(territoryIndicatorValue.getMethodologyNote());

            persist(entity);
        }
    }
}
