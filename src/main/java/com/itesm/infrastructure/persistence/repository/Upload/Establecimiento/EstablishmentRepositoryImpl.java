package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.Establishment;
import com.itesm.domain.repository.Upload.Establecimiento.EstablishmentRepository;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.EstablishmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EstablishmentRepositoryImpl implements EstablishmentRepository, PanacheRepositoryBase<EstablishmentEntity, Integer> {

    @Override
    @Transactional
    public void save(List<Establishment> establishments) {

        for (Establishment establishment : establishments) {

            EstablishmentEntity entity = new EstablishmentEntity();

            entity.setId(entity.getId());
            entity.setName(entity.getName());

            persist(entity);

        }
    }
}
