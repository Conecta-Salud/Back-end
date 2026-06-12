package com.itesm.interfaces.rest;

import com.itesm.application.dto.state.StateResponseDto;
import com.itesm.application.usecase.state.FindAllStatesUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class StateResourceTest {

    @InjectMock
    FindAllStatesUseCase findAllStatesUseCase;

    @Test
    void findAllStates_shouldReturnStates() {
        when(findAllStatesUseCase.execute()).thenReturn(
                List.of(
                        new StateResponseDto(1, "Jalisco", "14"),
                        new StateResponseDto(2, "Nuevo León", "19")
                )
        );

        given()
                .when()
                .get("/states")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Jalisco"))
                .body("[0].inegiCode", equalTo("14"))
                .body("[1].id", equalTo(2))
                .body("[1].name", equalTo("Nuevo León"))
                .body("[1].inegiCode", equalTo("19"));
    }
}
