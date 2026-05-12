package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.dashboard.DashboardSalud;
import com.itesm.domain.repository.DashboardSaludRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@ApplicationScoped
public class DashboardSaludRepositoryImpl implements DashboardSaludRepository {

    @Inject
    EntityManager em;

    @Override
    public Optional<DashboardSalud> findSaludByEstadoAndPeriodo(Integer idEstado, Integer idPeriodo) {

        Object[] result = (Object[]) em.createNativeQuery("""
                SELECT 
                    e.id AS id_territorio,
                    e.nombre AS nombre_territorio,
                    'estado' AS tipo_territorio,
                    p.id AS id_periodo,
                    p.anio AS anio,
                    COUNT(DISTINCT us.id) AS total_unidades,
                    COALESCE(SUM(pu.total_medicos), 0) AS total_medicos,
                    COALESCE(SUM(pu.total_enfermeras), 0) AS total_enfermeras,
                    COALESCE(SUM(CASE 
                        WHEN ti.nombre = 'total_consultorios' 
                        THEN iud.cantidad ELSE 0 END), 0) AS total_consultorios,
                    COALESCE(SUM(CASE 
                        WHEN ti.nombre = 'total_camas_hospitalizacion' 
                        THEN iud.cantidad ELSE 0 END), 0) AS total_camas_hospitalizacion
                FROM estados e
                JOIN municipios m ON m.id_estado = e.id
                JOIN unidades_salud us ON us.id_municipio = m.id
                JOIN periodos p ON p.id = :idPeriodo
                LEFT JOIN personal_unidad pu 
                    ON pu.id_unidad_salud = us.id 
                    AND pu.id_periodo = p.id
                LEFT JOIN infraestructura_unidad iu 
                    ON iu.id_unidad_salud = us.id 
                    AND iu.id_periodo = p.id
                LEFT JOIN infraestructura_unidad_detalle iud 
                    ON iud.id_infraestructura_unidad = iu.id
                LEFT JOIN tipos_infraestructura ti 
                    ON ti.id = iud.id_tipo_infraestructura
                WHERE e.id = :idEstado
                GROUP BY e.id, e.nombre, p.id, p.anio
                """)
                .setParameter("idEstado", idEstado)
                .setParameter("idPeriodo", idPeriodo)
                .getSingleResult();

        return Optional.of(mapToDashboardSalud(result));
    }

    @Override
    public Optional<DashboardSalud> findSaludByMunicipioAndPeriodo(Integer idMunicipio, Integer idPeriodo) {

        Object[] result = (Object[]) em.createNativeQuery("""
                SELECT 
                    m.id AS id_territorio,
                    m.nombre AS nombre_territorio,
                    'municipio' AS tipo_territorio,
                    p.id AS id_periodo,
                    p.anio AS anio,
                    COUNT(DISTINCT us.id) AS total_unidades,
                    COALESCE(SUM(pu.total_medicos), 0) AS total_medicos,
                    COALESCE(SUM(pu.total_enfermeras), 0) AS total_enfermeras,
                    COALESCE(SUM(CASE 
                        WHEN ti.nombre = 'total_consultorios' 
                        THEN iud.cantidad ELSE 0 END), 0) AS total_consultorios,
                    COALESCE(SUM(CASE 
                        WHEN ti.nombre = 'total_camas_hospitalizacion' 
                        THEN iud.cantidad ELSE 0 END), 0) AS total_camas_hospitalizacion
                FROM municipios m
                JOIN unidades_salud us ON us.id_municipio = m.id
                JOIN periodos p ON p.id = :idPeriodo
                LEFT JOIN personal_unidad pu 
                    ON pu.id_unidad_salud = us.id 
                    AND pu.id_periodo = p.id
                LEFT JOIN infraestructura_unidad iu 
                    ON iu.id_unidad_salud = us.id 
                    AND iu.id_periodo = p.id
                LEFT JOIN infraestructura_unidad_detalle iud 
                    ON iud.id_infraestructura_unidad = iu.id
                LEFT JOIN tipos_infraestructura ti 
                    ON ti.id = iud.id_tipo_infraestructura
                WHERE m.id = :idMunicipio
                GROUP BY m.id, m.nombre, p.id, p.anio
                """)
                .setParameter("idMunicipio", idMunicipio)
                .setParameter("idPeriodo", idPeriodo)
                .getSingleResult();

        return Optional.of(mapToDashboardSalud(result));
    }

    private DashboardSalud mapToDashboardSalud(Object[] row) {
        return new DashboardSalud(
                toInteger(row[0]),
                (String) row[1],
                (String) row[2],
                toInteger(row[3]),
                toInteger(row[4]),
                toLong(row[5]),
                toLong(row[6]),
                toLong(row[7]),
                toLong(row[8]),
                toLong(row[9])
        );
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Short) return ((Short) value).intValue();
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).intValue();
        return Integer.valueOf(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        return Long.valueOf(value.toString());
    }
}