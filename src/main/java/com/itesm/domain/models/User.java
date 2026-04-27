package com.itesm.domain.models;
import java.util.UUID;

public class User {
    private UUID id;
    private String fullName;
    private String email;
    private boolean active;
    private String firebaseUuid;
    private String role;
    public User() {}
    public User(UUID id, String username, String fullName, String email, boolean active, String firebaseUuid, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.active = active;
        this.firebaseUuid = firebaseUuid;
        this.role=role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFirebaseUuid() {
        return firebaseUuid;
    }

    public void setFirebaseUuid(String firebaseUuid) {
        this.firebaseUuid = firebaseUuid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
