package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.UserProfileResponseDto;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetCurrentUserUseCase {

    private final AuthenticatedUserContext authenticatedUserContext;
    private final UserRepository userRepository;

    @Inject
    public GetCurrentUserUseCase(
            AuthenticatedUserContext authenticatedUserContext,
            UserRepository userRepository
    ) {
        this.authenticatedUserContext = authenticatedUserContext;
        this.userRepository = userRepository;
    }

    public UserProfileResponseDto execute() {
        CurrentUser currentUser = authenticatedUserContext.getCurrentUser();

        userRepository.updateLastLoginAt(currentUser.getUserId());

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