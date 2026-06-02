package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.Uploader.Establecimiento.Municipality;
import com.itesm.domain.models.Uploader.Establecimiento.State;
import com.itesm.domain.repository.Upload.Establecimiento.StateRepository;
import com.itesm.infrastructure.mapper.Uploader.Establecimientos.StateMapper;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

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
    @Override
    @Transactional
    public void save(List<State> states) {

        for (State state : states) {

            StateEntity entity = new StateEntity();

            entity.setId(entity.getId());
            entity.setName(entity.getName());
            entity.setInegiCode(entity.getInegiCode());

            persist(entity);

        }
    }

}
