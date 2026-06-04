package com.itesm.interfaces.rest.error;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(BadRequestException exception) {
        return RestErrorResponseBuilder.controlled(
                exception,
                "BAD_REQUEST",
                Response.Status.BAD_REQUEST,
                uriInfo
        );
    }
}
