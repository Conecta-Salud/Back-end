package com.itesm.domain.repository;

import com.itesm.domain.models.municipality.Municipality;

import java.util.List;

public interface MunicipalityRepository {
    List<Municipality> findAllMunicipalities();
    List<Municipality> findByStateId(Integer stateId);
}
