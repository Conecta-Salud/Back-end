package com.itesm.domain.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.State;

import java.util.List;
import java.util.Optional;

public interface StateRepository {
    List<State> findAllStates();
    Optional<State> findStateById(Integer stateId);
    void save(List<State> states);
}
