package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.activity.ActivityLogResponseDto;
import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.usecase.admin.activity.FindActivityLogsUseCase;
import com.itesm.domain.models.user.UserRole;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

@Path("/admin/activity-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminActivityLogResource {

    private final FindActivityLogsUseCase findActivityLogsUseCase;
    private final AuthenticatedUserContext authenticatedUserContext;

    public AdminActivityLogResource(
            FindActivityLogsUseCase findActivityLogsUseCase,
            AuthenticatedUserContext authenticatedUserContext
    ) {
        this.findActivityLogsUseCase = findActivityLogsUseCase;
        this.authenticatedUserContext = authenticatedUserContext;
    }

    @GET
    public Response findActivityLogs(
            @QueryParam("query") String query,
            @QueryParam("action") String action,
            @QueryParam("module") String module,
            @QueryParam("result") String result,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        assertAdmin();

        PageResponseDto<ActivityLogResponseDto> response = findActivityLogsUseCase.execute(
                query,
                action,
                module,
                result,
                parseDateTime(from),
                parseDateTime(to),
                page,
                size
        );

        return Response.ok(response).build();
    }

    private void assertAdmin() {
        if (authenticatedUserContext.getCurrentUser().getRole() != UserRole.admin) {
            throw new ForbiddenException("Only administrators can access activity logs");
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value);
    }
}