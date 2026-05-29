package com.itesm.application.usecase.department;

import com.itesm.application.dto.department.DepartmentOptionResponseDto;
import com.itesm.application.dto.department.DepartmentOptionsResponseDto;
import com.itesm.domain.repository.DepartmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetDepartmentOptionsUseCase {

    private final DepartmentRepository departmentRepository;

    @Inject
    public GetDepartmentOptionsUseCase(
            DepartmentRepository departmentRepository
    ) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentOptionsResponseDto execute() {

        List<DepartmentOptionResponseDto> items =
                departmentRepository.findAllDepartments()
                        .stream()
                        .map(department ->
                                new DepartmentOptionResponseDto(
                                        department.getId(),
                                        department.getName()
                                )
                        )
                        .toList();

        return new DepartmentOptionsResponseDto(items);
    }
}