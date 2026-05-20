package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.admin.activity.SystemActivityLog;
import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.repository.SystemActivityLogRepository;
import com.itesm.infrastructure.persistence.entity.SystemActivityLogEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class SystemActivityLogRepositoryImpl implements SystemActivityLogRepository {

    private final EntityManager em;

    public SystemActivityLogRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void create(
            UUID userId,
            String action,
            String module,
            String result,
            String detail
    ) {
        UserEntity user = em.find(UserEntity.class, userId);

        if (user == null) {
            throw new NotFoundException("User not found for activity log");
        }

        SystemActivityLogEntity entity = new SystemActivityLogEntity();
        entity.setUser(user);
        entity.setAction(action);
        entity.setModule(module);
        entity.setResult(result != null ? result : "success");
        entity.setDetail(detail);

        em.persist(entity);
    }

    @Override
    public PageResult<SystemActivityLog> findActivityLogs(
            String query,
            String action,
            String module,
            String result,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = normalizePageSize(size);

        Map<String, Object> params = new HashMap<>();

        String whereClause = buildWhereClause(
                query,
                action,
                module,
                result,
                from,
                to,
                params
        );

        String selectJpql = """
                SELECT new com.itesm.domain.models.admin.activity.SystemActivityLog(
                    l.id,
                    u.id,
                    u.email,
                    CONCAT(u.firstName, ' ', u.lastName),
                    l.action,
                    l.module,
                    l.result,
                    l.detail,
                    l.createdAt
                )
                FROM SystemActivityLogEntity l
                JOIN l.user u
                """ + whereClause + """
                ORDER BY l.createdAt DESC
                """;

        String countJpql = """
                SELECT COUNT(l)
                FROM SystemActivityLogEntity l
                JOIN l.user u
                """ + whereClause;

        TypedQuery<SystemActivityLog> dataQuery = em.createQuery(selectJpql, SystemActivityLog.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        params.forEach((key, value) -> {
            dataQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        List<SystemActivityLog> items = dataQuery
                .setFirstResult(safePage * safeSize)
                .setMaxResults(safeSize)
                .getResultList();

        Long totalItems = countQuery.getSingleResult();

        return new PageResult<>(
                items,
                totalItems,
                safePage,
                safeSize
        );
    }

    private String buildWhereClause(
            String query,
            String action,
            String module,
            String result,
            LocalDateTime from,
            LocalDateTime to,
            Map<String, Object> params
    ) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        if (query != null && !query.isBlank()) {
            where.append("""
                    AND (
                        LOWER(u.email) LIKE LOWER(:query)
                        OR LOWER(u.firstName) LIKE LOWER(:query)
                        OR LOWER(u.lastName) LIKE LOWER(:query)
                        OR LOWER(l.action) LIKE LOWER(:query)
                        OR LOWER(l.module) LIKE LOWER(:query)
                    )
                    """);
            params.put("query", "%" + query.trim() + "%");
        }

        if (action != null && !action.isBlank()) {
            where.append(" AND l.action = :action ");
            params.put("action", action.trim());
        }

        if (module != null && !module.isBlank()) {
            where.append(" AND l.module = :module ");
            params.put("module", module.trim());
        }

        if (result != null && !result.isBlank()) {
            where.append(" AND l.result = :result ");
            params.put("result", result.trim());
        }

        if (from != null) {
            where.append(" AND l.createdAt >= :from ");
            params.put("from", from);
        }

        if (to != null) {
            where.append(" AND l.createdAt <= :to ");
            params.put("to", to);
        }

        return where.toString();
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 20;
        }

        return Math.min(size, 100);
    }
}
