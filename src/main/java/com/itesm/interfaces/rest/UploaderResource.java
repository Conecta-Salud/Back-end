package com.itesm.interfaces.rest;

import com.itesm.application.usecase.Upload.Establecimientos.EstablecimientoUseCase;
import com.itesm.application.usecase.Upload.Indicadores.IndicadoresUseCase;
import com.itesm.application.usecase.Upload.Sectoriales.SectorialesUseCase;
import com.itesm.interfaces.tools.FileUploadForm;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Path("/upload")
public class UploaderResource {

    @Inject
    EstablecimientoUseCase establecimientoUseCase;

    @Inject
    IndicadoresUseCase indicadoresUseCase;

    @Inject
    SectorialesUseCase sectorialesUseCase;
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/Establecimiento")
    public Response upload(@MultipartForm FileUploadForm form) {
        String csvContent = readCsvContent(form);

        try {
            establecimientoUseCase.execute(csvContent);
            return Response.ok("Archivo Establecimiento procesado correctamente").build();
        } catch (Exception e) {
            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error procesando CSV de Establecimiento: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/Indicadores")
    public Response uploadIndicators(@MultipartForm FileUploadForm form) {
        String csvContent = readCsvContent(form, StandardCharsets.UTF_8);

        try {
            indicadoresUseCase.execute(csvContent);
            return Response.ok("Archivo Indicadores procesado correctamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error procesando CSV de Indicadores: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/Sectoriales")
    public Response uploadSectoriales(@MultipartForm FileUploadForm form) {
        String csvContent = readCsvContent(form);

        try {
            sectorialesUseCase.execute(csvContent);
            return Response.ok("Archivo Sectoriales procesado correctamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error procesando CSV de Sectoriales: " + e.getMessage())
                    .build();
        }
    }

    private String readCsvContent(FileUploadForm form) {
        return readCsvContent(form, StandardCharsets.UTF_8);
    }

    private String readCsvContent(FileUploadForm form, java.nio.charset.Charset charset) {
        if (form == null || form.fileContent == null) {
            throw new BadRequestException("No se recibió archivo. El campo multipart debe llamarse fileContent.");
        }

        try {
            byte[] bytes = form.fileContent.readAllBytes();

            if (bytes.length == 0) {
                throw new BadRequestException("El archivo CSV está vacío.");
            }

            String content = new String(bytes, charset);

            if (!looksLikeCsv(content)) {
                throw new BadRequestException("El archivo recibido no parece ser un CSV válido.");
            }

            return content;
        } catch (IOException e) {
            throw new WebApplicationException(
                    "No se pudo leer el archivo CSV.",
                    e,
                    Response.Status.BAD_REQUEST
            );
        }
    }
    private boolean looksLikeCsv(String content) {
        String trimmed = content.trim();

        if (trimmed.isEmpty()) {
            return false;
        }

        return trimmed.length() > 0;
    }
}

