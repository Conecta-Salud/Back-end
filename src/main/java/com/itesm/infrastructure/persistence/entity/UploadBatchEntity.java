package com.itesm.infrastructure.persistence.entity;

import com.itesm.domain.models.upload.UploadSourceType;
import com.itesm.domain.models.upload.UploadProcessingMode;
import com.itesm.domain.models.upload.UploadStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "upload_batches")
public class UploadBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "data_source_id", nullable = false)
    private DataSourceEntity dataSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private UploadSourceType sourceType;

    @Column(name = "source_year", nullable = false)
    private Short sourceYear;

    @Column(name = "analysis_year")
    private Short analysisYear;

    @Column(name = "batch_version", nullable = false, length = 80)
    private String batchVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_mode", nullable = false)
    private UploadProcessingMode processingMode = UploadProcessingMode.validate_only;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus status = UploadStatus.pending;

    @Column(name = "expected_files", nullable = false)
    private Integer expectedFiles = 1;

    @Column(name = "uploaded_files", nullable = false)
    private Integer uploadedFiles = 0;

    @Column(name = "total_records", nullable = false)
    private Integer totalRecords = 0;

    @Column(name = "valid_records", nullable = false)
    private Integer validRecords = 0;

    @Column(name = "error_records", nullable = false)
    private Integer errorRecords = 0;

    @Column(name = "error_detail", columnDefinition = "TEXT")
    private String errorDetail;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public DataSourceEntity getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEntity dataSource) {
        this.dataSource = dataSource;
    }

    public UploadSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(UploadSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Short getSourceYear() {
        return sourceYear;
    }

    public void setSourceYear(Short sourceYear) {
        this.sourceYear = sourceYear;
    }

    public Short getAnalysisYear() {
        return analysisYear;
    }

    public void setAnalysisYear(Short analysisYear) {
        this.analysisYear = analysisYear;
    }

    public String getBatchVersion() {
        return batchVersion;
    }

    public void setBatchVersion(String batchVersion) {
        this.batchVersion = batchVersion;
    }

    public UploadProcessingMode getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(UploadProcessingMode processingMode) {
        this.processingMode = processingMode;
    }

    public UploadStatus getStatus() {
        return status;
    }

    public void setStatus(UploadStatus status) {
        this.status = status;
    }

    public Integer getExpectedFiles() {
        return expectedFiles;
    }

    public void setExpectedFiles(Integer expectedFiles) {
        this.expectedFiles = expectedFiles;
    }

    public Integer getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(Integer uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getValidRecords() {
        return validRecords;
    }

    public void setValidRecords(Integer validRecords) {
        this.validRecords = validRecords;
    }

    public Integer getErrorRecords() {
        return errorRecords;
    }

    public void setErrorRecords(Integer errorRecords) {
        this.errorRecords = errorRecords;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
