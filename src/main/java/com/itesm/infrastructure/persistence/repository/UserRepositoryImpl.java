package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.user.User;
import com.itesm.domain.models.user.UserRole;
import com.itesm.domain.repository.UserRepository;
import com.itesm.infrastructure.mapper.UserMapper;
import com.itesm.infrastructure.persistence.entity.DepartmentEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
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
    public PageResult<User> findUsers(
            String search,
            Integer departmentId,
            UserRole role,
            Boolean active,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = normalizePageSize(size);

        Map<String, Object> params = new HashMap<>();

        String whereClause = buildUsersWhereClause(
                search,
                departmentId,
                role,
                active,
                params
        );

        String selectJpql = """
            SELECT u
            FROM UserEntity u
            JOIN u.department d
            """ + whereClause + """
            ORDER BY u.firstName ASC, u.lastName ASC
            """;

        String countJpql = """
            SELECT COUNT(u)
            FROM UserEntity u
            JOIN u.department d
            """ + whereClause;

        EntityGraph<?> graph = em.getEntityGraph("User.withDepartment");

        TypedQuery<UserEntity> dataQuery = em.createQuery(selectJpql, UserEntity.class)
                .setHint("jakarta.persistence.loadgraph", graph);

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        params.forEach((key, value) -> {
            dataQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        List<User> items = dataQuery
                .setFirstResult(safePage * safeSize)
                .setMaxResults(safeSize)
                .getResultList()
                .stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());

        Long totalItems = countQuery.getSingleResult();

        return new PageResult<>(
                items,
                totalItems,
                safePage,
                safeSize
        );
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, User user) {
        UserEntity entity = findById(userId);

        if (entity == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getDepartmentId() != null) {
            DepartmentEntity department = em.find(
                    DepartmentEntity.class,
                    user.getDepartmentId()
            );

            if (department == null) {
                throw new BadRequestException("Department not found");
            }

            entity.setDepartment(department);
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

        if (user.getRole() != null) {
            entity.setRole(user.getRole());
        }

        entity.setActive(user.isActive());

        return UserMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public User deleteUserById(UUID userId) {
        UserEntity entity = findById(userId);

        if (entity == null) {
            throw new NotFoundException("User not found");
        }

        entity.setActive(false);

        return UserMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void updateLastLoginAt(UUID userId) {
        UserEntity entity = findById(userId);

        if (entity == null) {
            throw new NotFoundException("User not found");
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

    private String buildUsersWhereClause(
            String search,
            Integer departmentId,
            UserRole role,
            Boolean active,
            Map<String, Object> params
    ) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        if (search != null && !search.isBlank()) {
            where.append("""
                AND (
                    LOWER(u.email) LIKE :search
                    OR LOWER(u.firstName) LIKE :search
                    OR LOWER(u.lastName) LIKE :search
                    OR LOWER(CONCAT(CONCAT(u.firstName, ' '), u.lastName)) LIKE :search
                )
                """);

            params.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (departmentId != null) {
            where.append(" AND d.id = :departmentId ");
            params.put("departmentId", departmentId);
        }

        if (role != null) {
            where.append(" AND u.role = :role ");
            params.put("role", role);
        }

        if (active != null) {
            where.append(" AND u.isActive = :active ");
            params.put("active", active);
        }

        return where.toString();
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 20;
        }

        return Math.min(size, 100);
    }
}