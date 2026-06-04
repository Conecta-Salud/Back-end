package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.healthunit.HealthUnitInfrastructureSummary;
import com.itesm.domain.models.healthunit.HealthUnitStaffSummary;
import com.itesm.domain.models.healthunit.HealthUnitDetail;
import com.itesm.domain.models.healthunit.HealthUnitSummary;
import com.itesm.domain.repository.HealthUnitRepository;
import com.itesm.infrastructure.mapper.HealthUnitMapper;
import com.itesm.infrastructure.persistence.entity.HealthUnitEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class HealthUnitRepositoryImpl implements HealthUnitRepository, PanacheRepositoryBase<HealthUnitEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    public List<HealthUnitSummary> findSummaryByStateId(Integer stateId) {
        EntityGraph<?> graph = em.getEntityGraph("HealthUnit.summary");

        List<HealthUnitEntity> result = em.createQuery(
                        "SELECT u FROM HealthUnitEntity u " +
                                "WHERE u.municipality.state.id = :stateId " +
                                "ORDER BY u.name ASC",
                        HealthUnitEntity.class
                )
                .setParameter("stateId", stateId)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        return result.stream()
                .map(HealthUnitMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthUnitSummary> findSummaryByMunicipalityId(Integer municipalityId) {
        EntityGraph<?> graph = em.getEntityGraph("HealthUnit.summary");

        List<HealthUnitEntity> result = em.createQuery(
                        "SELECT u FROM HealthUnitEntity u " +
                                "WHERE u.municipality.id = :municipalityId " +
                                "ORDER BY u.name ASC",
                        HealthUnitEntity.class
                )
                .setParameter("municipalityId", municipalityId)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        return result.stream()
                .map(HealthUnitMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<HealthUnitDetail> findDetailByIdAndPeriodId(Integer healthUnitId, Integer periodId) {
        EntityGraph<?> graph = em.getEntityGraph("HealthUnit.summary");

        List<HealthUnitEntity> result = em.createQuery(
                        "SELECT u FROM HealthUnitEntity u WHERE u.id = :healthUnitId",
                        HealthUnitEntity.class
                )
                .setParameter("healthUnitId", healthUnitId)
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        HealthUnitEntity healthUnit = result.get(0);

        HealthUnitStaffSummary staff = findStaffByHealthUnitAndPeriod(healthUnitId, periodId);
        HealthUnitInfrastructureSummary infrastructure = findInfrastructureByHealthUnitAndPeriod(healthUnitId, periodId);

        return Optional.of(
                HealthUnitMapper.toDetail(healthUnit, staff, infrastructure)
        );
    }

    private HealthUnitStaffSummary findStaffByHealthUnitAndPeriod(Integer healthUnitId, Integer periodId) {
        Object[] row = (Object[]) em.createNativeQuery("""
            SELECT 
                COALESCE(hus.total_doctors, 0) AS total_doctors,
                COALESCE(hus.total_nurses, 0) AS total_nurses
            FROM health_units hu
            LEFT JOIN health_unit_staff hus 
                ON hus.health_unit_id = hu.id 
                AND hus.period_id = :periodId
            WHERE hu.id = :healthUnitId
            """)
                .setParameter("healthUnitId", healthUnitId)
                .setParameter("periodId", periodId)
                .getSingleResult();

        return new HealthUnitStaffSummary(
                toLong(row[0]),
                toLong(row[1])
        );
    }

    private HealthUnitInfrastructureSummary findInfrastructureByHealthUnitAndPeriod(Integer healthUnitId, Integer periodId) {
        Object[] row = (Object[]) em.createNativeQuery("""
            SELECT 
                COALESCE(SUM(CASE 
                    WHEN it.code = 'total_consultorios'
                    THEN huid.quantity ELSE 0 END), 0) AS total_consulting_rooms,
                COALESCE(SUM(CASE 
                    WHEN it.code = 'total_camas_hospitalizacion'
                    THEN huid.quantity ELSE 0 END), 0) AS total_hospital_beds
            FROM health_units hu
            LEFT JOIN health_unit_infrastructure hui 
                ON hui.health_unit_id = hu.id 
                AND hui.period_id = :periodId
            LEFT JOIN health_unit_infrastructure_details huid 
                ON huid.health_unit_infrastructure_id = hui.id
            LEFT JOIN infrastructure_types it 
                ON it.id = huid.infrastructure_type_id
            WHERE hu.id = :healthUnitId
            """)
                .setParameter("healthUnitId", healthUnitId)
                .setParameter("periodId", periodId)
                .getSingleResult();

        return new HealthUnitInfrastructureSummary(
                toLong(row[0]),
                toLong(row[1])
        );
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).longValue();
        return Long.valueOf(value.toString());
    }
}
