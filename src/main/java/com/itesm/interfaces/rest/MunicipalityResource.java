package com.itesm.interfaces.rest;

import com.itesm.application.dto.municipality.MunicipalityResponseDto;
import com.itesm.application.usecase.municipality.FindAllMunicipalitiesUseCase;
import com.itesm.application.usecase.municipality.FindMunicipalitiesByStateUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/municipalities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MunicipalityResource {

    private final FindAllMunicipalitiesUseCase findAllMunicipalitiesUseCase;
    private final FindMunicipalitiesByStateUseCase findMunicipalitiesByStateUseCase;

    @Inject
    public MunicipalityResource(
            FindAllMunicipalitiesUseCase findAllMunicipalitiesUseCase,
            FindMunicipalitiesByStateUseCase findMunicipalitiesByStateUseCase
    ) {
        this.findAllMunicipalitiesUseCase = findAllMunicipalitiesUseCase;
        this.findMunicipalitiesByStateUseCase = findMunicipalitiesByStateUseCase;
    }

    @GET
    public Response findMunicipalities(@QueryParam("stateId") Integer stateId) {
        List<MunicipalityResponseDto> municipalities = stateId != null
                ? findMunicipalitiesByStateUseCase.execute(stateId)
                : findAllMunicipalitiesUseCase.execute();

        return Response.ok(municipalities).build();
    }
}
