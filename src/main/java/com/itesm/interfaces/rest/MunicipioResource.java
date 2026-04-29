package com.itesm.interfaces.rest;

import com.itesm.application.usecase.municipio.FindAllMunicipiosUseCase;
import com.itesm.domain.models.municipio.Municipio;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/municipios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MunicipioResource {

    private final FindAllMunicipiosUseCase findAllMunicipiosUseCase;

    @Inject
    public MunicipioResource(FindAllMunicipiosUseCase findAllMunicipiosUseCase) {
        this.findAllMunicipiosUseCase = findAllMunicipiosUseCase;
    }

    @GET
    public Response getAllMunicipios() {
        return Response.ok(findAllMunicipiosUseCase.execute()).build();
    }
}
