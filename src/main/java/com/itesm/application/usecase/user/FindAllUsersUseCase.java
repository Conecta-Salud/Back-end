package com.itesm.application.usecase.user;

import com.itesm.application.dto.common.PageResponseDto;
import com.itesm.application.dto.user.UserListResponseDto;
import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.user.User;
import com.itesm.domain.models.user.UserRole;
import com.itesm.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FindAllUsersUseCase {

    private final UserRepository userRepository;

    @Inject
    public FindAllUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PageResponseDto<UserListResponseDto> execute(
            String search,
            Integer departmentId,
            UserRole role,
            Boolean active,
            int page,
            int size
    ) {
        PageResult<User> users = userRepository.findUsers(
                search,
                departmentId,
                role,
                active,
                page,
                size
        );

        return new PageResponseDto<>(
                users.getItems()
                        .stream()
                        .map(this::toDto)
                        .toList(),
                users.getTotalItems(),
                users.getPage(),
                users.getSize(),
                users.getTotalPages()
        );
    }

    private UserListResponseDto toDto(User user) {
        return new UserListResponseDto(
                user.getId(),
                user.getDepartmentId(),
                user.getDepartmentName(),
                user.getFirstName(),
                user.getLastName(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getLastLoginAt()
        );
    }
}
