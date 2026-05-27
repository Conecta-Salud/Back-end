package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.CreateUserDto;
import com.itesm.application.port.identity.IdentityProviderGateway;
import com.itesm.application.port.identity.IdentityUser;
import com.itesm.application.security.AuthenticatedUserContext;
import com.itesm.application.security.CurrentUser;
import com.itesm.application.service.activity.ActivityActions;
import com.itesm.application.service.activity.ActivityLoggerService;
import com.itesm.application.service.activity.ActivityModules;
import com.itesm.domain.models.user.User;
import com.itesm.domain.models.user.UserRole;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.UUID;

@ApplicationScoped
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final IdentityProviderGateway identityProviderGateway;
    private final AuthenticatedUserContext authenticatedUserContext;
    private final ActivityLoggerService activityLoggerService;

    @Inject
    public CreateUserUseCase(
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

    public User execute(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new BadRequestException("A user with this email already exists");
        }

        UserRole role = createUserDto.getRole() != null
                ? createUserDto.getRole()
                : UserRole.strategic;

        String displayName = createUserDto.getFirstName() + " " + createUserDto.getLastName();

        IdentityUser identityUser = identityProviderGateway.createUser(
                createUserDto.getEmail(),
                createUserDto.getPassword(),
                displayName
        );

        try {
            User user = new User();

            user.setId(UUID.randomUUID());
            user.setDepartmentId(createUserDto.getDepartmentId());
            user.setFirstName(createUserDto.getFirstName());
            user.setLastName(createUserDto.getLastName());
            user.setEmail(createUserDto.getEmail());
            user.setFirebaseUuid(identityUser.getUid());
            user.setActive(true);
            user.setRole(role);

            User createdUser = userRepository.create(user);

            CurrentUser adminUser = authenticatedUserContext.getCurrentUser();

            activityLoggerService.logSuccess(
                    adminUser.getUserId(),
                    ActivityActions.CREATE_USER,
                    ActivityModules.USERS,
                    "Created user " + createdUser.getEmail() + " with role " + createdUser.getRole()
            );

            return createdUser;

        } catch (RuntimeException e) {
            identityProviderGateway.deleteUser(identityUser.getUid());
            throw e;
        }
    }
}