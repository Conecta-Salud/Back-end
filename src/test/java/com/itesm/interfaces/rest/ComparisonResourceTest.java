package com.itesm.interfaces.rest;

import com.itesm.application.dto.comparison.TerritoryComparisonDto;
import com.itesm.application.usecase.comparison.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class ComparisonResourceTest {

    @InjectMock
    CompareStatesUseCase compareStatesUseCase;

    @InjectMock
    CompareMunicipalitiesUseCase compareMunicipalitiesUseCase;

    @InjectMock
    CompareMunicipalitiesByCodesUseCase compareMunicipalitiesByCodesUseCase;

    @InjectMock
    CompareStatesByCodesUseCase compareStatesByCodesUseCase;

    @Test
    void compareStates_shouldReturnComparisonResults() {
        when(compareStatesUseCase.execute(1, List.of(14, 19))).thenReturn(
                List.of(new TerritoryComparisonDto(14, "Jalisco", "STATE", null, null, null))
        );

        given()
                .queryParam("periodId", 1)
                .queryParam("stateIds", List.of(14, 19))
                .when()
                .get("/comparison/states")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    void compareMunicipalities_shouldReturnComparisonResults() {
        when(compareMunicipalitiesUseCase.execute(1, List.of(1, 2))).thenReturn(
                List.of(new TerritoryComparisonDto(1, "Guadalajara", "MUNICIPALITY", null, null, null))
        );

        given()
                .queryParam("periodId", 1)
                .queryParam("municipalityIds", List.of(1, 2))
                .when()
                .get("/comparison/municipalities")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    void compareMunicipalitiesByCodes_shouldReturnComparisonResults() {
        when(compareMunicipalitiesByCodesUseCase.execute(1, List.of("14001", "14002"))).thenReturn(
                List.of(new TerritoryComparisonDto(1, "Guadalajara", "MUNICIPALITY", null, null, null))
        );

        given()
                .queryParam("periodId", 1)
                .queryParam("municipalityCodes", List.of("14001", "14002"))
                .when()
                .get("/comparison/municipalities/by-codes")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    void compareStatesByCodes_shouldReturnComparisonResults() {
        when(compareStatesByCodesUseCase.execute(1, List.of("14", "19"))).thenReturn(
                List.of(new TerritoryComparisonDto(14, "Jalisco", "STATE", null, null, null))
        );

        given()
                .queryParam("periodId", 1)
                .queryParam("stateCodes", List.of("14", "19"))
                .when()
                .get("/comparison/states/by-codes")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }
}
