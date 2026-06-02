package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.Institution;
import com.itesm.domain.repository.Upload.Establecimiento.InstitutionRepository;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.InstitutionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.persist;

@ApplicationScoped
public class InstitutionRepositoryImpl implements InstitutionRepository, PanacheRepositoryBase<InstitutionEntity, Integer> {

    @Override
    @Transactional
    public void save(List<Institution> institutions) {

        for (Institution institution : institutions) {

            InstitutionEntity entity = new InstitutionEntity();

            entity.setId(entity.getId());
            entity.setName(entity.getName());

            persist(entity);

        }
    }
}
