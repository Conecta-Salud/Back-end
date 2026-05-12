package com.itesm.application.security;


import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.NotAuthorizedException;

@RequestScoped
public class AuthenticatedUserContext {
    private CurrentUser currentUser;

    public CurrentUser getCurrentUser() {
        if (currentUser == null) {
            throw new NotAuthorizedException("Usuario autenticado no encontrado en el contexto solicitado");
        }

        return currentUser;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
