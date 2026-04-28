package com.itesm.interfaces.rest;

import com.google.firebase.auth.FirebaseAuthException;
import com.itesm.application.dto.user_dto.RegisterUserDto;
import com.itesm.application.dto.user_dto.UserProfileResponseDto;
import com.itesm.application.usecase.user_usecase.GetCurrentUserUseCase;
import com.itesm.application.usecase.user_usecase.RegisterUserUseCase;
import com.itesm.domain.models.user_model.User;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    RegisterUserUseCase registerUserUseCase;
    GetCurrentUserUseCase  getCurrentUserUseCase;

    public UserResource(
            RegisterUserUseCase registerUserUseCase, GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @POST
    public Response registerUser(@Valid RegisterUserDto registerUserDto) {
        try {
            User user= registerUserUseCase.execute(registerUserDto);
            return Response.ok(user).build();
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/profile")
    public Response getCurrentUser(){
        UserProfileResponseDto user = getCurrentUserUseCase.execute();
        return Response.ok(user).build();
    }
}
