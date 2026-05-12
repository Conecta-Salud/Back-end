package com.itesm.interfaces.rest;

import com.itesm.application.usecase.estado.FindAllEstadosUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/estados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstadoResource {

    private final FindAllEstadosUseCase findAllEstadosUseCase;

    @Inject
    public EstadoResource(FindAllEstadosUseCase findAllEstadosUseCase) {
        this.findAllEstadosUseCase = findAllEstadosUseCase;
    }

    @GET
    public Response getAllEstados() {
        return Response.ok(findAllEstadosUseCase.execute()).build();
    }
}
