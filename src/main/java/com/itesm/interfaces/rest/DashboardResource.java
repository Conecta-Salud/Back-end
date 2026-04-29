package com.itesm.interfaces.rest;

import com.itesm.application.usecase.dashboard.GetDashboardEstadoUseCase;
import com.itesm.application.usecase.dashboard.GetDashboardMunicipioUseCase;
import com.itesm.application.usecase.dashboard.GetDashboardSaludEstadoUseCase;
import com.itesm.application.usecase.dashboard.GetDashboardSaludMunicipioUseCase;
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
    private final GetDashboardSaludEstadoUseCase getDashboardSaludEstadoUseCase;
    private final GetDashboardSaludMunicipioUseCase getDashboardSaludMunicipioUseCase;

    @Inject
    public DashboardResource(
            GetDashboardEstadoUseCase getDashboardEstadoUseCase,
            GetDashboardMunicipioUseCase getDashboardMunicipioUseCase,
            GetDashboardSaludEstadoUseCase getDashboardSaludEstadoUseCase,
            GetDashboardSaludMunicipioUseCase getDashboardSaludMunicipioUseCase
    ) {
        this.getDashboardEstadoUseCase = getDashboardEstadoUseCase;
        this.getDashboardMunicipioUseCase = getDashboardMunicipioUseCase;
        this.getDashboardSaludEstadoUseCase = getDashboardSaludEstadoUseCase;
        this.getDashboardSaludMunicipioUseCase = getDashboardSaludMunicipioUseCase;
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

    @GET
    @Path("/estados/{idEstado}/salud")
    public Response getDashboardSaludEstado(
            @PathParam("idEstado") Integer idEstado,
            @QueryParam("periodoId") Integer periodoId
    ) {
        return Response.ok(getDashboardSaludEstadoUseCase.execute(idEstado, periodoId)).build();
    }

    @GET
    @Path("/municipios/{idMunicipio}/salud")
    public Response getDashboardSaludMunicipio(
            @PathParam("idMunicipio") Integer idMunicipio,
            @QueryParam("periodoId") Integer periodoId
    ) {
        return Response.ok(getDashboardSaludMunicipioUseCase.execute(idMunicipio, periodoId)).build();
    }
}