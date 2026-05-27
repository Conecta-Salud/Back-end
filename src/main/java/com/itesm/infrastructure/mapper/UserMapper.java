package com.itesm.infrastructure.mapper;

import com.itesm.domain.models.user.User;
import com.itesm.infrastructure.persistence.entity.DepartmentEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;

import java.util.UUID;

public class UserMapper {

    private UserMapper() {}

    public static UserEntity toEntity(User user, DepartmentEntity departmentEntity) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();

        if (user.getId() != null) {
            entity.setId(user.getId());
        } else {
            entity.setId(UUID.randomUUID());
        }

        entity.setDepartment(departmentEntity);
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setFirebaseUuid(user.getFirebaseUuid());
        entity.setRole(user.getRole());
        entity.setActive(user.isActive());
        entity.setLastLoginAt(user.getLastLoginAt());

        return entity;
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        Integer departmentId = null;
        String departmentName = null;

        if (entity.getDepartment() != null) {
            departmentId = entity.getDepartment().getId();
            departmentName = entity.getDepartment().getName();
        }

        return new User(
                entity.getId(),
                departmentId,
                departmentName,
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getFirebaseUuid(),
                entity.getRole(),
                entity.isActive(),
                entity.getLastLoginAt()
        );
    }

    public static void updateEntity(UserEntity entity, User user, DepartmentEntity departmentEntity) {
        if (entity == null || user == null) {
            return;
        }

        if (departmentEntity != null) {
            entity.setDepartment(departmentEntity);
        }

        if (user.getFirstName() != null) {
            entity.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null) {
            entity.setLastName(user.getLastName());
        }

        if (user.getEmail() != null) {
            entity.setEmail(user.getEmail());
        }

        if (user.getFirebaseUuid() != null) {
            entity.setFirebaseUuid(user.getFirebaseUuid());
        }

        if (user.getRole() != null) {
            entity.setRole(user.getRole());
        }

        entity.setActive(user.isActive());

        if (user.getLastLoginAt() != null) {
            entity.setLastLoginAt(user.getLastLoginAt());
        }
    }
}
