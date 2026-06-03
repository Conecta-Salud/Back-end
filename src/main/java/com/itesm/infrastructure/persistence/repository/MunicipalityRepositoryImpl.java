package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.Uploader.Establecimiento.Municipality;
import com.itesm.domain.repository.Upload.Establecimiento.MunicipalityRepository;
import com.itesm.infrastructure.mapper.Uploader.Establecimientos.MunicipalityMapper;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MunicipalityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MunicipalityRepositoryImpl implements MunicipalityRepository, PanacheRepositoryBase<MunicipalityEntity, Integer> {

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

            MunicipalityEntity entity = new MunicipalityEntity();

            entity.setId(municipality.getId());
            entity.setName(municipality.getName());
            entity.setInegiCode(municipality.getInegiCode());

            persist(entity);

        }
    }
}
