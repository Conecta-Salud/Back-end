package com.itesm.interfaces.rest.error;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ForbiddenException exception) {
        return RestErrorResponseBuilder.controlled(
                exception,
                "ACCESS_DENIED",
                Response.Status.FORBIDDEN,
                uriInfo
        );
    }
}
