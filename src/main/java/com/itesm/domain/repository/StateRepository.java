package com.itesm.domain.repository;

import com.itesm.domain.models.state.State;

import java.util.List;

public interface StateRepository {
    List<State> findAllStates();
}
