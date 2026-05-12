package com.itesm.interfaces.rest;

import com.itesm.application.dto.map.MapIndicatorResponseDto;
import com.itesm.application.usecase.map.GetStateMapUseCase;
import com.itesm.application.usecase.map.GetMunicipalityMapUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/map")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MapResource {

    private final GetStateMapUseCase getStateMapUseCase;
    private final GetMunicipalityMapUseCase getMunicipalityMapUseCase;

    @Inject
    public MapResource(
            GetStateMapUseCase getStateMapUseCase,
            GetMunicipalityMapUseCase getMunicipalityMapUseCase
    ) {
        this.getStateMapUseCase = getStateMapUseCase;
        this.getMunicipalityMapUseCase = getMunicipalityMapUseCase;
    }

    @GET
    @Path("/states")
    public Response getStateMap(
            @QueryParam("indicator") String indicator,
            @QueryParam("year") Integer year
    ) {
        List<MapIndicatorResponseDto> response = getStateMapUseCase.execute(indicator, year);
        return Response.ok(response).build();
    }

    @GET
    @Path("/municipalities")
    public Response getMunicipalityMap(
            @QueryParam("stateCode") String stateCode,
            @QueryParam("indicator") String indicator,
            @QueryParam("year") Integer year
    ) {
        List<MapIndicatorResponseDto> response = getMunicipalityMapUseCase.execute(stateCode, indicator, year);
        return Response.ok(response).build();
    }
}
