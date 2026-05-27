package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.ChangeUserPasswordDto;
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
public class ChangeUserPasswordUseCase {

    private final UserRepository userRepository;
    private final IdentityProviderGateway identityProviderGateway;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ActivityLoggerService activityLoggerService;

    @Inject
    public ChangeUserPasswordUseCase(
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

    public void execute(UUID targetUserId, ChangeUserPasswordDto dto) {
        User targetUser = userRepository.findUserById(targetUserId);

        if (targetUser == null) {
            throw new NotFoundException("User not found");
        }

        identityProviderGateway.updatePassword(
                targetUser.getFirebaseUuid(),
                dto.getNewPassword()
        );

        if (Boolean.TRUE.equals(dto.getRevokeSessions())) {
            identityProviderGateway.revokeRefreshTokens(targetUser.getFirebaseUuid());
        }

        CurrentUser adminUser = authenticatedUserContext.getCurrentUser();

        activityLoggerService.logSuccess(
                adminUser.getUserId(),
                ActivityActions.CHANGE_USER_PASSWORD,
                ActivityModules.USERS,
                "Changed password for user " + targetUser.getEmail()
        );
    }
}