package com.itesm.application.usecase.user;

import com.itesm.application.dto.user.AdminUserDetailResponseDto;
import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class GetAdminUserDetailUseCase {

    private final UserRepository userRepository;

    @Inject
    public GetAdminUserDetailUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AdminUserDetailResponseDto execute(UUID userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        return new AdminUserDetailResponseDto(
                user.getId(),
                user.getDepartmentId(),
                user.getDepartmentName(),
                user.getFirstName(),
                user.getLastName(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }
}