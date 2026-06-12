package com.itesm.interfaces.rest;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.application.usecase.municipality.FindAllMunicipalitiesUseCase;
import com.itesm.application.usecase.municipality.FindMunicipalitiesByStateUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class MunicipalityResourceTest {

    @InjectMock
    FindAllMunicipalitiesUseCase findAllMunicipalitiesUseCase;

    @InjectMock
    FindMunicipalitiesByStateUseCase findMunicipalitiesByStateUseCase;

    @Test
    void findMunicipalities_shouldReturnAllMunicipalitiesWhenStateIdMissing() {
        when(findAllMunicipalitiesUseCase.execute()).thenReturn(
                List.of(
                        new MunicipalityResponseDto(10, 1, "Guadalajara", "001"),
                        new MunicipalityResponseDto(11, 1, "Zapopan", "002")
                )
        );

        given()
                .when()
                .get("/municipalities")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[1].name", equalTo("Zapopan"));
    }

    @Test
    void findMunicipalities_shouldReturnByStateId() {
        when(findMunicipalitiesByStateUseCase.execute(1)).thenReturn(
                List.of(new MunicipalityResponseDto(10, 1, "Guadalajara", "001"))
        );

        given()
                .queryParam("stateId", 1)
                .when()
                .get("/municipalities")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].inegiCode", equalTo("001"));
    }
}
