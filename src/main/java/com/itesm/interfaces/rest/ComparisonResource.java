package com.itesm.interfaces.rest;

import com.itesm.application.dto.comparison.TerritoryComparisonDto;
import com.itesm.application.usecase.comparison.CompareStatesUseCase;
import com.itesm.application.usecase.comparison.CompareMunicipalitiesUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/comparison")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComparisonResource {

    private final CompareStatesUseCase compareStatesUseCase;
    private final CompareMunicipalitiesUseCase compareMunicipalitiesUseCase;

    @Inject
    public ComparisonResource(
            CompareStatesUseCase compareStatesUseCase,
            CompareMunicipalitiesUseCase compareMunicipalitiesUseCase
    ) {
        this.compareStatesUseCase = compareStatesUseCase;
        this.compareMunicipalitiesUseCase = compareMunicipalitiesUseCase;
    }

    @GET
    @Path("/states")
    public Response compareStates(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("stateIds") List<Integer> stateIds
    ) {
        List<TerritoryComparisonDto> response = compareStatesUseCase.execute(periodId, stateIds);
        return Response.ok(response).build();
    }

    @GET
    @Path("/municipalities")
    public Response compareMunicipalities(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("municipalityIds") List<Integer> municipalityIds
    ) {
        List<TerritoryComparisonDto> response = compareMunicipalitiesUseCase.execute(periodId, municipalityIds);
        return Response.ok(response).build();
    }
}
