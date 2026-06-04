package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.uploads.*;
import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.service.upload.UploadBatchService;
import com.itesm.domain.models.user.UserRole;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/api/v1/admin/uploads")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUploadsResource {

    private final UploadBatchService uploadBatchService;
    private final AuthenticatedUserContext authenticatedUserContext;

    public AdminUploadsResource(
            UploadBatchService uploadBatchService,
            AuthenticatedUserContext authenticatedUserContext
    ) {
        this.uploadBatchService = uploadBatchService;
        this.authenticatedUserContext = authenticatedUserContext;
    }

    @POST
    @Path("/batches")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBatch(CreateUploadBatchRequest request) {
        assertAdmin();

        UploadBatchResponse response = uploadBatchService.createBatch(
                request,
                authenticatedUserContext.getCurrentUser().getUserId()
        );

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @POST
    @Path("/batches/{batchId}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @PathParam("batchId") Integer batchId,
            MultipartFormDataInput input
    ) {
        assertAdmin();

        InputPart filePart = requiredPart(input, "file");
        String fileRole = requiredStringPart(input, "fileRole");
        String originalFileName = extractFileName(filePart);
        String mimeType = filePart.getMediaType() == null ? null : filePart.getMediaType().toString();

        try (InputStream stream = filePart.getBody(InputStream.class, null)) {
            UploadFileResponse response = uploadBatchService.uploadFile(
                    batchId,
                    fileRole,
                    originalFileName,
                    mimeType,
                    stream
            );

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (IOException e) {
            throw new BadRequestException("UPLOAD_STORAGE_ERROR: Could not read uploaded file");
        }
    }

    @POST
    @Path("/{uploadId}/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    public ValidateUploadResponse validateUpload(@PathParam("uploadId") Integer uploadId) {
        assertAdmin();
        return uploadBatchService.validateUpload(uploadId);
    }

    @POST
    @Path("/batches/{batchId}/process")
    @Consumes(MediaType.APPLICATION_JSON)
    public ProcessUploadBatchResponse processBatch(
            @PathParam("batchId") Integer batchId,
            ProcessUploadBatchRequest request
    ) {
        assertAdmin();
        return uploadBatchService.processBatch(batchId, request);
    }

    @GET
    @Path("/batches")
    public PageResponseDto<UploadBatchResponse> findBatches(
            @QueryParam("sourceType") String sourceType,
            @QueryParam("sourceYear") Integer sourceYear,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        assertAdmin();
        return uploadBatchService.findBatches(sourceType, sourceYear, status, page, size);
    }

    @GET
    @Path("/batches/{batchId}")
    public UploadBatchDetailResponse findBatchDetail(@PathParam("batchId") Integer batchId) {
        assertAdmin();
        return uploadBatchService.findBatchDetail(batchId);
    }

    @GET
    @Path("/batches/{batchId}/errors")
    public PageResponseDto<UploadErrorResponse> findBatchErrors(
            @PathParam("batchId") Integer batchId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size
    ) {
        assertAdmin();
        return uploadBatchService.findBatchErrors(batchId, page, size);
    }

    @GET
    @Path("/{uploadId}/errors")
    public PageResponseDto<UploadErrorResponse> findUploadErrors(
            @PathParam("uploadId") Integer uploadId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        assertAdmin();
        return uploadBatchService.findUploadErrors(uploadId, page, size);
    }

    private void assertAdmin() {
        if (authenticatedUserContext.getCurrentUser().getRole() != UserRole.admin) {
            throw new ForbiddenException("USER_NOT_ADMIN: Only administrators can access upload administration");
        }
    }

    private InputPart requiredPart(MultipartFormDataInput input, String name) {
        if (input == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: multipart body is required");
        }

        Map<String, List<InputPart>> form = input.getFormDataMap();
        List<InputPart> parts = form.get(name);

        if (parts == null || parts.isEmpty()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + name + " is required");
        }

        return parts.get(0);
    }

    private String requiredStringPart(MultipartFormDataInput input, String name) {
        InputPart part = requiredPart(input, name);

        try {
            String value = part.getBody(String.class, null);

            if (value == null || value.isBlank()) {
                throw new BadRequestException("REQUIRED_FIELD_MISSING: " + name + " is required");
            }

            return value.trim();
        } catch (IOException e) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + name + " is required");
        }
    }

    private String extractFileName(InputPart filePart) {
        String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");

        if (contentDisposition == null || contentDisposition.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: file name is required");
        }

        for (String token : contentDisposition.split(";")) {
            String trimmed = token.trim();

            if (trimmed.startsWith("filename=")) {
                String fileName = trimmed.substring("filename=".length()).trim();

                if (fileName.startsWith("\"") && fileName.endsWith("\"") && fileName.length() >= 2) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }

                if (!fileName.isBlank()) {
                    return fileName;
                }
            }
        }

        throw new BadRequestException("REQUIRED_FIELD_MISSING: file name is required");
    }
}
