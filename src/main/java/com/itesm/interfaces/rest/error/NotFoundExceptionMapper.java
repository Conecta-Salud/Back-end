package com.itesm.interfaces.rest.error;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NotFoundException exception) {
        return RestErrorResponseBuilder.controlled(
                exception,
                "NOT_FOUND",
                Response.Status.NOT_FOUND,
                uriInfo
        );
    }
}
