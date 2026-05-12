package com.itesm.interfaces.rest;

import com.itesm.application.dto.healthunit.HealthUnitDetailDto;
import com.itesm.application.dto.healthunit.HealthUnitSummaryDto;
import com.itesm.application.usecase.healthunit.GetHealthUnitDetailUseCase;
import com.itesm.application.usecase.healthunit.GetHealthUnitsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/health-units")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HealthUnitResource {

    private final GetHealthUnitsUseCase getHealthUnitsUseCase;
    private final GetHealthUnitDetailUseCase getHealthUnitDetailUseCase;

    @Inject
    public HealthUnitResource(
            GetHealthUnitsUseCase getHealthUnitsUseCase,
            GetHealthUnitDetailUseCase getHealthUnitDetailUseCase
    ) {
        this.getHealthUnitsUseCase = getHealthUnitsUseCase;
        this.getHealthUnitDetailUseCase = getHealthUnitDetailUseCase;
    }

    @GET
    public Response getHealthUnits(
            @QueryParam("stateId") Integer stateId,
            @QueryParam("municipalityId") Integer municipalityId
    ) {
        List<HealthUnitSummaryDto> healthUnits = getHealthUnitsUseCase.execute(stateId, municipalityId);
        return Response.ok(healthUnits).build();
    }

    @GET
    @Path("/{healthUnitId}")
    public Response getHealthUnitDetail(
            @PathParam("healthUnitId") Integer healthUnitId,
            @QueryParam("periodId") Integer periodId
    ) {
        HealthUnitDetailDto healthUnit = getHealthUnitDetailUseCase.execute(healthUnitId, periodId);
        return Response.ok(healthUnit).build();
    }
}
