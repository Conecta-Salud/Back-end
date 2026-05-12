package com.itesm.interfaces.rest;

import com.itesm.application.usecase.comparacion.CompararEstadosUseCase;
import com.itesm.application.usecase.comparacion.CompararMunicipiosUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/comparacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComparacionResource {

    private final CompararEstadosUseCase compararEstadosUseCase;
    private final CompararMunicipiosUseCase compararMunicipiosUseCase;

    @Inject
    public ComparacionResource(
            CompararEstadosUseCase compararEstadosUseCase,
            CompararMunicipiosUseCase compararMunicipiosUseCase
    ) {
        this.compararEstadosUseCase = compararEstadosUseCase;
        this.compararMunicipiosUseCase = compararMunicipiosUseCase;
    }

    @GET
    @Path("/estados")
    public Response compararEstados(
            @QueryParam("periodoId") Integer periodoId,
            @QueryParam("ids") String ids
    ) {
        List<Integer> idsEstados = parseIds(ids);

        return Response.ok(
                compararEstadosUseCase.execute(periodoId, idsEstados)
        ).build();
    }

    private List<Integer> parseIds(String ids) {
        if (ids == null || ids.isBlank()) {
            return List.of();
        }

        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    @GET
    @Path("/municipios")
    public Response compararMunicipios(
            @QueryParam("periodoId") Integer periodoId,
            @QueryParam("ids") String ids
    ) {
        List<Integer> idsMunicipios = parseIds(ids);

        return Response.ok(
                compararMunicipiosUseCase.execute(periodoId, idsMunicipios)
        ).build();
    }
}
