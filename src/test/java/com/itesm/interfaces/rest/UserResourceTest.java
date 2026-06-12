package com.itesm.interfaces.rest;

import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.dto.user.CreateUserDto;
import com.itesm.application.dto.user.UserListResponseDto;
import com.itesm.application.dto.user.UserProfileResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.usecase.user.*;
import com.itesm.domain.models.user.User;
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
class UserResourceTest {

    @InjectMock
    CreateUserUseCase createUserUseCase;

    @InjectMock
    GetCurrentUserUseCase getCurrentUserUseCase;

    @InjectMock
    FindUserByIdUseCase findUserByIdUseCase;

    @InjectMock
    FindAllUsersUseCase findAllUsersUseCase;

    @InjectMock
    UpdateUserUseCase updateUserUseCase;

    @InjectMock
    DeleteUserByIdUseCase deleteUserByIdUseCase;

    @InjectMock
    ReactivateUserByIdUseCase reactivateUserByIdUseCase;

    @InjectMock
    ChangeUserPasswordUseCase changeUserPasswordUseCase;

    @InjectMock
    GetAdminUserDetailUseCase getAdminUserDetailUseCase;

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
    void getCurrentUser_shouldReturnCurrentUserProfile() {
        UUID userId = UUID.randomUUID();
        UserProfileResponseDto profile = new UserProfileResponseDto(
                userId,
                1,
                "IT",
                "John",
                "Doe",
                "user@example.com",
                "firebase-uuid",
                UserRole.strategic,
                true,
                LocalDateTime.now()
        );
        when(getCurrentUserUseCase.execute()).thenReturn(profile);

        given()
                .when()
                .get("/users/profile")
                .then()
                .statusCode(200)
                .body("email", equalTo("user@example.com"))
                .body("firstName", equalTo("John"))
                .body("role", equalTo("strategic"));
    }

    @Test
    void findAllUsers_shouldReturnPaginatedUsers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PageResponseDto<UserListResponseDto> response = new PageResponseDto<>(
                List.of(
                        new UserListResponseDto(userId1, 1, "IT", "Admin", "User", "Admin User", "admin@example.com", UserRole.admin, true, LocalDateTime.now()),
                        new UserListResponseDto(userId2, 2, "HR", "Viewer", "User", "Viewer User", "viewer@example.com", UserRole.strategic, true, LocalDateTime.now())
                ),
                2L,
                0,
                20,
                1
        );
        when(findAllUsersUseCase.execute(null, null, null, null, 0, 20)).thenReturn(response);

        given()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("items", hasSize(2))
                .body("items[0].email", equalTo("admin@example.com"))
                .body("items[1].role", equalTo("strategic"));
    }
}
