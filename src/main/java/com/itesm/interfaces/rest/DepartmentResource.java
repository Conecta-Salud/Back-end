package com.itesm.interfaces.rest;

import com.itesm.application.dto.department.DepartmentOptionsResponseDto;
import com.itesm.application.usecase.department.GetDepartmentOptionsUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/departments")
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentResource {

    private final GetDepartmentOptionsUseCase
            getDepartmentOptionsUseCase;

    @Inject
    public DepartmentResource(
            GetDepartmentOptionsUseCase getDepartmentOptionsUseCase
    ) {
        this.getDepartmentOptionsUseCase =
                getDepartmentOptionsUseCase;
    }

    @GET
    public Response getDepartments() {

        DepartmentOptionsResponseDto response =
                getDepartmentOptionsUseCase.execute();

        return Response.ok(response).build();
    }
}