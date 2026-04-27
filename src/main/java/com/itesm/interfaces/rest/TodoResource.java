package com.itesm.interfaces.rest;

import com.itesm.application.dto.CreateTodoDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.usecase.CreateTodoUseCase;
import com.itesm.domain.models.Todo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

    private final CreateTodoUseCase createTodoUseCase;
    private final AuthenticatedUserContext authenticatedUserContext;

    @Inject
    public TodoResource(CreateTodoUseCase createTodoUseCase, AuthenticatedUserContext authenticatedUserContext) {
        this.createTodoUseCase = createTodoUseCase;
        this.authenticatedUserContext = authenticatedUserContext;
    }

    @POST
    public Response createTodo(CreateTodoDto createTodoDto){
        Todo todo= createTodoUseCase.execute(createTodoDto);
        return Response.ok(todo).build();
    }

    @Path("/test")
    @GET
    public Response getTodo(){
        System.out.println("Desde el endpoint: "+authenticatedUserContext.getCurrentUser().getFullName());
        return Response.ok("TEST").build();
    }

}
