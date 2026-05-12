package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.user.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="users")
@NamedEntityGraph(
        name = "User.withDepartment",
        attributeNodes = {
                @NamedAttributeNode("department")
        }
)

public class UserEntity {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(name="firebase_uuid", unique = true, length = 128, nullable = false)
    private String firebaseUuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name="is_active", nullable = false)
    private boolean isActive;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public UserEntity(){}
    public UserEntity(UUID id, DepartmentEntity department , String firstName, String lastName, String email, String firebaseUuid, UserRole role, boolean isActive, LocalDateTime lastLoginAt) {
        this.id = id;
        this.department = department;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.role = role;
        this.isActive = isActive;
        this.lastLoginAt = lastLoginAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentEntity department) {
        this.department = department;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
