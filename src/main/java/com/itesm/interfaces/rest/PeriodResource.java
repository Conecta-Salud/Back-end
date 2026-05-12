package com.itesm.interfaces.rest;

import com.itesm.application.dto.period.PeriodResponseDto;
import com.itesm.application.usecase.period.FindAllPeriodsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/periods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeriodResource {

    private final FindAllPeriodsUseCase findAllPeriodsUseCase;

    @Inject
    public PeriodResource(FindAllPeriodsUseCase findAllPeriodsUseCase) {
        this.findAllPeriodsUseCase = findAllPeriodsUseCase;
    }

    @GET
    public Response findAllPeriods() {
        List<PeriodResponseDto> periods = findAllPeriodsUseCase.execute();
        return Response.ok(periods).build();
    }
}
