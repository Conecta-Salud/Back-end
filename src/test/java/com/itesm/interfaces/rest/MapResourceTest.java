package com.itesm.interfaces.rest;

import com.itesm.application.dto.map.MapIndicatorResponseDto;
import com.itesm.application.usecase.map.GetMunicipalityMapUseCase;
import com.itesm.application.usecase.map.GetStateMapUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class MapResourceTest {

    @InjectMock
    GetStateMapUseCase getStateMapUseCase;

    @InjectMock
    GetMunicipalityMapUseCase getMunicipalityMapUseCase;

    @Test
    void getStateMap_shouldReturnStateMapIndicators() {
        when(getStateMapUseCase.execute("medical_coverage", 2024)).thenReturn(
                List.of(new MapIndicatorResponseDto("14", "Jalisco", BigDecimal.valueOf(2.8), "good", "green", 2024, "%", "available", "note", "source"))
        );

        given()
                .queryParam("indicator", "medical_coverage")
                .queryParam("year", 2024)
                .when()
                .get("/api/v1/map/states")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].name", equalTo("Jalisco"));
    }

    @Test
    void getMunicipalityMap_shouldReturnMunicipalityMapIndicators() {
        when(getMunicipalityMapUseCase.execute("14", "medical_coverage", 2024)).thenReturn(
                List.of(new MapIndicatorResponseDto("14001", "Guadalajara", BigDecimal.valueOf(2.9), "good", "green", 2024, "%", "available", "note", "source"))
        );

        given()
                .queryParam("stateCode", "14")
                .queryParam("indicator", "medical_coverage")
                .queryParam("year", 2024)
                .when()
                .get("/api/v1/map/municipalities")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].code", equalTo("14001"));
    }
}
