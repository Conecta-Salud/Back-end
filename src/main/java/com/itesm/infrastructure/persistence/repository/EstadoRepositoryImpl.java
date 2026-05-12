package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.estado.Estado;
import com.itesm.domain.repository.EstadoRepository;
import com.itesm.infrastructure.mapper.EstadoMapper;
import com.itesm.infrastructure.persistence.entity.EstadoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EstadoRepositoryImpl implements EstadoRepository, PanacheRepositoryBase<EstadoEntity, Integer> {

    @Override
    public List<Estado> getAllEstados() {
        return listAll()
                .stream()
                .map(EstadoMapper::toDomain)
                .collect(Collectors.toList());
    }
}
