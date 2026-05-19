package com.itesm.application.usecase.user;

import com.itesm.application.port.identity.IdentityProviderGateway;
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

    @Inject
    public DeleteUserByIdUseCase(
            UserRepository userRepository,
            IdentityProviderGateway identityProviderGateway
    ) {
        this.userRepository = userRepository;
        this.identityProviderGateway = identityProviderGateway;
    }

    public User execute(UUID userId) {
        User existingUser = userRepository.findUserById(userId);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        identityProviderGateway.disableUser(existingUser.getFirebaseUuid());

        return userRepository.deleteUserById(userId);
    }
}
