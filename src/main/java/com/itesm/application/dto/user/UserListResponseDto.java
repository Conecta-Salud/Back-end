package com.itesm.application.dto.user;

import com.itesm.domain.models.user.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserListResponseDto {

    private UUID id;
    private Integer departmentId;
    private String departmentName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean active;
    private LocalDateTime lastLoginAt;

    public UserListResponseDto(
            UUID id,
            Integer departmentId,
            String departmentName,
            String firstName,
            String lastName,
            String fullName,
            String email,
            UserRole role,
            boolean active,
            LocalDateTime lastLoginAt
    ) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}