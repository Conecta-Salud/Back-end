package com.itesm.interfaces.rest.error;

import com.itesm.application.dto.common.ApiErrorResponse;
import com.itesm.application.dto.common.ApiErrorResponses;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

final class RestErrorResponseBuilder {

    private RestErrorResponseBuilder() {
    }

    static Response controlled(Throwable exception, String fallbackCode, Response.Status fallbackStatus, UriInfo uriInfo) {
        ApiErrorResponse body = ApiErrorResponses.fromException(exception, fallbackCode, path(uriInfo));
        int statusCode = ApiErrorResponses.statusFor(body.getCode(), fallbackStatus.getStatusCode());

        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    static Response internalServerError(UriInfo uriInfo) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiErrorResponses.internalServerError(path(uriInfo)))
                .build();
    }

    private static String path(UriInfo uriInfo) {
        return uriInfo == null ? null : uriInfo.getPath();
    }
}
