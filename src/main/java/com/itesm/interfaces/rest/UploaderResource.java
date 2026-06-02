package com.itesm.interfaces.rest;

import com.itesm.application.usecase.Upload.Establecimientos.EstablecimientoUseCase;
import com.itesm.interfaces.tools.FileUploadForm;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/upload")
public class UploaderResource {
    @Inject
    EstablecimientoUseCase establecimientoUseCase;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/Establecimiento")
    public Response upload(@MultipartForm FileUploadForm form) {
        establecimientoUseCase.execute(form.fileContent);

        return Response.ok().build();
    }
}
