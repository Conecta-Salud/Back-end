package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.usuario.User;
import com.itesm.infrastructure.persistence.entity.DependenciaEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(User user, DependenciaEntity dependencia) {
        UserEntity userEntity = new UserEntity();

        userEntity.setId(user.getId());
        userEntity.setDependencia(dependencia);
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
        if (userEntity.getDependencia() != null) {
            user.setIdDependencia(userEntity.getDependencia().getId());
            user.setNombreDependencia(userEntity.getDependencia().getNombre());
        }

        user.setNombre(userEntity.getNombre());
        user.setApellidos(userEntity.getApellidos());
        user.setEmail(userEntity.getEmail());
        user.setFirebaseUuid(userEntity.getFirebaseUuid());
        user.setActive(userEntity.isActive());
        user.setRol(userEntity.getRol());

        return user;
    }
}
