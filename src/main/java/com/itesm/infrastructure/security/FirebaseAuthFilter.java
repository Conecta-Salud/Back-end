package com.itesm.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import io.quarkus.arc.profile.UnlessBuildProfile;
import java.io.IOException;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
@UnlessBuildProfile("test")
public class FirebaseAuthFilter implements ContainerRequestFilter {
    @Inject
    UserRepository userRepository;
    @Inject
    AuthenticatedUserContext authenticatedUserContext;


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = normalizePath(requestContext.getUriInfo().getPath());
        String method = requestContext.getMethod();

        if (isPublicRoute(path, method)) {
            return;
        }

        String authHeader = requestContext.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String idToken = authHeader.substring("Bearer ".length()).trim();

        if (idToken.isBlank()) {
            abortUnauthorized(requestContext, "Empty Firebase token");
            return;
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken, true);

            Optional<User> userOptional = userRepository.findByFirebaseUuid(decodedToken.getUid());

            if (userOptional.isEmpty()) {
                abortUnauthorized(requestContext, "User not registered in database");
                return;
            }

            User user = userOptional.get();

            if (!user.isActive()) {
                abortUnauthorized(requestContext, "User is inactive");
                return;
            }

            CurrentUser currentUser = new CurrentUser(
                    user.getId(),
                    user.getDepartmentId(),
                    user.getDepartmentName(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getFirebaseUuid(),
                    user.getRole()
            );

            authenticatedUserContext.setCurrentUser(currentUser);

        } catch (FirebaseAuthException e) {
            abortUnauthorized(requestContext, "Invalid Firebase token");
        }
    }

    private boolean isPublicRoute(String path, String method) {
        return HttpMethod.OPTIONS.equalsIgnoreCase(method);
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }

        String normalized = path.trim();

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(message)
                        .build()
        );
    }
}