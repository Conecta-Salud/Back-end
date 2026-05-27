package com.itesm.application.dto.admin.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActivityLogResponseDto {

    private Integer id;
    private UUID userId;
    private String userEmail;
    private String userFullName;
    private String action;
    private String module;
    private String result;
    private String detail;
    private LocalDateTime createdAt;

    public ActivityLogResponseDto(
            Integer id,
            UUID userId,
            String userEmail,
            String userFullName,
            String action,
            String module,
            String result,
            String detail,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.action = action;
        this.module = module;
        this.result = result;
        this.detail = detail;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
