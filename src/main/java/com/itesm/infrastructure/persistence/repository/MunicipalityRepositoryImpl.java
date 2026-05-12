package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.municipality.Municipality;
import com.itesm.domain.repository.MunicipalityRepository;
import com.itesm.infrastructure.mapper.MunicipalityMapper;
import com.itesm.infrastructure.persistence.entity.MunicipalityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MunicipalityRepositoryImpl implements MunicipalityRepository, PanacheRepositoryBase<MunicipalityEntity, Integer> {

    @Override
    public List<Municipality> findAllMunicipalities() {
        return listAll()
                .stream()
                .map(MunicipalityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Municipality> findByStateId(Integer stateId) {
        return find("state.id", stateId)
                .list()
                .stream()
                .map(MunicipalityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
