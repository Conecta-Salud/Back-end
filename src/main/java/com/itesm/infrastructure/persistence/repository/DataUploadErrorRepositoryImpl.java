package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.upload.UploadErrorDraft;
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

        em.createQuery("DELETE FROM DataUploadErrorEntity e WHERE e.dataUpload.id = :uploadId")
                .setParameter("uploadId", uploadId)
                .executeUpdate();

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
    public long countByUploadId(Integer uploadId) {
        return em.createQuery("""
                        SELECT COUNT(e)
                        FROM DataUploadErrorEntity e
                        WHERE e.dataUpload.id = :uploadId
                        """, Long.class)
                .setParameter("uploadId", uploadId)
                .getSingleResult();
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 20;
        }

        return Math.min(size, 100);
    }
}
