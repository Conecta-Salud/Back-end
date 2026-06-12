package com.itesm.interfaces.rest;

import com.itesm.application.dto.department.DepartmentOptionResponseDto;
import com.itesm.application.dto.department.DepartmentOptionsResponseDto;
import com.itesm.application.usecase.department.GetDepartmentOptionsUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class DepartmentResourceTest {

    @InjectMock
    GetDepartmentOptionsUseCase getDepartmentOptionsUseCase;

    @Test
    void getDepartments_shouldReturnDepartmentOptions() {
        when(getDepartmentOptionsUseCase.execute()).thenReturn(
                new DepartmentOptionsResponseDto(
                        List.of(
                                new DepartmentOptionResponseDto(1, "Compras"),
                                new DepartmentOptionResponseDto(2, "Finanzas")
                        )
                )
        );

        given()
                .when()
                .get("/departments")
                .then()
                .statusCode(200)
                .body("items", hasSize(2))
                .body("items[0].id", equalTo(1))
                .body("items[0].name", equalTo("Compras"))
                .body("items[1].id", equalTo(2))
                .body("items[1].name", equalTo("Finanzas"));
    }
}
