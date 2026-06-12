package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.admin.overview.AdminOverviewMetrics;
import com.itesm.domain.repository.AdminOverviewRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.math.BigInteger;

@ApplicationScoped
public class AdminOverviewRepositoryImpl implements AdminOverviewRepository {

    private final EntityManager em;

    public AdminOverviewRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public AdminOverviewMetrics getOverviewMetrics() {
        Object[] row = (Object[]) em.createNativeQuery("""
                SELECT
                    (
                        SELECT COUNT(*)
                        FROM users
                    ) AS registered_users,

                    (
                        SELECT COUNT(*)
                        FROM users
                        WHERE last_login_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                    ) AS active_users_last_7_days,

                    (
                        SELECT COUNT(*)
                        FROM system_activity_logs
                        WHERE action IN ('COMPARE_STATES', 'COMPARE_MUNICIPALITIES')
                          AND result = 'success'
                    ) AS comparisons_performed,

                    (
                        SELECT COUNT(*)
                        FROM upload_batches
                        WHERE status = 'completed'
                    ) AS completed_upload_batches
                """)
                .getSingleResult();

        return new AdminOverviewMetrics(
                toLong(row[0]),
                toLong(row[1]),
                toLong(row[2]),
                toLong(row[3])
        );
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Long) {
            return (Long) value;
        }

        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }

        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        }

        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}