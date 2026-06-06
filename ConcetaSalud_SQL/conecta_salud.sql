DROP DATABASE IF EXISTS conecta_salud;
CREATE DATABASE conecta_salud
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE conecta_salud;

-- ============================================================
-- CONECTASALUD - ESQUEMA PROFESIONAL
-- Modelo flexible para indicadores, fuentes, disponibilidad,
-- salud sectorial, establecimientos y administración.
-- ============================================================

-- ============================================================
-- 1. CATÁLOGOS ORGANIZACIONALES
-- ============================================================

CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE institutions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(180) NOT NULL UNIQUE,
    acronym VARCHAR(50) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 2. CATÁLOGO TERRITORIAL
-- ============================================================

CREATE TABLE states (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE,
    inegi_code VARCHAR(10) UNIQUE NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE municipalities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    state_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    inegi_code VARCHAR(15) UNIQUE NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (state_id) REFERENCES states(id),

    UNIQUE KEY uk_municipality_state (state_id, name),
    INDEX idx_municipalities_state (state_id),
    INDEX idx_municipalities_name (name),
    INDEX idx_municipalities_state_name (state_id, name)
) ENGINE=InnoDB;

-- ============================================================
-- 3. USUARIOS
-- ============================================================

CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    department_id INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    firebase_uuid VARCHAR(128) NOT NULL UNIQUE,
    role ENUM('strategic', 'admin') NOT NULL DEFAULT 'strategic',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (department_id) REFERENCES departments(id),

    INDEX idx_users_email (email),
    INDEX idx_users_firebase_uuid (firebase_uuid),
    INDEX idx_users_role_active (role, is_active),
    INDEX idx_users_last_login (last_login_at)
) ENGINE=InnoDB;

-- ============================================================
-- 4. PERIODOS / AÑOS DE ANÁLISIS
-- ============================================================

CREATE TABLE periods (
    id INT PRIMARY KEY AUTO_INCREMENT,
    period_year SMALLINT NOT NULL UNIQUE,
    status ENUM('open', 'closed', 'published') NOT NULL DEFAULT 'open',
    description VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_periods_year_status (period_year, status)
) ENGINE=InnoDB;

-- ============================================================
-- 5. FUENTES DE DATOS
-- ============================================================

CREATE TABLE data_sources (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    institution VARCHAR(180) NOT NULL,
    description TEXT NULL,
    official_url VARCHAR(500) NULL,
    refresh_frequency VARCHAR(120) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_data_sources_code (code)
) ENGINE=InnoDB;

-- ============================================================
-- 6. CATEGORÍAS E INDICADORES
-- ============================================================

CREATE TABLE indicator_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT NULL,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_indicator_categories_active_order (is_active, display_order)
) ENGINE=InnoDB;

CREATE TABLE indicators (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT NULL,
    unit VARCHAR(50) NOT NULL,
    value_type ENUM('integer', 'decimal', 'percentage', 'rate', 'index') NOT NULL DEFAULT 'decimal',
    higher_is_better BOOLEAN NOT NULL DEFAULT TRUE,
    is_calculated BOOLEAN NOT NULL DEFAULT FALSE,
    formula_description TEXT NULL,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES indicator_categories(id),

    INDEX idx_indicators_category (category_id),
    INDEX idx_indicators_active_order (is_active, display_order),
    INDEX idx_indicators_code (code)
) ENGINE=InnoDB;

-- ============================================================
-- 7. REGLAS DE DISPONIBILIDAD POR NIVEL TERRITORIAL
-- ============================================================

CREATE TABLE indicator_availability_rules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    indicator_id INT NOT NULL,
    territory_level ENUM('country', 'state', 'municipality') NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    availability_note VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (indicator_id) REFERENCES indicators(id),

    UNIQUE KEY uk_indicator_availability_rule (
        indicator_id,
        territory_level
    ),

    INDEX idx_indicator_availability_level (
        territory_level,
        is_available
    )
) ENGINE=InnoDB;

-- ============================================================
-- 8. VALORES DE INDICADORES TERRITORIALES
-- Tabla central flexible.
-- Reemplaza state_indicators y municipality_indicators rígidas.
-- ============================================================

CREATE TABLE territory_indicator_values (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    territory_level ENUM('country', 'state', 'municipality') NOT NULL,

    state_id INT NULL,
    municipality_id INT NULL,

    indicator_id INT NOT NULL,

    value DECIMAL(18,4) NULL,

    analysis_year SMALLINT NOT NULL,
    source_year SMALLINT NULL,

    data_source_id INT NOT NULL,
    source_file VARCHAR(255) NULL,

    availability_status ENUM(
        'available',
        'not_available',
        'not_applicable',
        'estimated'
    ) NOT NULL DEFAULT 'available',

    methodology_note TEXT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (state_id) REFERENCES states(id),
    FOREIGN KEY (municipality_id) REFERENCES municipalities(id),
    FOREIGN KEY (indicator_id) REFERENCES indicators(id),
    FOREIGN KEY (data_source_id) REFERENCES data_sources(id),

    CONSTRAINT chk_territory_consistency CHECK (
        (
            territory_level = 'country'
            AND state_id IS NULL
            AND municipality_id IS NULL
        )
        OR
        (
            territory_level = 'state'
            AND state_id IS NOT NULL
            AND municipality_id IS NULL
        )
        OR
        (
            territory_level = 'municipality'
            AND municipality_id IS NOT NULL
        )
    ),

    UNIQUE KEY uk_territory_indicator_value (
        territory_level,
        state_id,
        municipality_id,
        indicator_id,
        analysis_year
    ),

    INDEX idx_tiv_indicator_year (
        indicator_id,
        analysis_year
    ),

    INDEX idx_tiv_indicator_source_year (
        indicator_id,
        source_year
    ),

    INDEX idx_tiv_territory_year (
        territory_level,
        analysis_year
    ),

    INDEX idx_tiv_state_year (
        state_id,
        analysis_year
    ),

    INDEX idx_tiv_municipality_year (
        municipality_id,
        analysis_year
    ),

    INDEX idx_tiv_availability (
        availability_status
    )
) ENGINE=InnoDB;

-- ============================================================
-- 9. DISPONIBILIDAD MATERIALIZADA PARA FRONTEND
-- Sirve para filtros dinámicos de años/categorías/niveles.
-- ============================================================

CREATE TABLE data_availability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    category_id INT NOT NULL,
    indicator_id INT NULL,

    territory_level ENUM('country', 'state', 'municipality') NOT NULL,

    analysis_year SMALLINT NOT NULL,
    source_year SMALLINT NULL,

    is_available BOOLEAN NOT NULL DEFAULT TRUE,

    availability_status ENUM(
        'available',
        'partial',
        'not_available',
        'not_applicable',
        'estimated'
    ) NOT NULL DEFAULT 'available',

    note VARCHAR(255) NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES indicator_categories(id),
    FOREIGN KEY (indicator_id) REFERENCES indicators(id),

    UNIQUE KEY uk_data_availability (
        category_id,
        indicator_id,
        territory_level,
        analysis_year
    ),

    INDEX idx_data_availability_lookup (
        category_id,
        territory_level,
        analysis_year,
        is_available
    ),

    INDEX idx_data_availability_year (
        analysis_year
    ),

    INDEX idx_data_availability_status (
        availability_status
    )
) ENGINE=InnoDB;

-- ============================================================
-- 10. CATÁLOGOS DE UNIDADES MÉDICAS
-- ============================================================

CREATE TABLE establishment_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE medical_unit_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(180) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 11. UNIDADES DE SALUD / ESTABLECIMIENTOS
-- Catálogo actual. Por ahora CLUES único.
-- source_year indica el año del catálogo cargado.
-- ============================================================

CREATE TABLE health_units (
    id INT PRIMARY KEY AUTO_INCREMENT,

    clues VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    municipality_id INT NOT NULL,
    institution_id INT NOT NULL,
    establishment_type_id INT NOT NULL,
    medical_unit_type_id INT NOT NULL,

    care_level ENUM(
        'primary',
        'secondary',
        'tertiary',
        'not_specified'
    ) NOT NULL DEFAULT 'not_specified',

    source_year SMALLINT NULL,
    operation_status VARCHAR(100) NULL,

    locality_name VARCHAR(180) NULL,
    address TEXT NULL,

    latitude DECIMAL(10,7) NULL,
    longitude DECIMAL(10,7) NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (municipality_id) REFERENCES municipalities(id),
    FOREIGN KEY (institution_id) REFERENCES institutions(id),
    FOREIGN KEY (establishment_type_id) REFERENCES establishment_types(id),
    FOREIGN KEY (medical_unit_type_id) REFERENCES medical_unit_types(id),

    INDEX idx_health_units_municipality (municipality_id),
    INDEX idx_health_units_institution (institution_id),
    INDEX idx_health_units_care_level (care_level),
    INDEX idx_health_units_establishment_type (establishment_type_id),
    INDEX idx_health_units_medical_unit_type (medical_unit_type_id),
    INDEX idx_health_units_source_year (source_year),
    INDEX idx_health_units_operation_status (operation_status),
    INDEX idx_health_units_active (is_active),
    INDEX idx_health_units_location (latitude, longitude)
) ENGINE=InnoDB;

-- ============================================================
-- 12. ESPECIALIDADES MÉDICAS
-- ============================================================

CREATE TABLE specialties (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_specialties_code (code)
) ENGINE=InnoDB;

-- ============================================================
-- 13. PERSONAL DE UNIDADES DE SALUD POR AÑO
-- Los años aquí corresponden al año fuente sectorial DGIS.
-- ============================================================

CREATE TABLE health_unit_staff (
    id INT PRIMARY KEY AUTO_INCREMENT,
    health_unit_id INT NOT NULL,
    period_id INT NOT NULL,

    total_doctors INT NOT NULL DEFAULT 0 CHECK (total_doctors >= 0),
    total_nurses INT NOT NULL DEFAULT 0 CHECK (total_nurses >= 0),

    data_source_id INT NULL,
    source_file VARCHAR(255) NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (health_unit_id) REFERENCES health_units(id),
    FOREIGN KEY (period_id) REFERENCES periods(id),
    FOREIGN KEY (data_source_id) REFERENCES data_sources(id),

    UNIQUE KEY uk_health_unit_staff_period (
        health_unit_id,
        period_id
    ),

    INDEX idx_health_unit_staff_period_unit (
        period_id,
        health_unit_id
    ),

    INDEX idx_health_unit_staff_source (
        data_source_id
    )
) ENGINE=InnoDB;

CREATE TABLE health_unit_staff_specialties (
    id INT PRIMARY KEY AUTO_INCREMENT,
    health_unit_staff_id INT NOT NULL,
    specialty_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (health_unit_staff_id) REFERENCES health_unit_staff(id),
    FOREIGN KEY (specialty_id) REFERENCES specialties(id),

    UNIQUE KEY uk_health_unit_staff_specialty (
        health_unit_staff_id,
        specialty_id
    ),

    INDEX idx_health_unit_staff_specialties_staff (
        health_unit_staff_id
    ),

    INDEX idx_health_unit_staff_specialties_specialty (
        specialty_id
    )
) ENGINE=InnoDB;

-- ============================================================
-- 14. INFRAESTRUCTURA DE UNIDADES DE SALUD POR AÑO
-- ============================================================

CREATE TABLE infrastructure_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL UNIQUE,
    unit VARCHAR(50) NOT NULL DEFAULT 'unidades',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_infrastructure_types_code (code)
) ENGINE=InnoDB;

CREATE TABLE health_unit_infrastructure (
    id INT PRIMARY KEY AUTO_INCREMENT,
    health_unit_id INT NOT NULL,
    period_id INT NOT NULL,

    data_source_id INT NULL,
    source_file VARCHAR(255) NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (health_unit_id) REFERENCES health_units(id),
    FOREIGN KEY (period_id) REFERENCES periods(id),
    FOREIGN KEY (data_source_id) REFERENCES data_sources(id),

    UNIQUE KEY uk_health_unit_infrastructure_period (
        health_unit_id,
        period_id
    ),

    INDEX idx_health_unit_infrastructure_period_unit (
        period_id,
        health_unit_id
    ),

    INDEX idx_health_unit_infrastructure_source (
        data_source_id
    )
) ENGINE=InnoDB;

CREATE TABLE health_unit_infrastructure_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    health_unit_infrastructure_id INT NOT NULL,
    infrastructure_type_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (health_unit_infrastructure_id) REFERENCES health_unit_infrastructure(id),
    FOREIGN KEY (infrastructure_type_id) REFERENCES infrastructure_types(id),

    UNIQUE KEY uk_health_unit_infrastructure_detail (
        health_unit_infrastructure_id,
        infrastructure_type_id
    ),

    INDEX idx_health_unit_infrastructure_details_parent (
        health_unit_infrastructure_id
    ),

    INDEX idx_health_unit_infrastructure_details_type (
        infrastructure_type_id
    )
) ENGINE=InnoDB;

-- ============================================================
-- 15. CARGAS ADMINISTRATIVAS
-- ============================================================

CREATE TABLE upload_batches (
    id INT PRIMARY KEY AUTO_INCREMENT,

    user_id CHAR(36) NOT NULL,

    data_source_id INT NOT NULL,

    source_type ENUM(
        'population',
        'health_establishments',
        'health_sectorial'
    ) NOT NULL,

    source_year SMALLINT NOT NULL,
    analysis_year SMALLINT NULL,

    batch_version VARCHAR(80) NOT NULL,

    status ENUM(
        'pending',
        'processing',
        'completed',
        'warning',
        'error'
    ) NOT NULL DEFAULT 'pending',

    expected_files INT NOT NULL DEFAULT 1 CHECK (expected_files >= 1),
    uploaded_files INT NOT NULL DEFAULT 0 CHECK (uploaded_files >= 0),

    total_records INT NOT NULL DEFAULT 0 CHECK (total_records >= 0),
    valid_records INT NOT NULL DEFAULT 0 CHECK (valid_records >= 0),
    error_records INT NOT NULL DEFAULT 0 CHECK (error_records >= 0),

    error_detail TEXT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (data_source_id) REFERENCES data_sources(id),

    UNIQUE KEY uk_upload_batch_version (
        source_type,
        source_year,
        batch_version
    ),

    INDEX idx_upload_batches_source_year_status (
        source_type,
        source_year,
        status
    ),

    INDEX idx_upload_batches_user_created (
        user_id,
        created_at
    ),

    INDEX idx_upload_batches_data_source (
        data_source_id
    )
) ENGINE=InnoDB;

CREATE TABLE data_uploads (
    id INT PRIMARY KEY AUTO_INCREMENT,

    batch_id INT NOT NULL,

    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NULL,
    file_version VARCHAR(80) NULL,

    file_size BIGINT NULL CHECK (file_size IS NULL OR file_size >= 0),
    mime_type VARCHAR(100) NULL,
    checksum VARCHAR(128) NULL,

    status ENUM(
        'pending',
        'processing',
        'completed',
        'warning',
        'error'
    ) NOT NULL DEFAULT 'pending',

    total_records INT NOT NULL DEFAULT 0 CHECK (total_records >= 0),
    valid_records INT NOT NULL DEFAULT 0 CHECK (valid_records >= 0),
    error_records INT NOT NULL DEFAULT 0 CHECK (error_records >= 0),

    error_detail TEXT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL,

    FOREIGN KEY (batch_id) REFERENCES upload_batches(id),

    UNIQUE KEY uk_data_upload_checksum (
        batch_id,
        checksum
    ),

    INDEX idx_data_uploads_batch_status (
        batch_id,
        status
    )
) ENGINE=InnoDB;

CREATE TABLE data_upload_errors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    data_upload_id INT NOT NULL,

    csv_row_number INT NULL,
    column_name VARCHAR(150) NULL,
    raw_value TEXT NULL,
    error_code VARCHAR(100) NOT NULL,
    error_message TEXT NOT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (data_upload_id) REFERENCES data_uploads(id),

    INDEX idx_data_upload_errors_upload (
        data_upload_id
    ),

    INDEX idx_data_upload_errors_code (
        error_code
    ),

    INDEX idx_data_upload_errors_row (
        csv_row_number
    )
) ENGINE=InnoDB;

-- ============================================================
-- 16. AUDITORÍA Y EXPORTACIONES
-- ============================================================

CREATE TABLE system_activity_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id CHAR(36) NOT NULL,

    action VARCHAR(150) NOT NULL,
    module VARCHAR(100) NOT NULL,

    result ENUM(
        'success',
        'warning',
        'error'
    ) NOT NULL DEFAULT 'success',

    detail TEXT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),

    INDEX idx_activity_logs_user_created (
        user_id,
        created_at
    ),

    INDEX idx_activity_logs_module_result_created (
        module,
        result,
        created_at
    )
) ENGINE=InnoDB;

CREATE TABLE export_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id CHAR(36) NOT NULL,

    report_type VARCHAR(150) NOT NULL,
    format ENUM('excel', 'csv', 'pdf') NOT NULL DEFAULT 'excel',

    territory_level ENUM(
        'country',
        'state',
        'municipality'
    ) NULL,

    territory_name VARCHAR(255) NULL,
    analysis_year SMALLINT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),

    INDEX idx_export_history_user_created (
        user_id,
        created_at
    ),

    INDEX idx_export_history_report_year (
        report_type,
        analysis_year
    )
) ENGINE=InnoDB;