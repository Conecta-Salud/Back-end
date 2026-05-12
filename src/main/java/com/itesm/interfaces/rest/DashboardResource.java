package com.itesm.interfaces.rest;

import com.itesm.application.dto.dashboard.HealthDashboardResponseDto;
import com.itesm.application.dto.dashboard.IndicatorsResponseDto;
import com.itesm.application.usecase.dashboard.GetStateDashboardUseCase;
import com.itesm.application.usecase.dashboard.GetMunicipalityDashboardUseCase;
import com.itesm.application.usecase.dashboard.GetStateHealthDashboardUseCase;
import com.itesm.application.usecase.dashboard.GetMunicipalityHealthDashboardUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    private final GetStateDashboardUseCase getStateDashboardUseCase;
    private final GetMunicipalityDashboardUseCase getMunicipalityDashboardUseCase;
    private final GetStateHealthDashboardUseCase getStateHealthDashboardUseCase;
    private final GetMunicipalityHealthDashboardUseCase getMunicipalityHealthDashboardUseCase;

    @Inject
    public DashboardResource(
            GetStateDashboardUseCase getStateDashboardUseCase,
            GetMunicipalityDashboardUseCase getMunicipalityDashboardUseCase,
            GetStateHealthDashboardUseCase getStateHealthDashboardUseCase,
            GetMunicipalityHealthDashboardUseCase getMunicipalityHealthDashboardUseCase
    ) {
        this.getStateDashboardUseCase = getStateDashboardUseCase;
        this.getMunicipalityDashboardUseCase = getMunicipalityDashboardUseCase;
        this.getStateHealthDashboardUseCase = getStateHealthDashboardUseCase;
        this.getMunicipalityHealthDashboardUseCase = getMunicipalityHealthDashboardUseCase;
    }

    @GET
    @Path("/states/{stateId}/indicators")
    public Response getStateIndicatorsDashboard(
            @PathParam("stateId") Integer stateId,
            @QueryParam("periodId") Integer periodId
    ) {
        IndicatorsResponseDto response = getStateDashboardUseCase.execute(stateId, periodId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/municipalities/{municipalityId}/indicators")
    public Response getMunicipalityIndicatorsDashboard(
            @PathParam("municipalityId") Integer municipalityId,
            @QueryParam("periodId") Integer periodId
    ) {
        IndicatorsResponseDto response = getMunicipalityDashboardUseCase.execute(municipalityId, periodId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/states/{stateId}/health")
    public Response getStateHealthDashboard(
            @PathParam("stateId") Integer stateId,
            @QueryParam("periodId") Integer periodId
    ) {
        HealthDashboardResponseDto response = getStateHealthDashboardUseCase.execute(stateId, periodId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/municipalities/{municipalityId}/health")
    public Response getMunicipalityHealthDashboard(
            @PathParam("municipalityId") Integer municipalityId,
            @QueryParam("periodId") Integer periodId
    ) {
        HealthDashboardResponseDto response = getMunicipalityHealthDashboardUseCase.execute(municipalityId, periodId);
        return Response.ok(response).build();
    }
}