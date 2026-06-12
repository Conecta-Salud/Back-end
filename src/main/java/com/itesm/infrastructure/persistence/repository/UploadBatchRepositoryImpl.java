package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.upload.UploadSourceType;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.UploadBatchRepository;
import com.itesm.infrastructure.persistence.entity.DataSourceEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import com.itesm.infrastructure.persistence.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UploadBatchRepositoryImpl implements UploadBatchRepository {

    private final EntityManager em;

    public UploadBatchRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<DataSourceEntity> findDataSourceByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }

        List<DataSourceEntity> rows = em.createQuery(
                        "SELECT ds FROM DataSourceEntity ds WHERE ds.code = :code",
                        DataSourceEntity.class
                )
                .setParameter("code", code.trim())
                .setMaxResults(1)
                .getResultList();

        return rows.stream().findFirst();
    }

    @Override
    @Transactional
    public UploadBatchEntity create(UploadBatchEntity batch, UUID userId, Integer dataSourceId) {
        UserEntity user = em.find(UserEntity.class, userId);
        DataSourceEntity dataSource = em.find(DataSourceEntity.class, dataSourceId);

        if (user == null) {
            throw new NotFoundException("UNKNOWN_USER: usuario autenticado no encontrado");
        }

        if (dataSource == null) {
            throw new NotFoundException("UNKNOWN_DATA_SOURCE: fuente de datos no encontrada");
        }

        batch.setUser(user);
        batch.setDataSource(dataSource);
        em.persist(batch);
        em.flush();
        em.refresh(batch);

        return batch;
    }

    @Override
    public Optional<UploadBatchEntity> findById(Integer batchId) {
        if (batchId == null) {
            return Optional.empty();
        }

        List<UploadBatchEntity> rows = em.createQuery("""
                        SELECT b
                        FROM UploadBatchEntity b
                        JOIN FETCH b.dataSource ds
                        JOIN FETCH b.user u
                        WHERE b.id = :batchId
                        """, UploadBatchEntity.class)
                .setParameter("batchId", batchId)
                .setMaxResults(1)
                .getResultList();

        return rows.stream().findFirst();
    }

    @Override
    public PageResult<UploadBatchEntity> findBatches(
            UploadSourceType sourceType,
            Integer sourceYear,
            UploadStatus status,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = normalizePageSize(size);
        Map<String, Object> params = new HashMap<>();

        String where = buildWhereClause(sourceType, sourceYear, status, params);

        TypedQuery<UploadBatchEntity> query = em.createQuery("""
                SELECT b
                FROM UploadBatchEntity b
                JOIN FETCH b.dataSource ds
                JOIN FETCH b.user u
                """ + where + """
                ORDER BY b.createdAt DESC, b.id DESC
                """, UploadBatchEntity.class);

        TypedQuery<Long> countQuery = em.createQuery("""
                SELECT COUNT(b)
                FROM UploadBatchEntity b
                """ + where, Long.class);

        params.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        List<UploadBatchEntity> items = query
                .setFirstResult(safePage * safeSize)
                .setMaxResults(safeSize)
                .getResultList();

        return new PageResult<>(
                items,
                countQuery.getSingleResult(),
                safePage,
                safeSize
        );
    }

    @Override
    public boolean existsBySourceTypeSourceYearAndBatchVersion(
            UploadSourceType sourceType,
            Short sourceYear,
            String batchVersion
    ) {
        if (sourceType == null || sourceYear == null || batchVersion == null || batchVersion.isBlank()) {
            return false;
        }

        Long count = em.createQuery("""
                        SELECT COUNT(b.id)
                        FROM UploadBatchEntity b
                        WHERE b.sourceType = :sourceType
                          AND b.sourceYear = :sourceYear
                          AND b.batchVersion = :batchVersion
                        """, Long.class)
                .setParameter("sourceType", sourceType)
                .setParameter("sourceYear", sourceYear)
                .setParameter("batchVersion", batchVersion.trim())
                .getSingleResult();

        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void updateStatus(Integer batchId, UploadStatus status, String errorDetail, boolean processed) {
        UploadBatchEntity batch = requireBatch(batchId);
        batch.setStatus(status);
        batch.setErrorDetail(errorDetail);

        if (processed) {
            batch.setProcessedAt(java.time.LocalDateTime.now());
        }
    }

    @Override
    @Transactional
    public void recalculateCounters(Integer batchId) {
        UploadBatchEntity batch = requireBatch(batchId);

        Object[] row = (Object[]) em.createQuery("""
                SELECT
                    COUNT(u.id),
                    COALESCE(SUM(u.totalRecords), 0),
                    COALESCE(SUM(u.validRecords), 0),
                    COALESCE(SUM(u.errorRecords), 0)
                FROM DataUploadEntity u
                WHERE u.batch.id = :batchId
                """)
                .setParameter("batchId", batchId)
                .getSingleResult();

        batch.setUploadedFiles(toInteger(row[0]));
        batch.setTotalRecords(toInteger(row[1]));
        batch.setValidRecords(toInteger(row[2]));
        batch.setErrorRecords(toInteger(row[3]));
    }

    private UploadBatchEntity requireBatch(Integer batchId) {
        UploadBatchEntity batch = em.find(UploadBatchEntity.class, batchId);

        if (batch == null) {
            throw new NotFoundException("UNKNOWN_BATCH: lote de carga no encontrado");
        }

        return batch;
    }

    private String buildWhereClause(
            UploadSourceType sourceType,
            Integer sourceYear,
            UploadStatus status,
            Map<String, Object> params
    ) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        if (sourceType != null) {
            where.append(" AND b.sourceType = :sourceType ");
            params.put("sourceType", sourceType);
        }

        if (sourceYear != null) {
            where.append(" AND b.sourceYear = :sourceYear ");
            params.put("sourceYear", sourceYear.shortValue());
        }

        if (status != null) {
            where.append(" AND b.status = :status ");
            params.put("status", status);
        }

        return where.toString();
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 20;
        }

        return Math.min(size, 100);
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
