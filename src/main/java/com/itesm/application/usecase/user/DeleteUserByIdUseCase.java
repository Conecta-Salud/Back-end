package com.itesm.application.usecase.user;

import com.itesm.application.port.identity.IdentityProviderGateway;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.service.activity.ActivityActions;
import com.itesm.application.service.activity.ActivityLoggerService;
import com.itesm.application.service.activity.ActivityModules;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class DeleteUserByIdUseCase {

    private final UserRepository userRepository;
    private final IdentityProviderGateway identityProviderGateway;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ActivityLoggerService activityLoggerService;

    @Inject
    public DeleteUserByIdUseCase(
            UserRepository userRepository,
            IdentityProviderGateway identityProviderGateway,
            AuthenticatedUserContext authenticatedUserContext,
            ActivityLoggerService activityLoggerService
    ) {
        this.userRepository = userRepository;
        this.identityProviderGateway = identityProviderGateway;
        this.authenticatedUserContext = authenticatedUserContext;
        this.activityLoggerService = activityLoggerService;
    }

    public User execute(UUID userId) {
        User existingUser = userRepository.findUserById(userId);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        identityProviderGateway.disableUser(existingUser.getFirebaseUuid());

        User deactivatedUser = userRepository.deleteUserById(userId);

        CurrentUser adminUser = authenticatedUserContext.getCurrentUser();

        activityLoggerService.logSuccess(
                adminUser.getUserId(),
                ActivityActions.DEACTIVATE_USER,
                ActivityModules.USERS,
                "Deactivated user " + deactivatedUser.getEmail()
        );

        return deactivatedUser;
    }
}
