package com.itesm.domain.repository;

import com.itesm.domain.models.indicador.IndicadorMunicipio;

import java.util.Optional;

public interface IndicadorMunicipioRepository {
    Optional<IndicadorMunicipio> findByMunicipioIdAndPeriodoId(Integer idMunicipio, Integer idPeriodo);
}
