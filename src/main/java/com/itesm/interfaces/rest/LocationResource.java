package com.itesm.interfaces.rest;

import com.itesm.application.dto.location.LocationSearchResultDto;
import com.itesm.application.usecase.location.SearchLocationsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {

    private final SearchLocationsUseCase searchLocationsUseCase;

    @Inject
    public LocationResource(SearchLocationsUseCase searchLocationsUseCase) {
        this.searchLocationsUseCase = searchLocationsUseCase;
    }

    @GET
    @Path("/search")
    public Response searchLocations(
            @QueryParam("q") String query,
            @QueryParam("limit") @DefaultValue("10") Integer limit
    ) {
        List<LocationSearchResultDto> results = searchLocationsUseCase.execute(query, limit);
        return Response.ok(results).build();
    }
}