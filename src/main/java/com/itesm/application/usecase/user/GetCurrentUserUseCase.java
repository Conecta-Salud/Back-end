package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.UserProfileResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.service.activity.ActivityActions;
import com.itesm.application.service.activity.ActivityLoggerService;
import com.itesm.application.service.activity.ActivityModules;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCurrentUserUseCase {

    private final AuthenticatedUserContext authenticatedUserContext;
    private final UserRepository userRepository;
    private final ActivityLoggerService activityLoggerService;

    @Inject
    public GetCurrentUserUseCase(
            AuthenticatedUserContext authenticatedUserContext,
            UserRepository userRepository,
            ActivityLoggerService activityLoggerService
    ) {
        this.authenticatedUserContext = authenticatedUserContext;
        this.userRepository = userRepository;
        this.activityLoggerService = activityLoggerService;
    }

    public UserProfileResponseDto execute() {
        CurrentUser currentUser = authenticatedUserContext.getCurrentUser();

        userRepository.updateLastLoginAt(currentUser.getUserId());

        activityLoggerService.logSuccess(
                currentUser.getUserId(),
                ActivityActions.LOGIN,
                ActivityModules.AUTH,
                "User logged in"
        );

        User user = userRepository.findUserById(currentUser.getUserId());

        return new UserProfileResponseDto(
                user.getId(),
                user.getDepartmentId(),
                user.getDepartmentName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getFirebaseUuid(),
                user.getRole(),
                user.isActive(),
                user.getLastLoginAt()
        );
    }
}