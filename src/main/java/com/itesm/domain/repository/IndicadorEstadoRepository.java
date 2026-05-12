package com.itesm.domain.repository;

import com.itesm.domain.models.indicador.IndicadorEstado;

import java.util.Optional;

public interface IndicadorEstadoRepository {
    Optional<IndicadorEstado> findByEstadoIdAndPeriodoId(Integer idEstado, Integer idPeriodo);
}
