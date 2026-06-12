package com.itesm.interfaces.rest;

import com.itesm.application.dto.availability.DataAvailabilityItemDto;
import com.itesm.application.dto.availability.DataAvailabilityResponseDto;
import com.itesm.application.usecase.availability.GetDataAvailabilityUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class DataAvailabilityResourceTest {

    @InjectMock
    GetDataAvailabilityUseCase getDataAvailabilityUseCase;

    @Test
    void findAvailability_shouldReturnDataAvailabilityResponse() {
        when(getDataAvailabilityUseCase.execute("state", 2024, "health")).thenReturn(
                new DataAvailabilityResponseDto(
                        List.of(2023, 2024),
                        List.of(new DataAvailabilityItemDto("CAT", "Categoria", "health", "Salud", "state", 2024, 2024, true, "available", "note"))
                )
        );

        given()
                .queryParam("territoryLevel", "state")
                .queryParam("analysisYear", 2024)
                .queryParam("categoryCode", "health")
                .when()
                .get("/api/v1/data-availability")
                .then()
                .statusCode(200)
                .body("years.size()", equalTo(2))
                .body("items[0].categoryCode", equalTo("CAT"));
    }
}
