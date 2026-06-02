package com.itesm.domain.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.State;

import java.util.List;

public interface StateRepository {
    List<State> findAllStates();
    void save(List<State> states);
}
