package com.itesm.domain.repository;


import com.itesm.domain.models.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByFirebaseUuid(String firebaseUuid);
    // Estos tienen que estar PROTEGIDOS PARA EL ROL ADMIN
    User create(User user);
    User findUserById(UUID userId);
    List<User> findAllUsers();
    User updateUser(UUID userId, User user);
    User deleteUserById(UUID userId);
}
