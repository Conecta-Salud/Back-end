package com.itesm.interfaces.rest;

import com.itesm.application.dto.dashboard.summary.DashboardSummaryDto;
import com.itesm.application.usecase.dashboard.summary.GetCountryDashboardSummaryUseCase;
import com.itesm.application.usecase.dashboard.summary.GetMunicipalityDashboardSummaryUseCase;
import com.itesm.application.usecase.dashboard.summary.GetStateDashboardSummaryUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
class DashboardSummaryResourceTest {

    @InjectMock
    GetCountryDashboardSummaryUseCase getCountryDashboardSummaryUseCase;

    @InjectMock
    GetStateDashboardSummaryUseCase getStateDashboardSummaryUseCase;

    @InjectMock
    GetMunicipalityDashboardSummaryUseCase getMunicipalityDashboardSummaryUseCase;

    @Test
    void getCountrySummary_shouldReturnDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto(null, null, "health", null, null, null, null);
        when(getCountryDashboardSummaryUseCase.execute(1, "health")).thenReturn(summary);

        given()
                .queryParam("periodId", 1)
                .queryParam("category", "health")
                .when()
                .get("/dashboard/country/summary")
                .then()
                .statusCode(200);
    }

    @Test
    void getStateSummary_shouldReturnDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto(null, null, "health", null, null, null, null);
        when(getStateDashboardSummaryUseCase.execute(14, 1, "health")).thenReturn(summary);

        given()
                .pathParam("stateId", 14)
                .queryParam("periodId", 1)
                .queryParam("category", "health")
                .when()
                .get("/dashboard/states/{stateId}/summary")
                .then()
                .statusCode(200);
    }

    @Test
    void getMunicipalitySummary_shouldReturnDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto(null, null, "health", null, null, null, null);
        when(getMunicipalityDashboardSummaryUseCase.execute(1, 1, "health")).thenReturn(summary);

        given()
                .pathParam("municipalityId", 1)
                .queryParam("periodId", 1)
                .queryParam("category", "health")
                .when()
                .get("/dashboard/municipalities/{municipalityId}/summary")
                .then()
                .statusCode(200);
    }
}
