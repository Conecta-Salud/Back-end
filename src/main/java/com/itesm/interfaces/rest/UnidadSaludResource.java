package com.itesm.interfaces.rest;

import com.itesm.application.usecase.unidadSalud.GetUnidadSaludDetalleUseCase;
import com.itesm.application.usecase.unidadSalud.GetUnidadesSaludUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/unidades-salud")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UnidadSaludResource {

    private final GetUnidadesSaludUseCase getUnidadesSaludUseCase;
    private final GetUnidadSaludDetalleUseCase  getUnidadSaludDetalleUseCase;

    @Inject
    public UnidadSaludResource(
            GetUnidadesSaludUseCase getUnidadesSaludUseCase,
            GetUnidadSaludDetalleUseCase  getUnidadSaludDetalleUseCase
    ) {
        this.getUnidadesSaludUseCase = getUnidadesSaludUseCase;
        this.getUnidadSaludDetalleUseCase = getUnidadSaludDetalleUseCase;
    }

    @GET
    public Response findAll(
            @QueryParam("estadoId") Integer estadoId,
            @QueryParam("municipioId") Integer municipioId
    ) {
        return Response.ok(getUnidadesSaludUseCase.execute(estadoId, municipioId)).build();
    }

    @GET
    @Path("/{idUnidad}")
    public Response findDetalleById(
            @PathParam("idUnidad") Integer idUnidad,
            @QueryParam("periodoId") Integer periodoId
    ) {
        return Response.ok(
                getUnidadSaludDetalleUseCase.execute(idUnidad, periodoId)
        ).build();
    }
}
