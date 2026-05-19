package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.UpdateUserDto;
import com.itesm.application.port.identity.IdentityProviderGateway;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final IdentityProviderGateway identityProviderGateway;

    @Inject
    public UpdateUserUseCase(
            UserRepository userRepository,
            IdentityProviderGateway identityProviderGateway
    ) {
        this.userRepository = userRepository;
        this.identityProviderGateway = identityProviderGateway;
    }

    public User execute(UUID userId, UpdateUserDto updateUserDto) {
        User existingUser = userRepository.findUserById(userId);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        if (
                updateUserDto.getEmail() != null
                        && userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), userId)
        ) {
            throw new BadRequestException("A user with this email already exists");
        }

        String nextFirstName = updateUserDto.getFirstName() != null
                ? updateUserDto.getFirstName()
                : existingUser.getFirstName();

        String nextLastName = updateUserDto.getLastName() != null
                ? updateUserDto.getLastName()
                : existingUser.getLastName();

        String nextEmail = updateUserDto.getEmail() != null
                ? updateUserDto.getEmail()
                : existingUser.getEmail();

        Boolean nextDisabled = updateUserDto.getActive() != null
                ? !updateUserDto.getActive()
                : null;

        identityProviderGateway.updateUser(
                existingUser.getFirebaseUuid(),
                nextEmail,
                nextFirstName + " " + nextLastName,
                nextDisabled
        );

        User user = new User();

        user.setDepartmentId(updateUserDto.getDepartmentId());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        user.setRole(updateUserDto.getRole());

        if (updateUserDto.getActive() != null) {
            user.setActive(updateUserDto.getActive());
        } else {
            user.setActive(existingUser.isActive());
        }

        return userRepository.updateUser(userId, user);
    }
}
