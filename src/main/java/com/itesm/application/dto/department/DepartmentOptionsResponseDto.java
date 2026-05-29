package com.itesm.application.dto.department;

import java.util.List;

public class DepartmentOptionsResponseDto {

    private List<DepartmentOptionResponseDto> items;

    public DepartmentOptionsResponseDto() {
    }

    public DepartmentOptionsResponseDto(
            List<DepartmentOptionResponseDto> items
    ) {
        this.items = items;
    }

    public List<DepartmentOptionResponseDto> getItems() {
        return items;
    }
}