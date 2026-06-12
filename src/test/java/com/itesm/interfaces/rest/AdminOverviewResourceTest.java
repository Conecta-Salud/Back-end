package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.overview.AdminOverviewResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.usecase.admin.overview.GetAdminOverviewUseCase;
import com.itesm.domain.models.user.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@QuarkusTest
class AdminOverviewResourceTest {

    @InjectMock
    GetAdminOverviewUseCase getAdminOverviewUseCase;

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
    void getOverview_shouldReturnAdminOverviewData() {
        when(getAdminOverviewUseCase.execute()).thenReturn(
                new AdminOverviewResponseDto(150L, 45L, 320L, 28L)
        );

        given()
                .when()
                .get("/admin/overview")
                .then()
                .statusCode(200)
                .body("registeredUsers", equalTo(150))
                .body("activeUsersLast7Days", equalTo(45))
                .body("comparisonsPerformed", equalTo(320))
                .body("completedUploadBatches", equalTo(28));
    }
}
