package com.itesm.domain.repository;

import com.itesm.domain.models.department.Department;

import java.util.List;

public interface DepartmentRepository {

    List<Department> findAllDepartments();
}