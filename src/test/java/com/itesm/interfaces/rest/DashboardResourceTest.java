package com.itesm.interfaces.rest;

import com.itesm.application.dto.dashboard.HealthDashboardResponseDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.usecase.dashboard.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
class DashboardResourceTest {

    @InjectMock
    GetStateDashboardUseCase getStateDashboardUseCase;

    @InjectMock
    GetMunicipalityDashboardUseCase getMunicipalityDashboardUseCase;

    @InjectMock
    GetStateHealthDashboardUseCase getStateHealthDashboardUseCase;

    @InjectMock
    GetMunicipalityHealthDashboardUseCase getMunicipalityHealthDashboardUseCase;

    @InjectMock
    GetCountryIndicatorsDashboardUseCase getCountryIndicatorsDashboardUseCase;

    @InjectMock
    GetCountryHealthDashboardUseCase getCountryHealthDashboardUseCase;

    @Test
    void getStateIndicatorsDashboard_shouldReturnIndicators() {
        IndicatorsResponseDto indicators = new IndicatorsResponseDto(null, null, null);
        when(getStateDashboardUseCase.execute(14, 1)).thenReturn(indicators);

        given()
                .pathParam("stateId", 14)
                .queryParam("periodId", 1)
                .when()
                .get("/dashboard/states/{stateId}/indicators")
                .then()
                .statusCode(200);
    }

    @Test
    void getMunicipalityIndicatorsDashboard_shouldReturnIndicators() {
        IndicatorsResponseDto indicators = new IndicatorsResponseDto(null, null, null);
        when(getMunicipalityDashboardUseCase.execute(1, 1)).thenReturn(indicators);

        given()
                .pathParam("municipalityId", 1)
                .queryParam("periodId", 1)
                .when()
                .get("/dashboard/municipalities/{municipalityId}/indicators")
                .then()
                .statusCode(200);
    }

    @Test
    void getStateHealthDashboard_shouldReturnHealthData() {
        HealthDashboardResponseDto health = new HealthDashboardResponseDto(null, null, null);
        when(getStateHealthDashboardUseCase.execute(14, 1)).thenReturn(health);

        given()
                .pathParam("stateId", 14)
                .queryParam("periodId", 1)
                .when()
                .get("/dashboard/states/{stateId}/health")
                .then()
                .statusCode(200);
    }

    @Test
    void getMunicipalityHealthDashboard_shouldReturnHealthData() {
        HealthDashboardResponseDto health = new HealthDashboardResponseDto(null, null, null);
        when(getMunicipalityHealthDashboardUseCase.execute(1, 1)).thenReturn(health);

        given()
                .pathParam("municipalityId", 1)
                .queryParam("periodId", 1)
                .when()
                .get("/dashboard/municipalities/{municipalityId}/health")
                .then()
                .statusCode(200);
    }
}
