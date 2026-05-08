package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.unidadSalud.InfraestructuraUnidadResumen;
import com.itesm.domain.models.unidadSalud.PersonalUnidadResumen;
import com.itesm.domain.models.unidadSalud.UnidadSaludDetalle;
import com.itesm.domain.models.unidadSalud.UnidadSaludResumen;
import com.itesm.domain.repository.UnidadSaludRepository;
import com.itesm.infrastructure.mapper.UnidadSaludMapper;
import com.itesm.infrastructure.persistence.entity.UnidadSaludEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UnidadSaludRepositoryImpl implements UnidadSaludRepository, PanacheRepositoryBase<UnidadSaludEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public List<UnidadSaludResumen> findResumenByEstadoId(Integer idEstado) {
        EntityGraph<?> graph = em.getEntityGraph("UnidadSalud.summary");

        List<UnidadSaludEntity> result = em.createQuery(
                        "SELECT u FROM UnidadSaludEntity u " +
                                "WHERE u.municipio.estado.id = :idEstado " +
                                "ORDER BY u.nombre ASC",
                        UnidadSaludEntity.class
                )
                .setParameter("idEstado", idEstado)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        return result.stream()
                .map(UnidadSaludMapper::toResumenDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnidadSaludResumen> findResumenByMunicipioId(Integer idMunicipio) {
        EntityGraph<?> graph = em.getEntityGraph("UnidadSalud.summary");

        List<UnidadSaludEntity> result = em.createQuery(
                        "SELECT u FROM UnidadSaludEntity u " +
                                "WHERE u.municipio.id = :idMunicipio " +
                                "ORDER BY u.nombre ASC",
                        UnidadSaludEntity.class
                )
                .setParameter("idMunicipio", idMunicipio)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        return result.stream()
                .map(UnidadSaludMapper::toResumenDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UnidadSaludDetalle> findDetalleByIdAndPeriodoId(Integer idUnidad, Integer idPeriodo) {
        EntityGraph<?> graph = em.getEntityGraph("UnidadSalud.summary");

        List<UnidadSaludEntity> result = em.createQuery(
                        "SELECT u FROM UnidadSaludEntity u WHERE u.id = :idUnidad",
                        UnidadSaludEntity.class
                )
                .setParameter("idUnidad", idUnidad)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        UnidadSaludEntity unidad = result.get(0);

        PersonalUnidadResumen personal = findPersonalByUnidadAndPeriodo(idUnidad, idPeriodo);
        InfraestructuraUnidadResumen infraestructura = findInfraestructuraByUnidadAndPeriodo(idUnidad, idPeriodo);

        return Optional.of(
                UnidadSaludMapper.toDetalleDomain(unidad, personal, infraestructura)
        );
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof java.math.BigInteger) return ((java.math.BigInteger) value).longValue();
        if (value instanceof java.math.BigDecimal) return ((java.math.BigDecimal) value).longValue();
        return Long.valueOf(value.toString());
    }

    private PersonalUnidadResumen findPersonalByUnidadAndPeriodo(Integer idUnidad, Integer idPeriodo) {
        Object[] row = (Object[]) em.createNativeQuery("""
            SELECT 
                COALESCE(pu.total_medicos, 0) AS total_medicos,
                COALESCE(pu.total_enfermeras, 0) AS total_enfermeras
            FROM unidades_salud us
            LEFT JOIN personal_unidad pu 
                ON pu.id_unidad_salud = us.id 
                AND pu.id_periodo = :idPeriodo
            WHERE us.id = :idUnidad
            """)
                .setParameter("idUnidad", idUnidad)
                .setParameter("idPeriodo", idPeriodo)
                .getSingleResult();

        return new PersonalUnidadResumen(
                toLong(row[0]),
                toLong(row[1])
        );
    }

    private InfraestructuraUnidadResumen findInfraestructuraByUnidadAndPeriodo(Integer idUnidad, Integer idPeriodo) {
        Object[] row = (Object[]) em.createNativeQuery("""
            SELECT 
                COALESCE(SUM(CASE 
                    WHEN ti.nombre = 'total_consultorios' 
                    THEN iud.cantidad ELSE 0 END), 0) AS total_consultorios,
                COALESCE(SUM(CASE 
                    WHEN ti.nombre = 'total_camas_hospitalizacion' 
                    THEN iud.cantidad ELSE 0 END), 0) AS total_camas_hospitalizacion
            FROM unidades_salud us
            LEFT JOIN infraestructura_unidad iu 
                ON iu.id_unidad_salud = us.id 
                AND iu.id_periodo = :idPeriodo
            LEFT JOIN infraestructura_unidad_detalle iud 
                ON iud.id_infraestructura_unidad = iu.id
            LEFT JOIN tipos_infraestructura ti 
                ON ti.id = iud.id_tipo_infraestructura
            WHERE us.id = :idUnidad
            """)
                .setParameter("idUnidad", idUnidad)
                .setParameter("idPeriodo", idPeriodo)
                .getSingleResult();

        return new InfraestructuraUnidadResumen(
                toLong(row[0]),
                toLong(row[1])
        );
    }

}
