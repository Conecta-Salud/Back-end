package com.itesm.interfaces.rest;

import com.itesm.application.usecase.dashboard.GetDashboardEstadoUseCase;
import com.itesm.application.usecase.dashboard.GetDashboardMunicipioUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {

    private final GetDashboardEstadoUseCase getDashboardEstadoUseCase;
    private final GetDashboardMunicipioUseCase getDashboardMunicipioUseCase;

    @Inject
    public DashboardResource(
            GetDashboardEstadoUseCase getDashboardEstadoUseCase,
            GetDashboardMunicipioUseCase getDashboardMunicipioUseCase
    ) {
        this.getDashboardEstadoUseCase = getDashboardEstadoUseCase;
        this.getDashboardMunicipioUseCase = getDashboardMunicipioUseCase;
    }

    @GET
    @Path("/estados/{idEstado}")
    public Response getDashboardEstado(
            @PathParam("idEstado") Integer idEstado,
            @QueryParam("periodoId") Integer periodoId
    ) {
        return Response.ok(getDashboardEstadoUseCase.execute(idEstado, periodoId)).build();
    }

    @GET
    @Path("/municipios/{idMunicipio}")
    public Response getDashboardMunicipio(
            @PathParam("idMunicipio") Integer idMunicipio,
            @QueryParam("periodoId") Integer periodoId
    ) {
        return Response.ok(getDashboardMunicipioUseCase.execute(idMunicipio, periodoId)).build();
    }
}