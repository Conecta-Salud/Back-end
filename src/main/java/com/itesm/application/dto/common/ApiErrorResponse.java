package com.itesm.application.dto.common;

import jakarta.json.bind.annotation.JsonbPropertyOrder;

import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonbPropertyOrder({"code", "message", "detail", "path", "timestamp"})
public class ApiErrorResponse {

    private final String code;
    private final String message;
    private final String detail;
    private final String path;
    private final LocalDateTime timestamp;

    public ApiErrorResponse(String code, String message, String detail, String path) {
        this.code = code;
        this.message = message;
        this.detail = detail;
        this.path = path;
        this.timestamp = LocalDateTime.now(ZoneId.systemDefault());
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
