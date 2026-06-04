package com.itesm.interfaces.rest.error;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        return RestErrorResponseBuilder.controlled(
                exception,
                "UNAUTHENTICATED",
                Response.Status.UNAUTHORIZED,
                uriInfo
        );
    }
}
