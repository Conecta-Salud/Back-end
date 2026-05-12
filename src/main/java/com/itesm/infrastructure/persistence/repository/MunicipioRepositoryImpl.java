package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.municipio.Municipio;
import com.itesm.domain.repository.MunicipioRepository;
import com.itesm.infrastructure.mapper.MunicipioMapper;
import com.itesm.infrastructure.persistence.entity.MunicipioEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MunicipioRepositoryImpl implements MunicipioRepository, PanacheRepositoryBase<MunicipioEntity, Integer> {

    @Override
    public List<Municipio> getAllMunicipios() {
        return listAll()
                .stream()
                .map(MunicipioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Municipio> getMunicipiosByEstadoId(Integer idEstado) {
        return find("estado.id", idEstado)
                .list()
                .stream()
                .map(MunicipioMapper::toDomain)
                .collect(Collectors.toList());
    }
}
