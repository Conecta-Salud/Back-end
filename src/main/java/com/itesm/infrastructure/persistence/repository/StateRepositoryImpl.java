package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.state.State;
import com.itesm.domain.repository.StateRepository;
import com.itesm.infrastructure.mapper.StateMapper;
import com.itesm.infrastructure.persistence.entity.StateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class StateRepositoryImpl implements StateRepository, PanacheRepositoryBase<StateEntity, Integer> {

    @Override
    public List<State> findAllStates() {
        return listAll()
                .stream()
                .map(StateMapper::toDomain)
                .collect(Collectors.toList());
    }
}
