package com.itesm.application.dto.common;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ApiErrorResponses {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String AUTH_PROVIDER_CONFIGURATION_ERROR = "AUTH_PROVIDER_CONFIGURATION_ERROR";

    private static final Pattern CODE_PREFIX = Pattern.compile("^([A-Z][A-Z0-9_]*)(?:\\s*:\\s*(.*))?$");

    private static final Set<String> BAD_REQUEST_CODES = Set.of(
            "INVALID_FILE_TYPE",
            "FILE_TOO_LARGE",
            "EMPTY_FILE",
            "MISSING_REQUIRED_HEADER",
            "DUPLICATED_FILE",
            "DUPLICATED_FILE_ROLE_IN_BATCH",
            "EXPECTED_FILES_EXCEEDED",
            "INVALID_FILE_ROLE",
            "INVALID_FILE_ROLE_FOR_SOURCE_TYPE",
            "INVALID_SOURCE_TYPE",
            "INVALID_BATCH_STATUS",
            "INVALID_UPLOAD_STATUS",
            "INVALID_PROCESSING_MODE",
            "INVALID_YEAR",
            "INVALID_NUMERIC_VALUE",
            "REQUIRED_FIELD_MISSING",
            "BATCH_HAS_ERRORS",
            "INVALID_FILE_NAME",
            "INVALID_STATUS",
            "UPLOAD_STORAGE_ERROR",
            "BAD_REQUEST"
    );

    private static final Set<String> NOT_FOUND_CODES = Set.of(
            "UNKNOWN_BATCH",
            "UNKNOWN_UPLOAD",
            "UNKNOWN_DATA_SOURCE",
            "UNKNOWN_USER",
            "UNKNOWN_INDICATOR",
            "UNKNOWN_SPECIALTY",
            "UNKNOWN_INFRASTRUCTURE_TYPE",
            "UNKNOWN_PERIOD",
            "UNKNOWN_HEALTH_UNIT",
            "NOT_FOUND"
    );

    private static final Set<String> FORBIDDEN_CODES = Set.of(
            "USER_NOT_ADMIN",
            "ACCESS_DENIED"
    );

    private static final Set<String> UNAUTHORIZED_CODES = Set.of(
            "UNAUTHENTICATED",
            "INVALID_TOKEN"
    );

    private static final Set<String> SERVICE_UNAVAILABLE_CODES = Set.of(
            AUTH_PROVIDER_CONFIGURATION_ERROR
    );

    private static final Map<String, String> MESSAGES = Map.ofEntries(
            Map.entry("INVALID_FILE_TYPE", "Solo se permiten archivos CSV."),
            Map.entry("FILE_TOO_LARGE", "El archivo excede el tamaño máximo permitido."),
            Map.entry("EMPTY_FILE", "El archivo CSV está vacío."),
            Map.entry("MISSING_REQUIRED_HEADER", "El archivo CSV no contiene todos los encabezados requeridos."),
            Map.entry("DUPLICATED_FILE", "Este archivo ya fue cargado en el lote."),
            Map.entry("DUPLICATED_FILE_ROLE_IN_BATCH", "Ya existe un archivo con ese rol en el lote."),
            Map.entry("EXPECTED_FILES_EXCEEDED", "El lote ya alcanzó el número esperado de archivos."),
            Map.entry("INVALID_FILE_ROLE", "El rol del archivo no es válido."),
            Map.entry("INVALID_FILE_ROLE_FOR_SOURCE_TYPE", "El tipo de archivo no corresponde con el tipo de carga seleccionado."),
            Map.entry("INVALID_SOURCE_TYPE", "El tipo de carga no es válido."),
            Map.entry("INVALID_BATCH_STATUS", "El estado actual del lote no permite esta operación."),
            Map.entry("INVALID_UPLOAD_STATUS", "El estado actual del archivo no permite esta operación."),
            Map.entry("INVALID_PROCESSING_MODE", "El modo de procesamiento no es válido."),
            Map.entry("INVALID_YEAR", "El año proporcionado no es válido."),
            Map.entry("INVALID_NUMERIC_VALUE", "El archivo contiene un valor numérico inválido."),
            Map.entry("REQUIRED_FIELD_MISSING", "Falta un campo requerido."),
            Map.entry("BATCH_HAS_ERRORS", "El lote contiene errores y no puede procesarse con failOnErrors activado."),
            Map.entry("UNKNOWN_BATCH", "No se encontró el lote de carga solicitado."),
            Map.entry("UNKNOWN_UPLOAD", "No se encontró el archivo de carga solicitado."),
            Map.entry("UNKNOWN_DATA_SOURCE", "No se encontró la fuente de datos solicitada."),
            Map.entry("UNKNOWN_USER", "No se encontró el usuario solicitado."),
            Map.entry("UNKNOWN_INDICATOR", "No se encontró el indicador solicitado."),
            Map.entry("UNKNOWN_SPECIALTY", "No se encontró la especialidad requerida."),
            Map.entry("UNKNOWN_INFRASTRUCTURE_TYPE", "No se encontró el tipo de infraestructura requerido."),
            Map.entry("UNKNOWN_PERIOD", "No se encontró el periodo solicitado."),
            Map.entry("UNKNOWN_HEALTH_UNIT", "No se encontró la unidad de salud solicitada."),
            Map.entry("USER_NOT_ADMIN", "El usuario autenticado no tiene permisos de administrador."),
            Map.entry("ACCESS_DENIED", "Acceso denegado."),
            Map.entry("UNAUTHENTICATED", "No se encontró una sesión autenticada."),
            Map.entry("INVALID_TOKEN", "El token de autenticación no es válido."),
            Map.entry(AUTH_PROVIDER_CONFIGURATION_ERROR, "El servicio de autenticación no está configurado correctamente."),
            Map.entry("INVALID_FILE_NAME", "El nombre del archivo no es válido."),
            Map.entry("INVALID_STATUS", "El estado solicitado no es válido."),
            Map.entry("UPLOAD_STORAGE_ERROR", "No fue posible guardar o leer el archivo cargado."),
            Map.entry("BAD_REQUEST", "La solicitud no es válida."),
            Map.entry("NOT_FOUND", "No se encontró el recurso solicitado."),
            Map.entry(INTERNAL_SERVER_ERROR, "Ocurrió un error interno inesperado.")
    );

    private static final Map<String, String> DEFAULT_DETAILS = Map.ofEntries(
            Map.entry(AUTH_PROVIDER_CONFIGURATION_ERROR, "Revise la configuración del proveedor de autenticación en el servidor."),
            Map.entry("INVALID_BATCH_STATUS", "El lote solo puede procesarse cuando está pendiente o en advertencia."),
            Map.entry("INVALID_UPLOAD_STATUS", "El archivo solo puede validarse cuando está pendiente, en advertencia o en error."),
            Map.entry("BATCH_HAS_ERRORS", "El lote tiene errores de validación. Revise la tabla de errores antes de procesar."),
            Map.entry("DUPLICATED_FILE", "Ya existe un archivo con el mismo contenido dentro de este lote."),
            Map.entry("EXPECTED_FILES_EXCEEDED", "El número de archivos cargados ya alcanzó el máximo esperado para este lote.")
    );

    private ApiErrorResponses() {
    }

    public static ApiErrorResponse fromException(Throwable exception, String fallbackCode, String rawPath) {
        ErrorParts parts = parse(exception == null ? null : exception.getMessage(), fallbackCode);
        String code = parts.code();
        String message = messageFor(code, fallbackCode);

        return new ApiErrorResponse(
                code,
                message,
                detailFor(code, parts.detail()),
                normalizePath(rawPath)
        );
    }

    public static ApiErrorResponse fromCode(String code, String detail, String rawPath) {
        String effectiveCode = isBlank(code) ? INTERNAL_SERVER_ERROR : code.trim();

        return new ApiErrorResponse(
                effectiveCode,
                messageFor(effectiveCode, INTERNAL_SERVER_ERROR),
                detailFor(effectiveCode, detail),
                normalizePath(rawPath)
        );
    }

    public static ApiErrorResponse internalServerError(String rawPath) {
        return fromCode(INTERNAL_SERVER_ERROR, null, rawPath);
    }

    public static int statusFor(String code, int fallbackStatus) {
        if (BAD_REQUEST_CODES.contains(code)) {
            return 400;
        }

        if (NOT_FOUND_CODES.contains(code)) {
            return 404;
        }

        if (FORBIDDEN_CODES.contains(code)) {
            return 403;
        }

        if (UNAUTHORIZED_CODES.contains(code)) {
            return 401;
        }

        if (SERVICE_UNAVAILABLE_CODES.contains(code)) {
            return 503;
        }

        if (INTERNAL_SERVER_ERROR.equals(code)) {
            return 500;
        }

        return fallbackStatus;
    }

    private static ErrorParts parse(String rawMessage, String fallbackCode) {
        String code = isBlank(fallbackCode) ? INTERNAL_SERVER_ERROR : fallbackCode.trim();

        if (isBlank(rawMessage)) {
            return new ErrorParts(code, null);
        }

        String message = rawMessage.trim();
        Matcher matcher = CODE_PREFIX.matcher(message);

        if (matcher.matches()) {
            return new ErrorParts(matcher.group(1), matcher.group(2));
        }

        return new ErrorParts(code, message);
    }

    private static String messageFor(String code, String fallbackCode) {
        String message = MESSAGES.get(code);

        if (message != null) {
            return message;
        }

        return MESSAGES.getOrDefault(fallbackCode, MESSAGES.get(INTERNAL_SERVER_ERROR));
    }

    private static String normalizePath(String rawPath) {
        if (isBlank(rawPath)) {
            return "/";
        }

        String path = rawPath.trim();
        return path.startsWith("/") ? path : "/" + path;
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static String detailFor(String code, String detail) {
        String defaultDetail = DEFAULT_DETAILS.get(code);

        if (defaultDetail != null) {
            return defaultDetail;
        }

        String sanitizedDetail = blankToNull(detail);

        if (sanitizedDetail != null) {
            return translateKnownDetail(sanitizedDetail);
        }

        return null;
    }

    private static String translateKnownDetail(String detail) {
        return switch (detail) {
            case "Batch can only be processed while pending or warning" ->
                    "El lote solo puede procesarse cuando está pendiente o en advertencia.";
            case "Files can only be uploaded to pending or warning batches" ->
                    "Solo se pueden subir archivos a lotes pendientes o en advertencia.";
            case "Upload can only be validated while pending, warning or error" ->
                    "El archivo solo puede validarse cuando está pendiente, en advertencia o en error.";
            case "Batch has no uploaded files" ->
                    "El lote no tiene archivos cargados.";
            case "A file with the same checksum already exists in this batch" ->
                    "Ya existe un archivo con el mismo contenido dentro de este lote.";
            case "CSV file exceeds maximum allowed size" ->
                    "El archivo CSV excede el tamaño máximo permitido.";
            case "Only .csv files are accepted" ->
                    "Solo se aceptan archivos .csv.";
            default -> detail;
        };
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ErrorParts(String code, String detail) {
    }
}
