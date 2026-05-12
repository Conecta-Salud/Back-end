package com.itesm.domain.repository;

import com.itesm.domain.models.municipio.Municipio;

import java.util.List;

public interface MunicipioRepository {
    List<Municipio> getAllMunicipios();
    List<Municipio> getMunicipiosByEstadoId(Integer idEstado);
}
