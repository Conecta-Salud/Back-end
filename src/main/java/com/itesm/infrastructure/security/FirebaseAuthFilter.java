package com.itesm.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.itesm.application.dto.common.ApiErrorResponse;
import com.itesm.application.dto.common.ApiErrorResponses;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
@UnlessBuildProfile("test")
public class FirebaseAuthFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(FirebaseAuthFilter.class);
    private static final String AUTH_PROVIDER_CONFIGURATION_DETAIL =
            "Revise la configuración del proveedor de autenticación en el servidor.";

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
            abortUnauthorized(requestContext, "UNAUTHENTICATED", "Missing or invalid Authorization header");
            return;
        }

        String idToken = authHeader.substring("Bearer ".length()).trim();

        if (idToken.isBlank()) {
            abortUnauthorized(requestContext, "UNAUTHENTICATED", "Empty Firebase token");
            return;
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken, true);

            Optional<User> userOptional = userRepository.findByFirebaseUuid(decodedToken.getUid());

            if (userOptional.isEmpty()) {
                LOG.warnf("No database user found for firebase_uuid=%s", decodedToken.getUid());
                abortUnauthorized(requestContext, "UNAUTHENTICATED", "User not registered in database");
                return;
            }

            User user = userOptional.get();

            if (!user.isActive()) {
                abortUnauthorized(requestContext, "UNAUTHENTICATED", "User is inactive");
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
            if (isAuthProviderConfigurationError(e)) {
                LOG.error("Firebase Admin authentication provider is not configured correctly.", e);
                abortServiceUnavailable(requestContext);
                return;
            }

            LOG.warn("Firebase token verification failed.");
            abortUnauthorized(requestContext, "INVALID_TOKEN", "Invalid Firebase token");
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

    private boolean isAuthProviderConfigurationError(FirebaseAuthException exception) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return false;
        }

        String normalized = message.toLowerCase(Locale.ROOT);

        return normalized.contains("error getting access token for service account")
                || normalized.contains("invalid_grant")
                || normalized.contains("invalid jwt signature")
                || normalized.contains("service account");
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String code, String detail) {
        abortWithStatus(requestContext, Response.Status.UNAUTHORIZED, code, detail);
    }

    private void abortServiceUnavailable(ContainerRequestContext requestContext) {
        abortWithStatus(
                requestContext,
                Response.Status.SERVICE_UNAVAILABLE,
                ApiErrorResponses.AUTH_PROVIDER_CONFIGURATION_ERROR,
                AUTH_PROVIDER_CONFIGURATION_DETAIL
        );
    }

    private void abortWithStatus(
            ContainerRequestContext requestContext,
            Response.Status status,
            String code,
            String detail
    ) {
        ApiErrorResponse body = ApiErrorResponses.fromCode(
                code,
                detail,
                requestContext.getUriInfo().getPath()
        );

        requestContext.abortWith(
                Response.status(status)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(body)
                        .build()
        );
    }
}
