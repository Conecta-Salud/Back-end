package com.itesm.interfaces.rest;

import com.itesm.application.usecase.periodo.FindAllPeriodosUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/periodos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeriodoResource {

    private final FindAllPeriodosUseCase findAllPeriodosUseCase;

    @Inject
    public PeriodoResource(FindAllPeriodosUseCase findAllPeriodosUseCase) {
        this.findAllPeriodosUseCase = findAllPeriodosUseCase;
    }

    @GET
    public Response findAll() {
        return Response.ok(findAllPeriodosUseCase.execute()).build();
    }
}
