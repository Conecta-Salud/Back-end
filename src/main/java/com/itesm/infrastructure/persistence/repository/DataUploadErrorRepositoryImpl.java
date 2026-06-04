package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.upload.UploadErrorDraft;
import com.itesm.domain.models.upload.UploadErrorRow;
import com.itesm.domain.repository.DataUploadErrorRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.DataUploadErrorEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class DataUploadErrorRepositoryImpl implements DataUploadErrorRepository {

    private static final List<String> NON_BLOCKING_ERROR_CODES = List.of(
            "INVALID_COORDINATE",
            "INVALID_CARE_LEVEL"
    );

    private final EntityManager em;

    public DataUploadErrorRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void replaceErrors(Integer uploadId, List<UploadErrorDraft> errors) {
        DataUploadEntity upload = em.find(DataUploadEntity.class, uploadId);

        if (upload == null) {
            throw new NotFoundException("UNKNOWN_UPLOAD: Upload not found");
        }

        deleteByUploadId(uploadId);

        appendErrors(uploadId, errors);
    }

    @Override
    @Transactional
    public void appendErrors(Integer uploadId, List<UploadErrorDraft> errors) {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        DataUploadEntity upload = em.find(DataUploadEntity.class, uploadId);

        if (upload == null) {
            throw new NotFoundException("UNKNOWN_UPLOAD: Upload not found");
        }

        for (UploadErrorDraft draft : errors) {
            DataUploadErrorEntity entity = new DataUploadErrorEntity();
            entity.setDataUpload(upload);
            entity.setCsvRowNumber(draft.getCsvRowNumber());
            entity.setColumnName(draft.getColumnName());
            entity.setRawValue(draft.getRawValue());
            entity.setErrorCode(draft.getErrorCode());
            entity.setErrorMessage(draft.getErrorMessage());
            em.persist(entity);
        }
    }

    @Override
    @Transactional
    public void deleteByUploadId(Integer uploadId) {
        em.createQuery("DELETE FROM DataUploadErrorEntity e WHERE e.dataUpload.id = :uploadId")
                .setParameter("uploadId", uploadId)
                .executeUpdate();
    }

    @Override
    public PageResult<DataUploadErrorEntity> findByUploadId(Integer uploadId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = normalizePageSize(size);

        List<DataUploadErrorEntity> items = em.createQuery("""
                        SELECT e
                        FROM DataUploadErrorEntity e
                        WHERE e.dataUpload.id = :uploadId
                        ORDER BY e.csvRowNumber ASC, e.id ASC
                        """, DataUploadErrorEntity.class)
                .setParameter("uploadId", uploadId)
                .setFirstResult(safePage * safeSize)
                .setMaxResults(safeSize)
                .getResultList();

        return new PageResult<>(
                items,
                countByUploadId(uploadId),
                safePage,
                safeSize
        );
    }

    @Override
    public PageResult<UploadErrorRow> findByBatchId(Integer batchId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = normalizePageSize(size);

        List<?> rows = em.createNativeQuery("""
                        SELECT
                            e.id,
                            e.data_upload_id,
                            u.original_file_name,
                            e.csv_row_number,
                            e.column_name,
                            e.raw_value,
                            e.error_code,
                            e.error_message
                        FROM data_upload_errors e
                        JOIN data_uploads u ON u.id = e.data_upload_id
                        WHERE u.batch_id = :batchId
                        ORDER BY u.id ASC, e.csv_row_number ASC, e.id ASC
                        LIMIT :limit OFFSET :offset
                        """)
                .setParameter("batchId", batchId)
                .setParameter("limit", safeSize)
                .setParameter("offset", safePage * safeSize)
                .getResultList();

        List<UploadErrorRow> items = rows.stream()
                .map(this::toUploadErrorRow)
                .toList();

        return new PageResult<>(
                items,
                countByBatchId(batchId),
                safePage,
                safeSize
        );
    }

    @Override
    public long countByUploadId(Integer uploadId) {
        return em.createQuery("""
                        SELECT COUNT(e)
                        FROM DataUploadErrorEntity e
                        WHERE e.dataUpload.id = :uploadId
                        """, Long.class)
                .setParameter("uploadId", uploadId)
                .getSingleResult();
    }

    @Override
    public long countBlockingByUploadId(Integer uploadId) {
        return em.createQuery("""
                        SELECT COUNT(e)
                        FROM DataUploadErrorEntity e
                        WHERE e.dataUpload.id = :uploadId
                          AND e.errorCode NOT IN (:nonBlockingErrorCodes)
                        """, Long.class)
                .setParameter("uploadId", uploadId)
                .setParameter("nonBlockingErrorCodes", NON_BLOCKING_ERROR_CODES)
                .getSingleResult();
    }

    @Override
    public long countByBatchId(Integer batchId) {
        Object count = em.createNativeQuery("""
                        SELECT COUNT(*)
                        FROM data_upload_errors e
                        JOIN data_uploads u ON u.id = e.data_upload_id
                        WHERE u.batch_id = :batchId
                        """)
                .setParameter("batchId", batchId)
                .getSingleResult();

        return toLong(count);
    }

    private UploadErrorRow toUploadErrorRow(Object row) {
        Object[] columns = (Object[]) row;

        return new UploadErrorRow(
                toLong(columns[0]),
                toInteger(columns[1]),
                toStringValue(columns[2]),
                toInteger(columns[3]),
                toStringValue(columns[4]),
                toStringValue(columns[5]),
                toStringValue(columns[6]),
                toStringValue(columns[7])
        );
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(value.toString());
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 20;
        }

        return Math.min(size, 100);
    }
}
