package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.User;
import com.itesm.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setActive(user.isActive());
        userEntity.setEmail(user.getEmail());
        userEntity.setFirebaseUuid(user.getFirebaseUuid());
        userEntity.setFullName(user.getFullName());
        userEntity.setRole(user.getRole());
        return userEntity;
    }
    public static User toDomain(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setActive(userEntity.isActive());
        user.setEmail(userEntity.getEmail());
        user.setFirebaseUuid(userEntity.getFirebaseUuid());
        user.setFullName(userEntity.getFullName());
        user.setRole(userEntity.getRole());
        return user;
    }
}
