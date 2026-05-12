package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.indicador.IndicadorEstado;
import com.itesm.domain.repository.IndicadorEstadoRepository;
import com.itesm.infrastructure.mapper.IndicadorEstadoMapper;
import com.itesm.infrastructure.persistence.entity.IndicadorEstadoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class IndicadorEstadoRepositoryImpl implements IndicadorEstadoRepository, PanacheRepositoryBase<IndicadorEstadoEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public Optional<IndicadorEstado> findByEstadoIdAndPeriodoId(Integer idEstado, Integer idPeriodo) {
        EntityGraph<?> graph = em.getEntityGraph("IndicadorEstado.withEstadoAndPeriodo");

        List<IndicadorEstadoEntity> result = em.createQuery(
                        "SELECT i FROM IndicadorEstadoEntity i " +
                                "WHERE i.estado.id = :idEstado " +
                                "AND i.periodo.id = :idPeriodo",
                        IndicadorEstadoEntity.class
                )
                .setParameter("idEstado", idEstado)
                .setParameter("idPeriodo", idPeriodo)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(IndicadorEstadoMapper.toDomain(result.get(0)));
    }
}
