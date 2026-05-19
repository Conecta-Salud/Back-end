package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.user.User;
import com.itesm.domain.repository.UserRepository;
import com.itesm.infrastructure.mapper.UserMapper;
import com.itesm.infrastructure.persistence.entity.DepartmentEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {

    @Inject
    EntityManager em;

    /*@Override
    public Optional<User> findByFirebaseUuid(String firebaseUuid) {
        return find("firebaseUuid",firebaseUuid).firstResultOptional().map(this::mapToDomain);
    }*/

    @Override
    public Optional<User> findByFirebaseUuid(String firebaseUuid) {
        EntityGraph<?> graph = em.getEntityGraph("User.withDepartment");

        List<UserEntity> result = em.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.firebaseUuid = :firebaseUuid",
                        UserEntity.class
                )
                .setParameter("firebaseUuid", firebaseUuid)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(UserMapper.toDomain(result.get(0)));
    }

    @Override
    @Transactional
    public User create(User user) {
        DepartmentEntity department = em.find(DepartmentEntity.class, user.getDepartmentId());

        if (department == null) {
            throw new IllegalArgumentException("Department not found");
        }

        UserEntity userEntity = UserMapper.toEntity(user, department);

        persist(userEntity);

        return UserMapper.toDomain(userEntity);
    }

    @Override
    public User findUserById(UUID userId) {
        EntityGraph<?> graph = em.getEntityGraph("User.withDepartment");

        List<UserEntity> result = em.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.id = :userId",
                        UserEntity.class
                )
                .setParameter("userId", userId)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return UserMapper.toDomain(result.get(0));
    }

    @Override
    public List<User> findAllUsers() {
        EntityGraph<?> graph = em.getEntityGraph("User.withDepartment");

        List<UserEntity> result = em.createQuery(
                        "SELECT u FROM UserEntity u ORDER BY u.firstName ASC, u.lastName ASC",
                        UserEntity.class
                )
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        return result.stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, User user) {
        UserEntity entity = findById(userId);

        if (entity == null) {
            throw new RuntimeException("User not found");
        }

        DepartmentEntity department = null;

        if (user.getDepartmentId() != null) {
            department = em.find(DepartmentEntity.class, user.getDepartmentId());

            if (department == null) {
                throw new RuntimeException("Department not found");
            }
        }

        UserMapper.updateEntity(entity, user, department);

        return UserMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public User deleteUserById(UUID userId) {
        UserEntity entity = findById(userId);

        if (entity == null) {
            throw new RuntimeException("User not found");
        }

        entity.setActive(false);

        return UserMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void updateLastLoginAt(UUID userId) {
        UserEntity entity = findById(userId);

        if (entity == null) {
            throw new RuntimeException("User not found");
        }

        entity.setLastLoginAt(LocalDateTime.now());
        em.flush();
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)",
                        Long.class
                )
                .setParameter("email", email.trim())
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID userId) {
        if (email == null || email.isBlank()) {
            return false;
        }

        Long count = em.createQuery(
                        """
                        SELECT COUNT(u)
                        FROM UserEntity u
                        WHERE LOWER(u.email) = LOWER(:email)
                          AND u.id <> :userId
                        """,
                        Long.class
                )
                .setParameter("email", email.trim())
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }
}