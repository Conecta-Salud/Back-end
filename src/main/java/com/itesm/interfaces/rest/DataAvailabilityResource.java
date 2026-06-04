package com.itesm.interfaces.rest;

import com.itesm.application.dto.availability.DataAvailabilityResponseDto;
import com.itesm.application.usecase.availability.GetDataAvailabilityUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/data-availability")
@Produces(MediaType.APPLICATION_JSON)
public class DataAvailabilityResource {

    private final GetDataAvailabilityUseCase getDataAvailabilityUseCase;

    @Inject
    public DataAvailabilityResource(GetDataAvailabilityUseCase getDataAvailabilityUseCase) {
        this.getDataAvailabilityUseCase = getDataAvailabilityUseCase;
    }

    @GET
    public DataAvailabilityResponseDto findAvailability(
            @QueryParam("territoryLevel") String territoryLevel,
            @QueryParam("analysisYear") Integer analysisYear,
            @QueryParam("categoryCode") String categoryCode
    ) {
        return getDataAvailabilityUseCase.execute(
                territoryLevel,
                analysisYear,
                categoryCode
        );
    }
}
