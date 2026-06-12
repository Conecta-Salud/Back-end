package com.itesm.interfaces.rest;

import com.itesm.application.dto.location.LocationSearchResultDto;
import com.itesm.application.usecase.location.SearchLocationsUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class LocationResourceTest {

    @InjectMock
    SearchLocationsUseCase searchLocationsUseCase;

    @Test
    void searchLocations_shouldReturnLocationResults() {
        when(searchLocationsUseCase.execute("Guad", 10)).thenReturn(
                List.of(new LocationSearchResultDto(1, "GDL", "Guadalajara", "city", 14, "14", "Jalisco", "Guadalajara, Jalisco"))
        );

        given()
                .queryParam("q", "Guad")
                .when()
                .get("/locations/search")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].code", equalTo("GDL"))
                .body("[0].displayName", equalTo("Guadalajara, Jalisco"));
    }
}
