package com.itesm.application.dto.user;

import com.itesm.domain.models.user.UserRole;

import java.util.UUID;

public class UserProfileResponseDto {

    private UUID id;
    private Integer departmentId;
    private String departmentName;
    private String firstName;
    private String lastName;
    private String email;
    private String firebaseUuid;
    private UserRole role;

    public UserProfileResponseDto(
            UUID id,
            Integer departmentId,
            String departmentName,
            String firstName,
            String lastName,
            String email,
            String firebaseUuid,
            UserRole role
    ) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirebaseUuid() {
        return firebaseUuid;
    }

    public void setFirebaseUuid(String firebaseUuid) {
        this.firebaseUuid = firebaseUuid;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
