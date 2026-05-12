package com.itesm.domain.repository;


import com.itesm.domain.models.usuario.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByFirebaseUuid(String firebaseUuid);
    User create(User user);
}
