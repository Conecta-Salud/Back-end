package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.CreateUserDto;
import com.itesm.application.port.identity.IdentityProviderGateway;
import com.itesm.application.port.identity.IdentityUser;
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

    @Inject
    public CreateUserUseCase(
            UserRepository userRepository,
            IdentityProviderGateway identityProviderGateway
    ) {
        this.userRepository = userRepository;
        this.identityProviderGateway = identityProviderGateway;
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

            return userRepository.create(user);

        } catch (RuntimeException e) {
            identityProviderGateway.deleteUser(identityUser.getUid());
            throw e;
        }
    }
}