package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.user_model.User;
import com.itesm.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());

        userEntity.setNombre(user.getNombre());
        userEntity.setApellidos(user.getApellidos());
        userEntity.setEmail(user.getEmail());
        userEntity.setFirebaseUuid(user.getFirebaseUuid());
        userEntity.setRol(user.getRol());
        userEntity.setActive(user.isActive());

        return userEntity;
    }
    public static User toDomain(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());

        user.setNombre(userEntity.getNombre());
        user.setApellidos(userEntity.getApellidos());
        user.setEmail(userEntity.getEmail());
        user.setFirebaseUuid(userEntity.getFirebaseUuid());
        user.setActive(userEntity.isActive());
        user.setRol(userEntity.getRol());
        return user;
    }
}
