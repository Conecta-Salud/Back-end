package com.itesm.interfaces.rest;

import com.itesm.application.dto.period.PeriodResponseDto;
import com.itesm.application.usecase.period.FindAllPeriodsUseCase;
import com.itesm.domain.models.period.PeriodStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class PeriodResourceTest {

    @InjectMock
    FindAllPeriodsUseCase findAllPeriodsUseCase;

    @Test
    void findAllPeriods_shouldReturnPeriods() {
        when(findAllPeriodsUseCase.execute()).thenReturn(
                List.of(
                        new PeriodResponseDto(1, 2024, PeriodStatus.open),
                        new PeriodResponseDto(2, 2023, PeriodStatus.published)
                )
        );

        given()
                .when()
                .get("/periods")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(1))
                .body("[0].periodYear", equalTo(2024))
                .body("[0].status", equalTo("open"));
    }
}
