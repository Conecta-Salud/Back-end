package com.itesm.domain.repository;

import com.itesm.domain.models.dashboard.DashboardSalud;

import java.util.Optional;

public interface DashboardSaludRepository {

    Optional<DashboardSalud> findSaludByEstadoAndPeriodo(Integer idEstado, Integer idPeriodo);

    Optional<DashboardSalud> findSaludByMunicipioAndPeriodo(Integer idMunicipio, Integer idPeriodo);
}
