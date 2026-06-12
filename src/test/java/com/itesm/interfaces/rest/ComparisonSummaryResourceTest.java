package com.itesm.interfaces.rest;

import com.itesm.application.dto.comparison.summary.ComparisonSummaryDto;
import com.itesm.application.usecase.comparison.summary.GetComparisonSummaryUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@QuarkusTest
class ComparisonSummaryResourceTest {

    @InjectMock
    GetComparisonSummaryUseCase getComparisonSummaryUseCase;

    @Test
    void compareStatesSummary_shouldReturnComparisonSummary() {
        ComparisonSummaryDto summary = new ComparisonSummaryDto(null, "STATE", List.of(), List.of(), List.of());
        when(getComparisonSummaryUseCase.executeStates(1, List.of("14", "19"))).thenReturn(summary);

        given()
                .queryParam("periodId", 1)
                .queryParam("stateCodes", "14,19")
                .when()
                .get("/comparison/summary/states")
                .then()
                .statusCode(200);
    }

    @Test
    void compareMunicipalitiesSummary_shouldReturnComparisonSummary() {
        ComparisonSummaryDto summary = new ComparisonSummaryDto(null, "MUNICIPALITY", List.of(), List.of(), List.of());
        when(getComparisonSummaryUseCase.executeMunicipalities(1, List.of("14001", "14002"))).thenReturn(summary);

        given()
                .queryParam("periodId", 1)
                .queryParam("municipalityCodes", "14001,14002")
                .when()
                .get("/comparison/summary/municipalities")
                .then()
                .statusCode(200);
    }
}
