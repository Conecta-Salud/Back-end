package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.usuario.User;
import com.itesm.domain.repository.UserRepository;
import com.itesm.infrastructure.mapper.UserMapper;
import com.itesm.infrastructure.persistence.entity.DependenciaEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        EntityGraph<?> graph = em.getEntityGraph("User.withDependencia");

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
        DependenciaEntity dependencia = em.find(DependenciaEntity.class, user.getIdDependencia());

        if (dependencia == null) {
            throw new IllegalArgumentException("Dependencia not found");
        }

        UserEntity userEntity = UserMapper.toEntity(user, dependencia);

        persist(userEntity);

        return UserMapper.toDomain(userEntity);
    }

    private User mapToDomain(UserEntity userEntity) {
        return UserMapper.toDomain(userEntity);
    }
}
