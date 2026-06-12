package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.activity.ActivityLogResponseDto;
import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.usecase.admin.activity.FindActivityLogsUseCase;
import com.itesm.domain.models.user.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@QuarkusTest
class AdminActivityLogResourceTest {

    @InjectMock
    FindActivityLogsUseCase findActivityLogsUseCase;

    @InjectMock
    AuthenticatedUserContext authenticatedUserContext;

    @BeforeEach
    void setup() {
        CurrentUser adminUser = new CurrentUser(
                UUID.randomUUID(),
                1,
                "IT",
                "Admin",
                "User",
                "admin@example.com",
                "firebase-uuid",
                UserRole.admin
        );
        when(authenticatedUserContext.getCurrentUser()).thenReturn(adminUser);
    }

    @Test
    void findActivityLogs_shouldReturnPaginatedActivityLogs() {
        PageResponseDto<ActivityLogResponseDto> response = new PageResponseDto<>(
                List.of(
                        new ActivityLogResponseDto(1L, UUID.randomUUID(), "user@email.com", "User Name", "CREATE_USER", "USERS", "SUCCESS", "User created", LocalDateTime.now())
                ),
                1L,
                0,
                20,
                1
        );

        when(findActivityLogsUseCase.execute(null, null, null, null, null, null, 0, 20)).thenReturn(response);

        given()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/admin/activity-logs")
                .then()
                .statusCode(200)
                .body("items", hasSize(1))
                .body("items[0].action", equalTo("CREATE_USER"))
                .body("items[0].module", equalTo("USERS"));
    }
}
