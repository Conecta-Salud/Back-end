package com.itesm.domain.repository;

import com.itesm.domain.models.common.PageResult;
import com.itesm.domain.models.upload.UploadErrorDraft;
import com.itesm.infrastructure.persistence.entity.DataUploadErrorEntity;

import java.util.List;

public interface DataUploadErrorRepository {

    void replaceErrors(Integer uploadId, List<UploadErrorDraft> errors);

    void appendErrors(Integer uploadId, List<UploadErrorDraft> errors);

    void deleteByUploadId(Integer uploadId);

    PageResult<DataUploadErrorEntity> findByUploadId(Integer uploadId, int page, int size);

    long countByUploadId(Integer uploadId);
}
