package com.itesm.domain.repository;


import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.user.User;
import com.itesm.domain.models.user.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByFirebaseUuid(String firebaseUuid);
    // PROTEGIDOS PARA EL ROL ADMIN
    User create(User user);
    User findUserById(UUID userId);
    List<User> findAllUsers();
    PageResult<User> findUsers(
            String search,
            Integer departmentId,
            UserRole role,
            Boolean active,
            int page,
            int size
    );
    User updateUser(UUID userId, User user);
    User deleteUserById(UUID userId);
    User reactivateUserById(UUID userId);
    void updateLastLoginAt(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID userId);
}
