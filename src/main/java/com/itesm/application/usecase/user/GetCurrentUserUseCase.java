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
                currentUser.getDepartmentId(),
                currentUser.getDepartmentName(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                currentUser.getFirebaseUuid(),
                currentUser.getRole()
        );
    }
}