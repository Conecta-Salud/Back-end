SET @data_uploads_file_role_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'data_uploads'
      AND COLUMN_NAME = 'file_role'
);

SET @add_data_uploads_file_role = IF(
    @data_uploads_file_role_exists = 0,
    'ALTER TABLE data_uploads ADD COLUMN file_role VARCHAR(80) NULL AFTER file_version',
    'SELECT 1'
);

PREPARE add_data_uploads_file_role_stmt FROM @add_data_uploads_file_role;
EXECUTE add_data_uploads_file_role_stmt;
DEALLOCATE PREPARE add_data_uploads_file_role_stmt;

SET @upload_batches_processing_mode_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'upload_batches'
      AND COLUMN_NAME = 'processing_mode'
);

SET @add_upload_batches_processing_mode = IF(
    @upload_batches_processing_mode_exists = 0,
    'ALTER TABLE upload_batches ADD COLUMN processing_mode VARCHAR(40) NOT NULL DEFAULT ''validate_only'' AFTER batch_version',
    'SELECT 1'
);

PREPARE add_upload_batches_processing_mode_stmt FROM @add_upload_batches_processing_mode;
EXECUTE add_upload_batches_processing_mode_stmt;
DEALLOCATE PREPARE add_upload_batches_processing_mode_stmt;
