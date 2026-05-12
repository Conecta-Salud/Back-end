package com.itesm.application.dto.user;

import com.itesm.domain.models.usuario.UserRole;

import java.util.UUID;

public class UserProfileResponseDto {
    private UUID id;
    private Integer idDependencia;
    private String nombreDependencia;
    private String nombre;
    private String email;
    private String apellido;
    private String firebaseUuid;
    private UserRole rol;

    public UserProfileResponseDto(UUID id, Integer idDependencia, String nombreDependencia, String nombre, String apellido, String email, String firebaseUuid, UserRole rol) {
        this.id = id;
        this.idDependencia = idDependencia;
        this.nombreDependencia = nombreDependencia;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.rol = rol;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getIdDependencia() { return idDependencia; }
    public void setIdDependencia(Integer idDependencia) { this.idDependencia = idDependencia; }

    public String getNombreDependencia() { return nombreDependencia; }
    public void setNombreDependencia(String nombreDependencia) { this.nombreDependencia = nombreDependencia; }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
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
}
