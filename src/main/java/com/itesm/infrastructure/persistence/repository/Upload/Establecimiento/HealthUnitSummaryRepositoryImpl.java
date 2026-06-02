package com.itesm.infrastructure.persistence.repository.Upload.Establecimiento;


import com.itesm.domain.models.healthunit.HealthUnitSummary;
import com.itesm.domain.repository.Upload.Establecimiento.HealthUnitSummaryRepository;
import com.itesm.infrastructure.persistence.entity.HealthUnitEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class HealthUnitSummaryRepositoryImpl implements HealthUnitSummaryRepository, PanacheRepositoryBase<HealthUnitEntity, Integer> {

    @Override
    @Transactional
    public void save(List<HealthUnitSummary> HealthUnitSummary) {

        for (HealthUnitSummary healthUnitSummary : HealthUnitSummary) {

            HealthUnitEntity entity = new HealthUnitEntity();

            entity.setId(entity.getId());
            entity.setName(entity.getName());
            entity.setInstitution(entity.getInstitution());
            entity.setMunicipality(entity.getMunicipality());
            entity.setEstablishmentType(entity.getEstablishmentType());
            entity.setMedicalUnitType(entity.getMedicalUnitType());
            entity.setCareLevel(entity.getCareLevel());

            persist(entity);

        }
    }
}
