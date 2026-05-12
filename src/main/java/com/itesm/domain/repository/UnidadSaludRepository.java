package com.itesm.domain.repository;

import com.itesm.domain.models.unidadSalud.UnidadSaludDetalle;
import com.itesm.domain.models.unidadSalud.UnidadSaludResumen;

import java.util.List;
import java.util.Optional;

public interface UnidadSaludRepository {
    List<UnidadSaludResumen> findResumenByEstadoId(Integer idEstado);
    List<UnidadSaludResumen> findResumenByMunicipioId(Integer idMunicipio);
    Optional<UnidadSaludDetalle> findDetalleByIdAndPeriodoId(Integer idUnidad, Integer idPeriodo);
}
