package com.itesm.interfaces.rest;

import com.itesm.application.dto.comparison.summary.ComparisonSummaryDto;
import com.itesm.application.usecase.comparison.summary.GetComparisonSummaryUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/comparison/summary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComparisonSummaryResource {

    private final GetComparisonSummaryUseCase getComparisonSummaryUseCase;

    @Inject
    public ComparisonSummaryResource(GetComparisonSummaryUseCase getComparisonSummaryUseCase) {
        this.getComparisonSummaryUseCase = getComparisonSummaryUseCase;
    }

    @GET
    @Path("/states")
    public Response compareStatesSummary(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("stateCodes") List<String> stateCodes
    ) {
        ComparisonSummaryDto response = getComparisonSummaryUseCase.executeStates(
                periodId,
                stateCodes
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("/municipalities")
    public Response compareMunicipalitiesSummary(
            @QueryParam("periodId") Integer periodId,
            @QueryParam("municipalityCodes") List<String> municipalityCodes
    ) {
        ComparisonSummaryDto response = getComparisonSummaryUseCase.executeMunicipalities(
                periodId,
                municipalityCodes
        );

        return Response.ok(response).build();
    }
}
