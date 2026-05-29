package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.department.Department;
import com.itesm.domain.repository.DepartmentRepository;
import com.itesm.infrastructure.mapper.DepartmentMapper;
import com.itesm.infrastructure.persistence.entity.DepartmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DepartmentRepositoryImpl
        implements DepartmentRepository,
        PanacheRepositoryBase<DepartmentEntity, Integer> {

    @Override
    public List<Department> findAllDepartments() {

        return listAll()
                .stream()
                .map(DepartmentMapper::toDomain)
                .collect(Collectors.toList());
    }
}