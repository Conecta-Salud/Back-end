package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.User;
import com.itesm.domain.repository.UserRepository;
import com.itesm.infrastructure.mapper.UserMapper;
import com.itesm.infrastructure.persistence.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {
    @Override
    public Optional<User> findByFirebaseUuid(String firebaseUuid) {
        return find("firebaseUuid",firebaseUuid).firstResultOptional().map(this::mapToDomain);
    }

    @Override
    @Transactional
    public User create(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        persist(userEntity);
        return UserMapper.toDomain(userEntity) ;
    }

    private User mapToDomain(UserEntity userEntity) {
        return UserMapper.toDomain(userEntity);
    }
}
