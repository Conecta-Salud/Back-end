package com.itesm.application.usecase.user;

import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FindAllUsersUseCase {

    private final UserRepository userRepository;

    @Inject
    public FindAllUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute() {
        return userRepository.findAllUsers();
    }
}
