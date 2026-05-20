package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.UpdateUserDto;
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
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final IdentityProviderGateway identityProviderGateway;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ActivityLoggerService activityLoggerService;

    @Inject
    public UpdateUserUseCase(
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

    public User execute(UUID userId, UpdateUserDto updateUserDto) {
        User existingUser = userRepository.findUserById(userId);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        validateEmailUniquenessIfNeeded(userId, updateUserDto, existingUser);

        String finalFirstName = valueOrCurrent(
                updateUserDto.getFirstName(),
                existingUser.getFirstName()
        );

        String finalLastName = valueOrCurrent(
                updateUserDto.getLastName(),
                existingUser.getLastName()
        );

        String finalEmail = valueOrCurrent(
                updateUserDto.getEmail(),
                existingUser.getEmail()
        );

        boolean finalActive = updateUserDto.getActive() != null
                ? updateUserDto.getActive()
                : existingUser.isActive();

        String currentDisplayName = buildDisplayName(
                existingUser.getFirstName(),
                existingUser.getLastName()
        );

        String finalDisplayName = buildDisplayName(
                finalFirstName,
                finalLastName
        );

        boolean emailChanged = !sameText(existingUser.getEmail(), finalEmail);
        boolean displayNameChanged = !sameText(currentDisplayName, finalDisplayName);
        boolean activeChanged = existingUser.isActive() != finalActive;

        boolean requiresFirebaseUpdate = emailChanged || displayNameChanged || activeChanged;

        if (requiresFirebaseUpdate) {
            identityProviderGateway.updateUser(
                    existingUser.getFirebaseUuid(),
                    emailChanged ? finalEmail : null,
                    displayNameChanged ? finalDisplayName : null,
                    activeChanged ? !finalActive : null
            );
        }

        User user = new User();

        user.setDepartmentId(updateUserDto.getDepartmentId());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        user.setRole(updateUserDto.getRole());
        user.setActive(finalActive);

        User updatedUser = userRepository.updateUser(userId, user);

        CurrentUser adminUser = authenticatedUserContext.getCurrentUser();

        activityLoggerService.logSuccess(
                adminUser.getUserId(),
                ActivityActions.UPDATE_USER,
                ActivityModules.USERS,
                "Updated user " + updatedUser.getEmail()
        );

        return updatedUser;
    }

    private void validateEmailUniquenessIfNeeded(
            UUID userId,
            UpdateUserDto updateUserDto,
            User existingUser
    ) {
        if (updateUserDto.getEmail() == null || updateUserDto.getEmail().isBlank()) {
            return;
        }

        if (sameText(updateUserDto.getEmail(), existingUser.getEmail())) {
            return;
        }

        if (userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), userId)) {
            throw new BadRequestException("A user with this email already exists");
        }
    }

    private String valueOrCurrent(String nextValue, String currentValue) {
        if (nextValue == null) {
            return currentValue;
        }

        return nextValue.trim();
    }

    private String buildDisplayName(String firstName, String lastName) {
        String safeFirstName = firstName != null ? firstName.trim() : "";
        String safeLastName = lastName != null ? lastName.trim() : "";

        return (safeFirstName + " " + safeLastName).trim();
    }

    private boolean sameText(String a, String b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return Objects.equals(
                a.trim().toLowerCase(),
                b.trim().toLowerCase()
        );
    }
}