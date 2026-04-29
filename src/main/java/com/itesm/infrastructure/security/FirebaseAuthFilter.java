package com.itesm.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.domain.models.usuario.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
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
        String path = requestContext.getUriInfo().getPath();
        if(path.equals("/user")){
            return;
        }
        String authHeader = requestContext.getHeaders().getFirst("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            requestContext.abortWith(
                    Response.status(401).build()
            );
        }
        try {
            assert authHeader != null;
            FirebaseToken decodedToken= FirebaseAuth.getInstance().verifyIdToken(authHeader.replace("Bearer ",""),true);
            Optional<User> userOptional = userRepository.findByFirebaseUuid(decodedToken.getUid());
            if(userOptional.isEmpty()){
                requestContext.abortWith(
                        Response.status(401).build()
                );
            }
            User user= userOptional.get();
            CurrentUser currentUser= new CurrentUser(
                    user.getId(),
                    user.getIdDependencia(),
                    user.getNombreDependencia(),
                    user.getNombre(),
                    user.getApellidos(),
                    user.getEmail(),
                    user.getFirebaseUuid(),
                    user.getRol()
            );
            authenticatedUserContext.setCurrentUser(currentUser);
        } catch (FirebaseAuthException e) {
            requestContext.abortWith(
                    Response.status(401).build()
            );
        }

    }
}
