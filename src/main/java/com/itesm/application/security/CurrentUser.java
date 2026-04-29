package com.itesm.application.security;
import com.itesm.domain.models.usuario.UserRole;

import java.util.UUID;

public class CurrentUser {
    private final UUID userId;
    private final Integer idDependencia;
    private final String nombreDependencia;
    private final String nombre;
    private final String apellidos;
    private final String email;
    private final String firebaseUuid;
    private final UserRole rol;

    public CurrentUser(UUID userId, Integer idDependencia, String nombreDependencia, String nombre, String apellidos, String email, String firebaseUuid, UserRole rol) {
        this.userId = userId;
        this.idDependencia = idDependencia;
        this.nombreDependencia = nombreDependencia;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.rol = rol;
    }

    public boolean hasRol(UserRole rol){
        return this.rol.equals(rol);
    }

    public UUID getUserId() {
        return userId;
    }

    public Integer getIdDependencia() { return idDependencia; }

    public String getNombreDependencia() { return nombreDependencia; }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public String getFirebaseUuid() {
        return firebaseUuid;
    }

    public UserRole getRol() {
        return rol;
    }


}
