package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;

import com.itesm.domain.models.Uploader.Establecimiento.MedicalUnitTypes;
import com.itesm.domain.repository.Upload.Establecimiento.MedicalUnitTypesRepository;
import com.itesm.infrastructure.persistence.entity.Upload.Establecimientos.MedicalUnitTypeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;


@ApplicationScoped
public class MedicalUnitTypesRepositoryImpl implements MedicalUnitTypesRepository, PanacheRepositoryBase<MedicalUnitTypeEntity, Integer> {

    @Override
    @Transactional
    public void save(List<MedicalUnitTypes> medicalUnitTypes) {

        for (MedicalUnitTypes medicalUnitType : medicalUnitTypes) {

            MedicalUnitTypeEntity entity = new MedicalUnitTypeEntity();

            entity.setId(entity.getId());
            entity.setName(entity.getName());

            persist(entity);

        }
    }
}
