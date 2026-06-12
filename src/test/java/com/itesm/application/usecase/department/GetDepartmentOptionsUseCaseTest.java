package com.itesm.application.usecase.department;

import com.itesm.application.dto.department.DepartmentOptionResponseDto;
import com.itesm.application.dto.department.DepartmentOptionsResponseDto;
import com.itesm.domain.models.department.Department;
import com.itesm.domain.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetDepartmentOptionsUseCaseTest {

    private DepartmentRepository departmentRepository;
    private GetDepartmentOptionsUseCase useCase;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        useCase = new GetDepartmentOptionsUseCase(departmentRepository);
    }

    @Test
    void execute_shouldReturnDepartmentOptions() {
        when(departmentRepository.findAllDepartments()).thenReturn(
                List.of(
                        new Department(1, "Compras"),
                        new Department(2, "Finanzas")
                )
        );

        DepartmentOptionsResponseDto result = useCase.execute();

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());

        DepartmentOptionResponseDto first = result.getItems().get(0);
        assertEquals(1, first.getId());
        assertEquals("Compras", first.getName());

        DepartmentOptionResponseDto second = result.getItems().get(1);
        assertEquals(2, second.getId());
        assertEquals("Finanzas", second.getName());
    }

    @Test
    void execute_shouldReturnEmptyWhenNoDepartments() {
        when(departmentRepository.findAllDepartments()).thenReturn(List.of());

        DepartmentOptionsResponseDto result = useCase.execute();

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}
