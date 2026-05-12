package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.UserProfileResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCurrentUserUseCase {

    private final AuthenticatedUserContext authenticatedUserContext;

    @Inject
    public GetCurrentUserUseCase(AuthenticatedUserContext authenticatedUserContext) {
        this.authenticatedUserContext = authenticatedUserContext;
    }

    public UserProfileResponseDto execute() {
        CurrentUser currentUser = authenticatedUserContext.getCurrentUser();

        return new UserProfileResponseDto(
                currentUser.getUserId(),
                currentUser.getIdDependencia(),
                currentUser.getNombreDependencia(),
                currentUser.getNombre(),
                currentUser.getApellidos(),
                currentUser.getEmail(),
                currentUser.getFirebaseUuid(),
                currentUser.getRol()
        );
    }
}