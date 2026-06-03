package com.itesm.infrastructure.persistence.repository;

import com.itesm.domain.models.upload.CsvFileRole;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.domain.repository.DataUploadRepository;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DataUploadRepositoryImpl implements DataUploadRepository {

    private final EntityManager em;

    public DataUploadRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public DataUploadEntity create(DataUploadEntity upload, Integer batchId) {
        UploadBatchEntity batch = em.find(UploadBatchEntity.class, batchId);

        if (batch == null) {
            throw new NotFoundException("UNKNOWN_BATCH: Upload batch not found");
        }

        upload.setBatch(batch);
        em.persist(upload);
        em.flush();
        em.refresh(upload);

        return upload;
    }

    @Override
    public Optional<DataUploadEntity> findById(Integer uploadId) {
        if (uploadId == null) {
            return Optional.empty();
        }

        List<DataUploadEntity> rows = em.createQuery("""
                        SELECT u
                        FROM DataUploadEntity u
                        JOIN FETCH u.batch b
                        JOIN FETCH b.dataSource ds
                        WHERE u.id = :uploadId
                        """, DataUploadEntity.class)
                .setParameter("uploadId", uploadId)
                .setMaxResults(1)
                .getResultList();

        return rows.stream().findFirst();
    }

    @Override
    public List<DataUploadEntity> findByBatchId(Integer batchId) {
        return em.createQuery("""
                        SELECT u
                        FROM DataUploadEntity u
                        WHERE u.batch.id = :batchId
                        ORDER BY u.createdAt ASC, u.id ASC
                        """, DataUploadEntity.class)
                .setParameter("batchId", batchId)
                .getResultList();
    }

    @Override
    public long countByBatchId(Integer batchId) {
        return em.createQuery("""
                        SELECT COUNT(u)
                        FROM DataUploadEntity u
                        WHERE u.batch.id = :batchId
                        """, Long.class)
                .setParameter("batchId", batchId)
                .getSingleResult();
    }

    @Override
    public boolean existsChecksumInBatch(Integer batchId, String checksum) {
        if (checksum == null || checksum.isBlank()) {
            return false;
        }

        Long count = em.createQuery("""
                        SELECT COUNT(u)
                        FROM DataUploadEntity u
                        WHERE u.batch.id = :batchId
                          AND u.checksum = :checksum
                        """, Long.class)
                .setParameter("batchId", batchId)
                .setParameter("checksum", checksum)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsByBatchIdAndFileRole(Integer batchId, CsvFileRole fileRole) {
        if (fileRole == null) {
            return false;
        }

        Long count = em.createQuery("""
                        SELECT COUNT(u)
                        FROM DataUploadEntity u
                        WHERE u.batch.id = :batchId
                          AND u.fileRole = :fileRole
                        """, Long.class)
                .setParameter("batchId", batchId)
                .setParameter("fileRole", fileRole)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional
    public void updateValidationResult(
            Integer uploadId,
            String status,
            Integer totalRecords,
            Integer validRecords,
            Integer errorRecords,
            String errorDetail
    ) {
        DataUploadEntity upload = em.find(DataUploadEntity.class, uploadId);

        if (upload == null) {
            throw new NotFoundException("UNKNOWN_UPLOAD: Upload not found");
        }

        upload.setStatus(UploadStatus.valueOf(status));
        upload.setTotalRecords(totalRecords == null ? 0 : totalRecords);
        upload.setValidRecords(validRecords == null ? 0 : validRecords);
        upload.setErrorRecords(errorRecords == null ? 0 : errorRecords);
        upload.setErrorDetail(errorDetail);
        upload.setProcessedAt(LocalDateTime.now());
    }
}
