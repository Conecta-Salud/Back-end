package com.itesm.interfaces.rest;

import com.itesm.application.dto.admin.uploads.*;
import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.service.activity.ActivityActions;
import com.itesm.application.service.activity.ActivityLoggerService;
import com.itesm.application.service.activity.ActivityModules;
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
import java.util.UUID;

@Path("/api/v1/admin/uploads")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUploadsResource {

    private final UploadBatchService uploadBatchService;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ActivityLoggerService activityLoggerService;

    public AdminUploadsResource(
            UploadBatchService uploadBatchService,
            AuthenticatedUserContext authenticatedUserContext,
            ActivityLoggerService activityLoggerService
    ) {
        this.uploadBatchService = uploadBatchService;
        this.authenticatedUserContext = authenticatedUserContext;
        this.activityLoggerService = activityLoggerService;
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

        logUploadSuccess(
                ActivityActions.UPLOAD_BATCH_CREATED,
                "Lote de carga " + response.getId()
                        + " creado para " + response.getSourceType()
                        + ", año " + response.getSourceYear()
                        + ", versión " + response.getBatchVersion()
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

            logUploadSuccess(
                    ActivityActions.UPLOAD_FILE_UPLOADED,
                    "Archivo " + response.getFile().getOriginalFileName()
                            + " subido al lote " + batchId
                            + " con rol " + response.getFile().getFileRole()
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

        ValidateUploadResponse response = uploadBatchService.validateUpload(uploadId);

        String detail = "Archivo de carga " + uploadId
                + " validado: total=" + response.getTotalRecords()
                + ", válidos=" + response.getValidRecords()
                + ", errores=" + response.getErrorRecords();

        if (response.getErrorRecords() != null && response.getErrorRecords() > 0) {
            logUploadWarning(ActivityActions.UPLOAD_FILE_VALIDATED, detail);
        } else {
            logUploadSuccess(ActivityActions.UPLOAD_FILE_VALIDATED, detail);
        }

        return response;
    }

    @POST
    @Path("/batches/{batchId}/process")
    @Consumes(MediaType.APPLICATION_JSON)
    public ProcessUploadBatchResponse processBatch(
            @PathParam("batchId") Integer batchId,
            ProcessUploadBatchRequest request
    ) {
        assertAdmin();

        ProcessUploadBatchResponse response = uploadBatchService.processBatch(batchId, request);

        String detail = safeDetail(response.getMessage());

        if ("error".equalsIgnoreCase(response.getStatus())) {
            logUploadError(ActivityActions.UPLOAD_BATCH_FAILED, detail);
        } else if ("warning".equalsIgnoreCase(response.getStatus())) {
            logUploadWarning(ActivityActions.UPLOAD_BATCH_PROCESSED, detail);
        } else {
            logUploadSuccess(ActivityActions.UPLOAD_BATCH_PROCESSED, detail);
        }

        return response;
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
            throw new ForbiddenException("USER_NOT_ADMIN: solo administradores pueden acceder a la administración de cargas");
        }
    }

    private InputPart requiredPart(MultipartFormDataInput input, String name) {
        if (input == null) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: el cuerpo multipart es obligatorio");
        }

        Map<String, List<InputPart>> form = input.getFormDataMap();
        List<InputPart> parts = form.get(name);

        if (parts == null || parts.isEmpty()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + name + " es obligatorio");
        }

        return parts.get(0);
    }

    private String requiredStringPart(MultipartFormDataInput input, String name) {
        InputPart part = requiredPart(input, name);

        try {
            String value = part.getBody(String.class, null);

            if (value == null || value.isBlank()) {
                throw new BadRequestException("REQUIRED_FIELD_MISSING: " + name + " es obligatorio");
            }

            return value.trim();
        } catch (IOException e) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: " + name + " es obligatorio");
        }
    }

    private String extractFileName(InputPart filePart) {
        String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");

        if (contentDisposition == null || contentDisposition.isBlank()) {
            throw new BadRequestException("REQUIRED_FIELD_MISSING: el nombre del archivo es obligatorio");
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

        throw new BadRequestException("REQUIRED_FIELD_MISSING: el nombre del archivo es obligatorio");
    }

    private UUID currentUserId() {
        return authenticatedUserContext.getCurrentUser().getUserId();
    }

    private void logUploadSuccess(String action, String detail) {
        activityLoggerService.logSuccess(
                currentUserId(),
                action,
                ActivityModules.DATA_UPLOADS,
                detail
        );
    }

    private void logUploadWarning(String action, String detail) {
        activityLoggerService.logWarning(
                currentUserId(),
                action,
                ActivityModules.DATA_UPLOADS,
                detail
        );
    }

    private void logUploadError(String action, String detail) {
        activityLoggerService.logError(
                currentUserId(),
                action,
                ActivityModules.DATA_UPLOADS,
                detail
        );
    }

    private String safeDetail(String detail) {
        if (detail == null || detail.isBlank()) {
            return null;
        }

        return detail.length() > 900 ? detail.substring(0, 900) + "..." : detail;
    }
}
