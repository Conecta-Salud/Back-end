package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.overview.AdminOverviewResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.usecase.admin.overview.GetAdminOverviewUseCase;
import com.itesm.domain.models.user.UserRole;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/admin/overview")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminOverviewResource {

    private final GetAdminOverviewUseCase getAdminOverviewUseCase;
    private final AuthenticatedUserContext authenticatedUserContext;

    public AdminOverviewResource(
            GetAdminOverviewUseCase getAdminOverviewUseCase,
            AuthenticatedUserContext authenticatedUserContext
    ) {
        this.getAdminOverviewUseCase = getAdminOverviewUseCase;
        this.authenticatedUserContext = authenticatedUserContext;
    }

    @GET
    public Response getOverview() {
        assertAdmin();

        AdminOverviewResponseDto response = getAdminOverviewUseCase.execute();

        return Response.ok(response).build();
    }

    private void assertAdmin() {
        if (authenticatedUserContext.getCurrentUser().getRole() != UserRole.admin) {
            throw new ForbiddenException("Only administrators can access admin overview");
        }
    }
}