package com.itesm.domain.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.Municipality;

import java.util.List;

public interface MunicipalityRepository {
    List<Municipality> findAllMunicipalities();
    List<Municipality> findByStateId(Integer stateId);
    void save(List<Municipality> municipalities);
}
