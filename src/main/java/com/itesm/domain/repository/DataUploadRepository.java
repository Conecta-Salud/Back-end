package com.itesm.domain.repository;

import com.itesm.domain.models.upload.CsvFileRole;
import com.itesm.infrastructure.persistence.entity.DataUploadEntity;

import java.util.List;
import java.util.Optional;

public interface DataUploadRepository {

    DataUploadEntity create(DataUploadEntity upload, Integer batchId);

    Optional<DataUploadEntity> findById(Integer uploadId);

    List<DataUploadEntity> findByBatchId(Integer batchId);

    long countByBatchId(Integer batchId);

    boolean existsChecksumInBatch(Integer batchId, String checksum);

    boolean existsByBatchIdAndFileRole(Integer batchId, CsvFileRole fileRole);

    void updateValidationResult(
            Integer uploadId,
            String status,
            Integer totalRecords,
            Integer validRecords,
            Integer errorRecords,
            String errorDetail
    );
}
