package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.Uploader.Establecimiento.Municipality;
import com.itesm.domain.repository.Upload.Establecimiento.MunicipalityRepository;
import com.itesm.infrastructure.mapper.Uploader.Establecimientos.MunicipalityMapper;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.StateEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MunicipalityRepositoryImpl implements MunicipalityRepository, PanacheRepositoryBase<MunicipalityEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public List<Municipality> findAllMunicipalities() {
        return listAll()
                .stream()
                .map(MunicipalityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Municipality> findByStateId(Integer stateId) {
        return find("state.id", stateId)
                .list()
                .stream()
                .map(MunicipalityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Municipality> findMunicipalityById(Integer municipalityId) {
        MunicipalityEntity entity = findById(municipalityId);
        return entity == null ? Optional.empty() : Optional.of(MunicipalityMapper.toDomain(entity));
    }

    @Override
    @Transactional
    public void save(List<Municipality> municipalities) {

        for (Municipality municipality : municipalities) {
            if (municipality.getName() == null || municipality.getName().isBlank()) {
                continue;
            }

            StateEntity state = em.createQuery(
                            "SELECT s FROM StateEntity s WHERE s.inegiCode = :inegiCode",
                            StateEntity.class
                    )
                    .setParameter("inegiCode", municipality.getStateInegiCode())
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe estado con inegiCode: " + municipality.getStateInegiCode()
                    ));

            MunicipalityEntity existing = em.createQuery(
                            """
                            SELECT m FROM MunicipalityEntity m
                            WHERE m.inegiCode = :inegiCode
                            OR (m.state.id = :stateId AND m.name = :name)
                            """,
                            MunicipalityEntity.class
                    )
                    .setParameter("inegiCode", municipality.getInegiCode())
                    .setParameter("stateId", state.getId())
                    .setParameter("name", municipality.getName())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setName(municipality.getName());
                existing.setInegiCode(municipality.getInegiCode());
                existing.setState(state);
                persist(existing);
                continue;
            }

            MunicipalityEntity entity = new MunicipalityEntity();
            entity.setName(municipality.getName());
            entity.setInegiCode(municipality.getInegiCode());
            entity.setState(state);

            persist(entity);
        }
    }
}
