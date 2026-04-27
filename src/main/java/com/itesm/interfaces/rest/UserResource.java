package com.itesm.interfaces.rest;

import com.google.firebase.auth.FirebaseAuthException;
import com.itesm.application.dto.RegisterUserDto;
import com.itesm.application.usecase.RegisterUserUseCase;
import com.itesm.domain.models.User;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    RegisterUserUseCase registerUserUseCase;

    public UserResource(
            RegisterUserUseCase registerUserUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
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
}
