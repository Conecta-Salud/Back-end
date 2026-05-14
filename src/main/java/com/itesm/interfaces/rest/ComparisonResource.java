package com.itesm.interfaces.rest;

import com.itesm.application.dto.comparison.TerritoryComparisonDto;
import com.itesm.application.usecase.comparison.CompareStatesUseCase;
import com.itesm.application.usecase.comparison.CompareMunicipalitiesUseCase;
import com.itesm.application.usecase.comparison.CompareMunicipalitiesByCodesUseCase;
import com.itesm.application.usecase.comparison.CompareStatesByCodesUseCase;
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
    private final CompareMunicipalitiesByCodesUseCase compareMunicipalitiesByCodesUseCase;
    private final CompareStatesByCodesUseCase compareStatesByCodesUseCase;

    @Inject
    public ComparisonResource(
            CompareStatesUseCase compareStatesUseCase,
            CompareMunicipalitiesUseCase compareMunicipalitiesUseCase,
            CompareMunicipalitiesByCodesUseCase compareMunicipalitiesByCodesUseCase,
            CompareStatesByCodesUseCase compareStatesByCodesUseCase
            ) {
        this.compareStatesUseCase = compareStatesUseCase;
        this.compareMunicipalitiesUseCase = compareMunicipalitiesUseCase;
        this.compareMunicipalitiesByCodesUseCase = compareMunicipalitiesByCodesUseCase;
        this.compareStatesByCodesUseCase = compareStatesByCodesUseCase;
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

    @GET
    @Path("/municipalities/by-codes")
    public Response compareMunicipalitiesByCodes(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("municipalityCodes") List<String> municipalityCodes
    ) {
        List<TerritoryComparisonDto> response = compareMunicipalitiesByCodesUseCase.execute(periodId, municipalityCodes);
        return Response.ok(response).build();
    }

    @GET
    @Path("/states/by-codes")
    public Response compareStatesByCodes(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("stateCodes") List<String> stateCodes
    ) {
        List<TerritoryComparisonDto> response = compareStatesByCodesUseCase.execute(periodId, stateCodes);
        return Response.ok(response).build();
    }
}
