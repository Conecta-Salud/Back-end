package com.itesm.domain.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.Municipality;

import java.util.List;
import java.util.Optional;

public interface MunicipalityRepository {
    List<Municipality> findAllMunicipalities();
    List<Municipality> findByStateId(Integer stateId);
    Optional<Municipality> findMunicipalityById(Integer municipalityId);
    void save(List<Municipality> municipalities);
}
