package com.itesm.application.security;

import java.util.UUID;

public class CurrentUser {
    private final UUID userId;
    private final String firebaseUuid;
    private final String email;
    private final String role;
    private final String fullName;

    public CurrentUser(UUID userId, String firebaseUuid, String email, String role, String fullName) {
        this.userId = userId;
        this.firebaseUuid = firebaseUuid;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }

    public boolean hasRole(String role){
        return this.role.equals(role);
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFirebaseUuid() {
        return firebaseUuid;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }
}
