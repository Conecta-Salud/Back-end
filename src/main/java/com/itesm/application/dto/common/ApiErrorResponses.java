package com.itesm.application.dto.common;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ApiErrorResponses {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

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

    private static final Map<String, String> MESSAGES = Map.ofEntries(
            Map.entry("INVALID_FILE_TYPE", "Solo se permiten archivos CSV."),
            Map.entry("FILE_TOO_LARGE", "El archivo excede el tamaÃ±o mÃ¡ximo permitido."),
            Map.entry("EMPTY_FILE", "El archivo CSV estÃ¡ vacÃ­o."),
            Map.entry("MISSING_REQUIRED_HEADER", "El archivo CSV no contiene todos los encabezados requeridos."),
            Map.entry("DUPLICATED_FILE", "Este archivo ya fue cargado en el lote."),
            Map.entry("DUPLICATED_FILE_ROLE_IN_BATCH", "Ya existe un archivo con ese rol en el lote."),
            Map.entry("EXPECTED_FILES_EXCEEDED", "El lote ya alcanzÃ³ el nÃºmero esperado de archivos."),
            Map.entry("INVALID_FILE_ROLE", "El rol del archivo no es vÃ¡lido."),
            Map.entry("INVALID_FILE_ROLE_FOR_SOURCE_TYPE", "El tipo de archivo no corresponde con el tipo de carga seleccionado."),
            Map.entry("INVALID_SOURCE_TYPE", "El tipo de carga no es vÃ¡lido."),
            Map.entry("INVALID_BATCH_STATUS", "El estado actual del lote no permite esta operaciÃ³n."),
            Map.entry("INVALID_UPLOAD_STATUS", "El estado actual del archivo no permite esta operaciÃ³n."),
            Map.entry("INVALID_PROCESSING_MODE", "El modo de procesamiento no es vÃ¡lido."),
            Map.entry("INVALID_YEAR", "El aÃ±o proporcionado no es vÃ¡lido."),
            Map.entry("INVALID_NUMERIC_VALUE", "El archivo contiene un valor numerico invalido."),
            Map.entry("REQUIRED_FIELD_MISSING", "Falta un campo requerido."),
            Map.entry("BATCH_HAS_ERRORS", "El lote contiene errores y no puede procesarse con failOnErrors activado."),
            Map.entry("UNKNOWN_BATCH", "No se encontrÃ³ el lote de carga solicitado."),
            Map.entry("UNKNOWN_UPLOAD", "No se encontrÃ³ el archivo de carga solicitado."),
            Map.entry("UNKNOWN_DATA_SOURCE", "No se encontrÃ³ la fuente de datos solicitada."),
            Map.entry("UNKNOWN_USER", "No se encontrÃ³ el usuario solicitado."),
            Map.entry("UNKNOWN_INDICATOR", "No se encontrÃ³ el indicador solicitado."),
            Map.entry("UNKNOWN_SPECIALTY", "No se encontro la especialidad requerida."),
            Map.entry("UNKNOWN_INFRASTRUCTURE_TYPE", "No se encontro el tipo de infraestructura requerido."),
            Map.entry("UNKNOWN_PERIOD", "No se encontro el periodo solicitado."),
            Map.entry("UNKNOWN_HEALTH_UNIT", "No se encontro la unidad de salud solicitada."),
            Map.entry("USER_NOT_ADMIN", "El usuario autenticado no tiene permisos de administrador."),
            Map.entry("ACCESS_DENIED", "Acceso denegado."),
            Map.entry("UNAUTHENTICATED", "No se encontrÃ³ una sesiÃ³n autenticada."),
            Map.entry("INVALID_TOKEN", "El token de autenticaciÃ³n no es vÃ¡lido."),
            Map.entry("INVALID_FILE_NAME", "El nombre del archivo no es vÃ¡lido."),
            Map.entry("INVALID_STATUS", "El estado solicitado no es vÃ¡lido."),
            Map.entry("UPLOAD_STORAGE_ERROR", "No fue posible guardar o leer el archivo cargado."),
            Map.entry("BAD_REQUEST", "La solicitud no es vÃ¡lida."),
            Map.entry("NOT_FOUND", "No se encontrÃ³ el recurso solicitado."),
            Map.entry(INTERNAL_SERVER_ERROR, "OcurriÃ³ un error interno inesperado.")
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
                blankToNull(parts.detail()),
                normalizePath(rawPath)
        );
    }

    public static ApiErrorResponse fromCode(String code, String detail, String rawPath) {
        String effectiveCode = isBlank(code) ? INTERNAL_SERVER_ERROR : code.trim();

        return new ApiErrorResponse(
                effectiveCode,
                messageFor(effectiveCode, INTERNAL_SERVER_ERROR),
                blankToNull(detail),
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ErrorParts(String code, String detail) {
    }
}
