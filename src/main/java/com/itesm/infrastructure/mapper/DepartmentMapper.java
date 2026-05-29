package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.department.Department;
import com.itesm.infrastructure.persistence.entity.DepartmentEntity;

public class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static Department toDomain(
            DepartmentEntity entity
    ) {
        return new Department(
                entity.getId(),
                entity.getName()
        );
    }
}