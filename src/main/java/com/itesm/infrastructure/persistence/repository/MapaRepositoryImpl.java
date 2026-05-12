package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.mapa.IndicadorMapaTipo;
import com.itesm.domain.models.mapa.MapaIndicador;
import com.itesm.domain.repository.MapaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MapaRepositoryImpl implements MapaRepository {

    @Inject
    EntityManager em;

    @Override
    public boolean existsPeriodoByAnio(Integer anio) {
        Long count = em.createQuery(
                        "SELECT COUNT(p) FROM PeriodoEntity p WHERE p.anio = :anio",
                        Long.class
                )
                .setParameter("anio", anio)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsEstadoByClave(String claveEstado) {
        Long count = em.createQuery(
                        "SELECT COUNT(e) FROM EstadoEntity e WHERE e.claveInegi = :claveEstado",
                        Long.class
                )
                .setParameter("claveEstado", claveEstado)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<MapaIndicador> findEstadosIndicador(IndicadorMapaTipo indicador, Integer anio) {
        String sql = switch (indicador) {
            case COBERTURA_MEDICA -> sqlEstadosCoberturaMedica();
            case CAMAS_HOSPITALARIAS -> sqlEstadosCamasHospitalarias();
            case CARENCIA_ACCESO_SALUD -> sqlEstadosCarenciaAccesoSalud();
        };

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("anio", anio)
                .getResultList();

        return rows.stream()
                .map(row -> mapRow(row, indicador))
                .collect(Collectors.toList());
    }

    @Override
    public List<MapaIndicador> findMunicipiosIndicador(String claveEstado, IndicadorMapaTipo indicador, Integer anio) {
        String sql = switch (indicador) {
            case COBERTURA_MEDICA -> sqlMunicipiosCoberturaMedica();
            case CAMAS_HOSPITALARIAS -> sqlMunicipiosCamasHospitalarias();
            case CARENCIA_ACCESO_SALUD -> sqlMunicipiosCarenciaAccesoSalud();
        };

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("claveEstado", claveEstado)
                .setParameter("anio", anio)
                .getResultList();

        return rows.stream()
                .map(row -> mapRow(row, indicador))
                .collect(Collectors.toList());
    }

    // ESTADOS

    private String sqlEstadosCoberturaMedica() {
        return """
                SELECT
                    e.clave_inegi AS code,
                    e.nombre AS name,
                    ROUND((COALESCE(SUM(pu.total_medicos), 0) / NULLIF(ie.poblacion_total, 0)) * 1000, 2) AS value
                FROM estados e
                JOIN indicadores_estado ie ON ie.id_estado = e.id
                JOIN periodos p ON p.id = ie.id_periodo
                LEFT JOIN municipios m ON m.id_estado = e.id
                LEFT JOIN unidades_salud us ON us.id_municipio = m.id
                LEFT JOIN personal_unidad pu
                    ON pu.id_unidad_salud = us.id
                    AND pu.id_periodo = p.id
                WHERE p.anio = :anio
                GROUP BY e.id, e.clave_inegi, e.nombre, ie.poblacion_total
                ORDER BY e.nombre ASC
                """;
    }

    private String sqlEstadosCamasHospitalarias() {
        return """
            SELECT
                e.clave_inegi AS code,
                e.nombre AS name,
                ROUND((COALESCE(SUM(CASE 
                    WHEN ti.nombre = 'total_camas_hospitalizacion' 
                    THEN iud.cantidad ELSE 0 END), 0) / NULLIF(ie.poblacion_total, 0)) * 1000, 2) AS value
            FROM estados e
            JOIN indicadores_estado ie ON ie.id_estado = e.id
            JOIN periodos p ON p.id = ie.id_periodo
            LEFT JOIN municipios m ON m.id_estado = e.id
            LEFT JOIN unidades_salud us ON us.id_municipio = m.id
            LEFT JOIN infraestructura_unidad iu
                ON iu.id_unidad_salud = us.id
                AND iu.id_periodo = p.id
            LEFT JOIN infraestructura_unidad_detalle iud
                ON iud.id_infraestructura_unidad = iu.id
            LEFT JOIN tipos_infraestructura ti
                ON ti.id = iud.id_tipo_infraestructura
            WHERE p.anio = :anio
            GROUP BY e.id, e.clave_inegi, e.nombre, ie.poblacion_total
            ORDER BY e.nombre ASC
            """;
    }

    private String sqlEstadosCarenciaAccesoSalud() {
        return """
                SELECT
                    e.clave_inegi AS code,
                    e.nombre AS name,
                    ROUND((ie.carencia_acceso_salud / NULLIF(ie.poblacion_total, 0)) * 100, 2) AS value
                FROM estados e
                JOIN indicadores_estado ie ON ie.id_estado = e.id
                JOIN periodos p ON p.id = ie.id_periodo
                WHERE p.anio = :anio
                ORDER BY e.nombre ASC
                """;
    }

    // MUNICIPIOS

    private String sqlMunicipiosCoberturaMedica() {
        return """
            SELECT
                m.clave_inegi AS code,
                m.nombre AS name,
                ROUND((COALESCE(SUM(pu.total_medicos), 0) / NULLIF(im.poblacion_total, 0)) * 1000, 2) AS value
            FROM municipios m
            JOIN estados e ON e.id = m.id_estado
            JOIN indicadores_municipio im ON im.id_municipio = m.id
            JOIN periodos p ON p.id = im.id_periodo
            LEFT JOIN unidades_salud us ON us.id_municipio = m.id
            LEFT JOIN personal_unidad pu
                ON pu.id_unidad_salud = us.id
                AND pu.id_periodo = p.id
            WHERE e.clave_inegi = :claveEstado
            AND p.anio = :anio
            GROUP BY m.id, m.clave_inegi, m.nombre, im.poblacion_total
            ORDER BY m.nombre ASC
            """;
    }

    private String sqlMunicipiosCamasHospitalarias() {
        return """
            SELECT
                m.clave_inegi AS code,
                m.nombre AS name,
                ROUND((COALESCE(SUM(CASE
                    WHEN ti.nombre = 'total_camas_hospitalizacion'
                    THEN iud.cantidad ELSE 0 END), 0) / NULLIF(im.poblacion_total, 0)) * 1000, 2) AS value
            FROM municipios m
            JOIN estados e ON e.id = m.id_estado
            JOIN indicadores_municipio im ON im.id_municipio = m.id
            JOIN periodos p ON p.id = im.id_periodo
            LEFT JOIN unidades_salud us ON us.id_municipio = m.id
            LEFT JOIN infraestructura_unidad iu
                ON iu.id_unidad_salud = us.id
                AND iu.id_periodo = p.id
            LEFT JOIN infraestructura_unidad_detalle iud
                ON iud.id_infraestructura_unidad = iu.id
            LEFT JOIN tipos_infraestructura ti
                ON ti.id = iud.id_tipo_infraestructura
            WHERE e.clave_inegi = :claveEstado
            AND p.anio = :anio
            GROUP BY m.id, m.clave_inegi, m.nombre, im.poblacion_total
            ORDER BY m.nombre ASC
            """;
    }

    private String sqlMunicipiosCarenciaAccesoSalud() {
        return """
            SELECT
                m.clave_inegi AS code,
                m.nombre AS name,
                ROUND((im.carencia_acceso_salud / NULLIF(im.poblacion_total, 0)) * 100, 2) AS value
            FROM municipios m
            JOIN estados e ON e.id = m.id_estado
            JOIN indicadores_municipio im ON im.id_municipio = m.id
            JOIN periodos p ON p.id = im.id_periodo
            WHERE e.clave_inegi = :claveEstado
            AND p.anio = :anio
            ORDER BY m.nombre ASC
            """;
    }

    private MapaIndicador mapRow(Object[] row, IndicadorMapaTipo indicador) {
        return new MapaIndicador(
                (String) row[0],
                (String) row[1],
                toBigDecimal(row[2]),
                indicador
        );
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }

}
