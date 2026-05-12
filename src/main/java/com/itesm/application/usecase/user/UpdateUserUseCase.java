package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.UpdateUserDto;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class UpdateUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UUID userId, UpdateUserDto updateUserDto) {
        User user = new User();

        user.setDepartmentId(updateUserDto.getDepartmentId());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        user.setRole(updateUserDto.getRole());

        if (updateUserDto.getActive() != null) {
            user.setActive(updateUserDto.getActive());
        }

        return userRepository.updateUser(userId, user);
    }
}
