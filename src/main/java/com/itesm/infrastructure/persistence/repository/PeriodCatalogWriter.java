package com.itesm.infrastructure.persistence.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PeriodCatalogWriter {

    private static final String DESCRIPTION = "Datos oficiales cargados desde fuente sectorial DGIS.";

    private final EntityManager em;

    public PeriodCatalogWriter(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Integer ensurePeriod(short year) {
        return ensurePeriod(year, DESCRIPTION);
    }

    @Transactional
    public Integer ensurePeriod(short year, String description) {
        Optional<Integer> existing = findPeriodId(year);
        if (existing.isPresent()) {
            return existing.get();
        }

        String effectiveDescription = description == null || description.isBlank()
                ? DESCRIPTION
                : description.trim();

        em.createNativeQuery("""
                        INSERT INTO periods (period_year, status, description)
                        VALUES (:periodYear, 'published', :description)
                        """)
                .setParameter("periodYear", year)
                .setParameter("description", effectiveDescription)
                .executeUpdate();

        return findPeriodId(year)
                .orElseThrow(() -> new NotFoundException("UNKNOWN_PERIOD: no se encontró el periodo después de insertarlo"));
    }

    public Optional<Integer> findPeriodId(short year) {
        List<?> rows = em.createNativeQuery("""
                        SELECT id
                        FROM periods
                        WHERE period_year = :periodYear
                        """)
                .setParameter("periodYear", year)
                .setMaxResults(1)
                .getResultList();

        return rows.stream().findFirst().map(this::toInteger);
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(value.toString());
    }
}
