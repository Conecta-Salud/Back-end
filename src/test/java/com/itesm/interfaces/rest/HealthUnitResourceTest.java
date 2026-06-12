package com.itesm.interfaces.rest;

import com.itesm.application.dto.healthunit.HealthUnitDetailDto;
import com.itesm.application.dto.healthunit.HealthUnitSummaryDto;
import com.itesm.application.usecase.healthunit.GetHealthUnitDetailUseCase;
import com.itesm.application.usecase.healthunit.GetHealthUnitsUseCase;
import com.itesm.domain.models.healthunit.CareLevel;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class HealthUnitResourceTest {

    @InjectMock
    GetHealthUnitsUseCase getHealthUnitsUseCase;

    @InjectMock
    GetHealthUnitDetailUseCase getHealthUnitDetailUseCase;

    @Test
    void getHealthUnits_shouldReturnHealthUnitsList() {
        when(getHealthUnitsUseCase.execute(14, null)).thenReturn(
                List.of(
                        new HealthUnitSummaryDto(1, "123456", "Hospital Central", 1, "Guadalajara", 14, "Jalisco", "Secretaría de Salud", "Hospital", "General", CareLevel.primary),
                        new HealthUnitSummaryDto(2, "654321", "Clínica Sur", 2, "Zapopan", 14, "Jalisco", "Secretaría de Salud", "Clínica", "Especializada", CareLevel.secondary)
                )
        );

        given()
                .queryParam("stateId", 14)
                .when()
                .get("/health-units")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].name", equalTo("Hospital Central"))
                .body("[1].clues", equalTo("654321"));
    }

    @Test
    void getHealthUnitDetail_shouldReturnHealthUnitDetail() {
        HealthUnitDetailDto detail = new HealthUnitDetailDto(1, "123456", "Hospital Central", null, null, null, null);
        when(getHealthUnitDetailUseCase.execute(1, 1)).thenReturn(detail);

        given()
                .queryParam("periodId", 1)
                .when()
                .get("/health-units/1")
                .then()
                .statusCode(200);
    }
}
