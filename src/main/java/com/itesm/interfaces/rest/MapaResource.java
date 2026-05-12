package com.itesm.interfaces.rest;

import com.itesm.application.usecase.mapa.GetMapaEstadosUseCase;
import com.itesm.application.usecase.mapa.GetMapaMunicipiosUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/map")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MapaResource {

    private final GetMapaEstadosUseCase getMapaEstadosUseCase;
    private final GetMapaMunicipiosUseCase getMapaMunicipiosUseCase;

    @Inject
    public MapaResource(
            GetMapaEstadosUseCase getMapaEstadosUseCase,
            GetMapaMunicipiosUseCase getMapaMunicipiosUseCase
    ) {
        this.getMapaEstadosUseCase = getMapaEstadosUseCase;
        this.getMapaMunicipiosUseCase = getMapaMunicipiosUseCase;
    }

    @GET
    @Path("/estados")
    public Response getMapaEstados(
            @QueryParam("indicador") String indicador,
            @QueryParam("anio") Integer anio
    ) {
        return Response.ok(
                getMapaEstadosUseCase.execute(indicador, anio)
        ).build();
    }

    @GET
    @Path("/estados/{claveEstado}/municipios")
    public Response getMapaMunicipios(
            @PathParam("claveEstado") String claveEstado,
            @QueryParam("indicador") String indicador,
            @QueryParam("anio") Integer anio
    ) {
        return Response.ok(
                getMapaMunicipiosUseCase.execute(claveEstado, indicador, anio)
        ).build();
    }
}
