package com.itesm.interfaces.rest;

import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.dto.user.*;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.usecase.user.*;
import com.itesm.domain.models.user.User;
import com.itesm.domain.models.user.UserRole;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final CreateUserUseCase createUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserByIdUseCase deleteUserByIdUseCase;
    private final ReactivateUserByIdUseCase reactivateUserByIdUseCase;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;

    @Inject
    public UserResource(
            CreateUserUseCase createUserUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase,
            FindUserByIdUseCase findUserByIdUseCase,
            FindAllUsersUseCase findAllUsersUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserByIdUseCase deleteUserByIdUseCase,
            AuthenticatedUserContext authenticatedUserContext,
            ReactivateUserByIdUseCase reactivateUserByIdUseCase,
            ChangeUserPasswordUseCase changeUserPasswordUseCase

    ) {
        this.createUserUseCase = createUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.findAllUsersUseCase = findAllUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserByIdUseCase = deleteUserByIdUseCase;
        this.authenticatedUserContext = authenticatedUserContext;
        this.reactivateUserByIdUseCase = reactivateUserByIdUseCase;
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
    }

    @POST
    public Response createUser(@Valid CreateUserDto createUserDto) {
        assertAdmin();

        User user = createUserUseCase.execute(createUserDto);

        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }

    @GET
    @Path("/profile")
    public Response getCurrentUser() {
        UserProfileResponseDto user = getCurrentUserUseCase.execute();
        return Response.ok(user).build();
    }

    @GET
    public Response findAllUsers(
            @QueryParam("search") String search,
            @QueryParam("departmentId") Integer departmentId,
            @QueryParam("role") UserRole role,
            @QueryParam("active") Boolean active,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        assertAdmin();

        PageResponseDto<UserListResponseDto> users = findAllUsersUseCase.execute(
                search,
                departmentId,
                role,
                active,
                page,
                size
        );

        return Response.ok(users).build();
    }

    @GET
    @Path("/{userId}")
    public Response findUserById(@PathParam("userId") UUID userId) {
        assertAdmin();

        User user = findUserByIdUseCase.execute(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        return Response.ok(user).build();
    }

    @PUT
    @Path("/{userId}")
    public Response updateUser(
            @PathParam("userId") UUID userId,
            UpdateUserDto updateUserDto
    ) {
        assertAdmin();

        User updatedUser = updateUserUseCase.execute(userId, updateUserDto);

        return Response.ok(updatedUser).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") UUID userId) {
        assertAdmin();

        User deletedUser = deleteUserByIdUseCase.execute(userId);

        return Response.ok(deletedUser).build();
    }

    @PUT
    @Path("/reactivate/{userId}")
    public Response reactivateUser(@PathParam("userId") UUID userId) {
        assertAdmin();

        User reactivatedUser = reactivateUserByIdUseCase.execute(userId);

        return Response.ok(reactivatedUser).build();
    }


    private void assertAdmin() {
        CurrentUser currentUser = authenticatedUserContext.getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new ForbiddenException("Solo administradores pueden realizar esta acción");
        }
    }

    @PATCH
    @Path("/{userId}/password")
    public Response changePassword(
            @PathParam("userId") UUID userId,
            @Valid ChangeUserPasswordDto dto
    ) {
        assertAdmin();

        changeUserPasswordUseCase.execute(userId, dto);

        return Response.noContent().build();
    }
}
