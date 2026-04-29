package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.periodo.Periodo;
import com.itesm.domain.repository.PeriodoRepository;
import com.itesm.infrastructure.mapper.PeriodoMapper;
import com.itesm.infrastructure.persistence.entity.PeriodoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PeriodoRepositoryImpl implements PeriodoRepository, PanacheRepositoryBase<PeriodoEntity, Integer> {

    @Override
    public List<Periodo> getAllPeriodos() {
        return listAll()
                .stream()
                .map(PeriodoMapper::toDomain)
                .collect(Collectors.toList());
    }
}