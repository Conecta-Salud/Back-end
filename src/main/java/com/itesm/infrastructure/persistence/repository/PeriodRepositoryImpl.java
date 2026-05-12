package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.period.Period;
import com.itesm.domain.repository.PeriodRepository;
import com.itesm.infrastructure.mapper.PeriodMapper;
import com.itesm.infrastructure.persistence.entity.PeriodEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PeriodRepositoryImpl implements PeriodRepository, PanacheRepositoryBase<PeriodEntity, Integer> {

    @Override
    public List<Period> findAllPeriods() {
        return listAll()
                .stream()
                .map(PeriodMapper::toDomain)
                .collect(Collectors.toList());
    }
}