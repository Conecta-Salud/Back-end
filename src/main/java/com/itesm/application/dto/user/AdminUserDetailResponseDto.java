package com.itesm.application.dto.user;

import com.itesm.domain.models.user.UserRole;

import java.util.UUID;

public class AdminUserDetailResponseDto {

    private UUID id;
    private Integer departmentId;
    private String departmentName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean active;

    public AdminUserDetailResponseDto() {
    }

    public AdminUserDetailResponseDto(
            UUID id,
            Integer departmentId,
            String departmentName,
            String firstName,
            String lastName,
            String fullName,
            String email,
            UserRole role,
            boolean active
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
    }

    public UUID getId() {
        return id;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}