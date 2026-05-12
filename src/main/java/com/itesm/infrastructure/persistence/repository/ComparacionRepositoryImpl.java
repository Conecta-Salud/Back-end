package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.comparacion.ComparacionTerritorio;
import com.itesm.domain.repository.ComparacionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ComparacionRepositoryImpl implements ComparacionRepository {

    @Inject
    EntityManager em;

    @Override
    public List<ComparacionTerritorio> compararEstados(Integer periodoId, List<Integer> idsEstados) {

        String sql = """
                SELECT
                    e.id,
                    e.nombre,
                    'estado' AS tipo,
                    p.id AS id_periodo,
                    p.anio,
                    ie.poblacion_total,
                    ie.porcentaje_60_mas,
                    ie.carencia_acceso_salud,
                    ie.situacion_pobreza_total,
                    COALESCE(unidades.total_unidades, 0) AS total_unidades,
                    COALESCE(personal.total_medicos, 0) AS total_medicos,
                    COALESCE(personal.total_enfermeras, 0) AS total_enfermeras,
                    COALESCE(infra.total_consultorios, 0) AS total_consultorios,
                    COALESCE(infra.total_camas_hospitalizacion, 0) AS total_camas_hospitalizacion
                FROM indicadores_estado ie
                JOIN estados e ON e.id = ie.id_estado
                JOIN periodos p ON p.id = ie.id_periodo

                LEFT JOIN (
                    SELECT
                        m.id_estado,
                        COUNT(DISTINCT us.id) AS total_unidades
                    FROM municipios m
                    JOIN unidades_salud us ON us.id_municipio = m.id
                    GROUP BY m.id_estado
                ) unidades ON unidades.id_estado = e.id

                LEFT JOIN (
                    SELECT
                        m.id_estado,
                        SUM(pu.total_medicos) AS total_medicos,
                        SUM(pu.total_enfermeras) AS total_enfermeras
                    FROM municipios m
                    JOIN unidades_salud us ON us.id_municipio = m.id
                    JOIN personal_unidad pu ON pu.id_unidad_salud = us.id
                    WHERE pu.id_periodo = :periodoId
                    GROUP BY m.id_estado
                ) personal ON personal.id_estado = e.id

                LEFT JOIN (
                    SELECT
                        m.id_estado,
                        SUM(CASE WHEN ti.nombre = 'total_consultorios' THEN iud.cantidad ELSE 0 END) AS total_consultorios,
                        SUM(CASE WHEN ti.nombre = 'total_camas_hospitalizacion' THEN iud.cantidad ELSE 0 END) AS total_camas_hospitalizacion
                    FROM municipios m
                    JOIN unidades_salud us ON us.id_municipio = m.id
                    JOIN infraestructura_unidad iu ON iu.id_unidad_salud = us.id
                    JOIN infraestructura_unidad_detalle iud ON iud.id_infraestructura_unidad = iu.id
                    JOIN tipos_infraestructura ti ON ti.id = iud.id_tipo_infraestructura
                    WHERE iu.id_periodo = :periodoId
                    GROUP BY m.id_estado
                ) infra ON infra.id_estado = e.id

                WHERE ie.id_periodo = :periodoId
                AND e.id IN (:idsEstados)
                ORDER BY e.nombre ASC
                """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodoId", periodoId);
        query.setParameterList("idsEstados", idsEstados);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapRowToComparacionTerritorio)
                .collect(Collectors.toList());
    }


    @Override
    public List<ComparacionTerritorio> compararMunicipios(Integer periodoId, List<Integer> idsMunicipios) {

        String sql = """
            SELECT
                m.id,
                m.nombre,
                'municipio' AS tipo,
                p.id AS id_periodo,
                p.anio,
                im.poblacion_total,
                im.porcentaje_60_mas,
                im.carencia_acceso_salud,
                im.situacion_pobreza_total,
                COALESCE(unidades.total_unidades, 0) AS total_unidades,
                COALESCE(personal.total_medicos, 0) AS total_medicos,
                COALESCE(personal.total_enfermeras, 0) AS total_enfermeras,
                COALESCE(infra.total_consultorios, 0) AS total_consultorios,
                COALESCE(infra.total_camas_hospitalizacion, 0) AS total_camas_hospitalizacion
            FROM indicadores_municipio im
            JOIN municipios m ON m.id = im.id_municipio
            JOIN periodos p ON p.id = im.id_periodo

            LEFT JOIN (
                SELECT
                    us.id_municipio,
                    COUNT(DISTINCT us.id) AS total_unidades
                FROM unidades_salud us
                GROUP BY us.id_municipio
            ) unidades ON unidades.id_municipio = m.id

            LEFT JOIN (
                SELECT
                    us.id_municipio,
                    SUM(pu.total_medicos) AS total_medicos,
                    SUM(pu.total_enfermeras) AS total_enfermeras
                FROM unidades_salud us
                JOIN personal_unidad pu ON pu.id_unidad_salud = us.id
                WHERE pu.id_periodo = :periodoId
                GROUP BY us.id_municipio
            ) personal ON personal.id_municipio = m.id

            LEFT JOIN (
                SELECT
                    us.id_municipio,
                    SUM(CASE 
                        WHEN ti.nombre = 'total_consultorios' 
                        THEN iud.cantidad ELSE 0 END
                    ) AS total_consultorios,
                    SUM(CASE 
                        WHEN ti.nombre = 'total_camas_hospitalizacion' 
                        THEN iud.cantidad ELSE 0 END
                    ) AS total_camas_hospitalizacion
                FROM unidades_salud us
                JOIN infraestructura_unidad iu ON iu.id_unidad_salud = us.id
                JOIN infraestructura_unidad_detalle iud ON iud.id_infraestructura_unidad = iu.id
                JOIN tipos_infraestructura ti ON ti.id = iud.id_tipo_infraestructura
                WHERE iu.id_periodo = :periodoId
                GROUP BY us.id_municipio
            ) infra ON infra.id_municipio = m.id

            WHERE im.id_periodo = :periodoId
            AND m.id IN (:idsMunicipios)
            ORDER BY m.nombre ASC
            """;

        NativeQuery<?> query = em.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("periodoId", periodoId);
        query.setParameterList("idsMunicipios", idsMunicipios);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        return rows.stream()
                .map(this::mapRowToComparacionTerritorio)
                .collect(Collectors.toList());
    }

    private ComparacionTerritorio mapRowToComparacionTerritorio(Object[] row) {
        return new ComparacionTerritorio(
                toInteger(row[0]),
                (String) row[1],
                (String) row[2],
                toInteger(row[3]),
                toInteger(row[4]),
                toBigInteger(row[5]),
                toBigDecimal(row[6]),
                toBigInteger(row[7]),
                toBigInteger(row[8]),
                toLong(row[9]),
                toLong(row[10]),
                toLong(row[11]),
                toLong(row[12]),
                toLong(row[13])
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

    private BigInteger toBigInteger(Object value) {
        if (value == null) return BigInteger.ZERO;
        if (value instanceof BigInteger) return (BigInteger) value;
        if (value instanceof BigDecimal) return ((BigDecimal) value).toBigInteger();
        if (value instanceof Long) return BigInteger.valueOf((Long) value);
        if (value instanceof Integer) return BigInteger.valueOf((Integer) value);
        return new BigInteger(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof BigInteger) return new BigDecimal((BigInteger) value);
        return new BigDecimal(value.toString());
    }
}
