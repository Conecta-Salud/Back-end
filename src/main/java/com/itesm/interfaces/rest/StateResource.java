package com.itesm.interfaces.rest;

import com.itesm.application.dto.state.StateResponseDto;
import com.itesm.application.usecase.state.FindAllStatesUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/states")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StateResource {

    private final FindAllStatesUseCase findAllStatesUseCase;

    @Inject
    public StateResource(FindAllStatesUseCase findAllStatesUseCase) {
        this.findAllStatesUseCase = findAllStatesUseCase;
    }

    @GET
    public Response findAllStates() {
        List<StateResponseDto> states = findAllStatesUseCase.execute();
        return Response.ok(states).build();
    }
}
