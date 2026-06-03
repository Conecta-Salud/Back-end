package com.itesm.domain.repository;

import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.upload.UploadSourceType;
import com.itesm.domain.models.upload.UploadStatus;
import com.itesm.infrastructure.persistence.entity.DataSourceEntity;
import com.itesm.infrastructure.persistence.entity.UploadBatchEntity;

import java.util.Optional;
import java.util.UUID;

public interface UploadBatchRepository {

    Optional<DataSourceEntity> findDataSourceByCode(String code);

    UploadBatchEntity create(UploadBatchEntity batch, UUID userId, Integer dataSourceId);

    Optional<UploadBatchEntity> findById(Integer batchId);

    PageResult<UploadBatchEntity> findBatches(
            UploadSourceType sourceType,
            Integer sourceYear,
            UploadStatus status,
            int page,
            int size
    );

    void updateStatus(Integer batchId, UploadStatus status, String errorDetail, boolean processed);

    void recalculateCounters(Integer batchId);
}
