package com.itesm.application.security;
import com.itesm.domain.models.user.UserRole;

import java.util.UUID;

public class CurrentUser {

    private final UUID userId;
    private final Integer departmentId;
    private final String departmentName;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String firebaseUuid;
    private final UserRole role;

    public CurrentUser(
            UUID userId,
            Integer departmentId,
            String departmentName,
            String firstName,
            String lastName,
            String email,
            String firebaseUuid,
            UserRole role
    ) {
        this.userId = userId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.role = role;
    }

    public boolean hasRole(UserRole role) {
        return this.role != null && this.role.equals(role);
    }

    public boolean isAdmin() {
        return UserRole.admin.equals(this.role);
    }

    public boolean isStrategic() {
        return UserRole.strategic.equals(this.role);
    }

    public UUID getUserId() {
        return userId;
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

    public String getEmail() {
        return email;
    }

    public String getFirebaseUuid() {
        return firebaseUuid;
    }

    public UserRole getRole() {
        return role;
    }
}
