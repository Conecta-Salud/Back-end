package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.usuario.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="usuarios")
@NamedEntityGraph(
        name = "User.withDependencia",
        attributeNodes = {
                @NamedAttributeNode("dependencia")
        }
)

public class UserEntity {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dependencia", nullable = false)
    private DependenciaEntity dependencia;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String apellidos;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(name="firebase_uuid", unique = true, length = 128, nullable = false)
    private String firebaseUuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole rol;

    @Column(name="is_active", nullable = false)
    private boolean isActive;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public UserEntity(){}
    public UserEntity(UUID id, DependenciaEntity dependencia ,String nombre, String apellidos, String email, String firebaseUuid, UserRole rol, boolean isActive,  LocalDateTime lastLoginAt) {
        this.id = id;
        this.dependencia = dependencia;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.rol = rol;
        this.isActive = isActive;
        this.lastLoginAt = lastLoginAt;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) { this.id = id; }

    public DependenciaEntity getDependencia() { return dependencia; }
    public void setDependencia(DependenciaEntity dependencia) { this.dependencia = dependencia; }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public UserRole getRol() {
        return rol;
    }
    public void setRol(UserRole rol) {
        this.rol = rol;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
