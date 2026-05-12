package com.itesm.domain.models.usuario;
import java.util.UUID;

public class User {
    private UUID id;
    private Integer idDependencia;
    private String nombreDependencia;
    private String nombre;
    private String apellidos;
    private String email;
    private String firebaseUuid;
    private UserRole rol;
    private boolean isActive;

    public User() {}

    public User(UUID id, Integer idDependencia, String nombreDependencia,String nombre, String apellidos, String email, String firebaseUuid, UserRole rol,  boolean isActive) {
        this.id = id;
        this.idDependencia = idDependencia;
        this.nombreDependencia = nombreDependencia;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.firebaseUuid = firebaseUuid;
        this.rol=rol;
        this.isActive = isActive;
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
}
