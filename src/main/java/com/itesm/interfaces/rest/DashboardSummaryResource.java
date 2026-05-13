package com.itesm.interfaces.rest;

import com.itesm.application.dto.dashboard.summary.DashboardSummaryDto;
import com.itesm.application.usecase.dashboard.summary.GetCountryDashboardSummaryUseCase;
import com.itesm.application.usecase.dashboard.summary.GetMunicipalityDashboardSummaryUseCase;
import com.itesm.application.usecase.dashboard.summary.GetStateDashboardSummaryUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardSummaryResource {

    private final GetCountryDashboardSummaryUseCase getCountryDashboardSummaryUseCase;
    private final GetStateDashboardSummaryUseCase getStateDashboardSummaryUseCase;
    private final GetMunicipalityDashboardSummaryUseCase getMunicipalityDashboardSummaryUseCase;

    @Inject
    public DashboardSummaryResource(
            GetCountryDashboardSummaryUseCase getCountryDashboardSummaryUseCase,
            GetStateDashboardSummaryUseCase getStateDashboardSummaryUseCase,
            GetMunicipalityDashboardSummaryUseCase getMunicipalityDashboardSummaryUseCase
    ) {
        this.getCountryDashboardSummaryUseCase = getCountryDashboardSummaryUseCase;
        this.getStateDashboardSummaryUseCase = getStateDashboardSummaryUseCase;
        this.getMunicipalityDashboardSummaryUseCase = getMunicipalityDashboardSummaryUseCase;
    }

    @GET
    @Path("/country/summary")
    public Response getCountrySummary(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("category") String category
    ) {
        DashboardSummaryDto response = getCountryDashboardSummaryUseCase.execute(periodId, category);
        return Response.ok(response).build();
    }

    @GET
    @Path("/states/{stateId}/summary")
    public Response getStateSummary(
            @PathParam("stateId") Integer stateId,
            @QueryParam("periodId") Integer periodId,
            @QueryParam("category") String category
    ) {
        DashboardSummaryDto response = getStateDashboardSummaryUseCase.execute(stateId, periodId, category);
        return Response.ok(response).build();
    }

    @GET
    @Path("/municipalities/{municipalityId}/summary")
    public Response getMunicipalitySummary(
            @PathParam("municipalityId") Integer municipalityId,
            @QueryParam("periodId") Integer periodId,
            @QueryParam("category") String category
    ) {
        DashboardSummaryDto response = getMunicipalityDashboardSummaryUseCase.execute(
                municipalityId,
                periodId,
                category
        );

        return Response.ok(response).build();
    }
}