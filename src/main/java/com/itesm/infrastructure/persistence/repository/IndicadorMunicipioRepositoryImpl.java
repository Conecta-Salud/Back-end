package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicador.IndicadorMunicipio;
import com.itesm.domain.repository.IndicadorMunicipioRepository;
import com.itesm.infrastructure.mapper.IndicadorMunicipioMapper;
import com.itesm.infrastructure.persistence.entity.IndicadorMunicipioEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class IndicadorMunicipioRepositoryImpl implements IndicadorMunicipioRepository, PanacheRepositoryBase<IndicadorMunicipioEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Optional<IndicadorMunicipio> findByMunicipioIdAndPeriodoId(Integer idMunicipio, Integer idPeriodo) {
        EntityGraph<?> graph = em.getEntityGraph("IndicadorMunicipio.withMunicipioEstadoAndPeriodo");

        List<IndicadorMunicipioEntity> result = em.createQuery(
                        "SELECT i FROM IndicadorMunicipioEntity i " +
                                "WHERE i.municipio.id = :idMunicipio " +
                                "AND i.periodo.id = :idPeriodo",
                        IndicadorMunicipioEntity.class
                )
                .setParameter("idMunicipio", idMunicipio)
                .setParameter("idPeriodo", idPeriodo)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(IndicadorMunicipioMapper.toDomain(result.get(0)));
    }
}
