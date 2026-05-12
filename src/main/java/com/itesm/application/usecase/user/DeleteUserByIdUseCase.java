package com.itesm.application.usecase.user;

import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class DeleteUserByIdUseCase {

    private final UserRepository userRepository;

    @Inject
    public DeleteUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UUID userId) {
        return userRepository.deleteUserById(userId);
    }
}
