package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.uploads.CreateUploadBatchRequest;
import com.itesm.application.dto.admin.uploads.UploadBatchResponse;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.service.activity.ActivityLoggerService;
import com.itesm.application.service.upload.UploadBatchService;
import com.itesm.domain.models.user.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class AdminUploadsResourceTest {

    @InjectMock
    UploadBatchService uploadBatchService;

    @InjectMock
    AuthenticatedUserContext authenticatedUserContext;

    @InjectMock
    ActivityLoggerService activityLoggerService;

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
    void createBatch_shouldReturnCreatedBatchResponse() {
        LocalDateTime now = LocalDateTime.now();
        UploadBatchResponse response = new UploadBatchResponse(
                1,
                "INDICADORES",
                "INDIC_2024",
                "Indicadores 2024",
                2024,
                2024,
                "1.0",
                "AUTOMATIC",
                "CREATED",
                2,
                0,
                0,
                0,
                0,
                null,
                now,
                null
        );

        CreateUploadBatchRequest request = new CreateUploadBatchRequest();
        request.setSourceType("INDICADORES");
        request.setSourceYear(2024);

        when(uploadBatchService.createBatch(any(CreateUploadBatchRequest.class), any(UUID.class))).thenReturn(response);

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/admin/uploads/batches")
                .then()
                .statusCode(201)
                .body("sourceType", equalTo("INDICADORES"))
                .body("sourceYear", equalTo(2024));
    }
}
