USE conecta_salud;

-- ============================================================
-- REGLAS DE DISPONIBILIDAD POR INDICADOR Y NIVEL
-- ============================================================

-- Indicadores disponibles en country, state y municipality
INSERT INTO indicator_availability_rules (
    indicator_id,
    territory_level,
    is_available,
    availability_note
)
SELECT i.id, levels.territory_level, TRUE, NULL
FROM indicators i
JOIN (
    SELECT 'country' AS territory_level
    UNION ALL SELECT 'state'
    UNION ALL SELECT 'municipality'
) levels
WHERE i.code IN (
    'total_population',
    'percentage_over_60'
)
ON DUPLICATE KEY UPDATE
    is_available = VALUES(is_available),
    availability_note = VALUES(availability_note);

-- Vulnerabilidad disponible solo country y state
INSERT INTO indicator_availability_rules (
    indicator_id,
    territory_level,
    is_available,
    availability_note
)
SELECT i.id, levels.territory_level, TRUE, NULL
FROM indicators i
JOIN (
    SELECT 'country' AS territory_level
    UNION ALL SELECT 'state'
) levels
WHERE i.code IN (
    'healthcare_access_deficiency',
    'total_poverty_population'
)
ON DUPLICATE KEY UPDATE
    is_available = VALUES(is_available),
    availability_note = VALUES(availability_note);

-- Vulnerabilidad NO disponible en municipality
INSERT INTO indicator_availability_rules (
    indicator_id,
    territory_level,
    is_available,
    availability_note
)
SELECT
    i.id,
    'municipality',
    FALSE,
    'No disponible a nivel municipal con las fuentes cargadas.'
FROM indicators i
WHERE i.code IN (
    'healthcare_access_deficiency',
    'total_poverty_population'
)
ON DUPLICATE KEY UPDATE
    is_available = VALUES(is_available),
    availability_note = VALUES(availability_note);

-- Sectorial disponible state y municipality
INSERT INTO indicator_availability_rules (
    indicator_id,
    territory_level,
    is_available,
    availability_note
)
SELECT i.id, levels.territory_level, TRUE, NULL
FROM indicators i
JOIN (
    SELECT 'state' AS territory_level
    UNION ALL SELECT 'municipality'
) levels
WHERE i.code IN (
    'total_doctors',
    'total_nurses',
    'doctors_per_1000',
    'hospital_beds',
    'beds_per_1000',
    'consulting_rooms',
    'health_establishments'
)
ON DUPLICATE KEY UPDATE
    is_available = VALUES(is_available),
    availability_note = VALUES(availability_note);

-- ============================================================
-- DISPONIBILIDAD MATERIALIZADA
-- ============================================================

-- Población base 2020: country/state/municipality
INSERT INTO data_availability (
    category_id,
    indicator_id,
    territory_level,
    analysis_year,
    source_year,
    is_available,
    availability_status,
    note
)
SELECT
    c.id,
    i.id,
    levels.territory_level,
    2020,
    2020,
    TRUE,
    'available',
    'Dato poblacional base disponible para 2020.'
FROM indicators i
JOIN indicator_categories c ON c.id = i.category_id
JOIN (
    SELECT 'country' AS territory_level
    UNION ALL SELECT 'state'
    UNION ALL SELECT 'municipality'
) levels
WHERE i.code IN (
    'total_population',
    'percentage_over_60'
)
ON DUPLICATE KEY UPDATE
    source_year = VALUES(source_year),
    is_available = VALUES(is_available),
    availability_status = VALUES(availability_status),
    note = VALUES(note);

-- Vulnerabilidad 2018/2020/2022/2024: country/state
INSERT INTO data_availability (
    category_id,
    indicator_id,
    territory_level,
    analysis_year,
    source_year,
    is_available,
    availability_status,
    note
)
SELECT
    c.id,
    i.id,
    levels.territory_level,
    years.analysis_year,
    years.analysis_year,
    TRUE,
    'available',
    'Indicador disponible para México y entidades federativas.'
FROM indicators i
JOIN indicator_categories c ON c.id = i.category_id
JOIN (
    SELECT 'country' AS territory_level
    UNION ALL SELECT 'state'
) levels
JOIN (
    SELECT 2018 AS analysis_year
    UNION ALL SELECT 2020
    UNION ALL SELECT 2022
    UNION ALL SELECT 2024
) years
WHERE i.code IN (
    'healthcare_access_deficiency',
    'total_poverty_population'
)
ON DUPLICATE KEY UPDATE
    source_year = VALUES(source_year),
    is_available = VALUES(is_available),
    availability_status = VALUES(availability_status),
    note = VALUES(note);

-- Vulnerabilidad NO disponible en municipio
INSERT INTO data_availability (
    category_id,
    indicator_id,
    territory_level,
    analysis_year,
    source_year,
    is_available,
    availability_status,
    note
)
SELECT
    c.id,
    i.id,
    'municipality',
    years.analysis_year,
    NULL,
    FALSE,
    'not_available',
    'No disponible a nivel municipal con las fuentes actuales.'
FROM indicators i
JOIN indicator_categories c ON c.id = i.category_id
JOIN (
    SELECT 2018 AS analysis_year
    UNION ALL SELECT 2020
    UNION ALL SELECT 2022
    UNION ALL SELECT 2024
) years
WHERE i.code IN (
    'healthcare_access_deficiency',
    'total_poverty_population'
)
ON DUPLICATE KEY UPDATE
    source_year = VALUES(source_year),
    is_available = VALUES(is_available),
    availability_status = VALUES(availability_status),
    note = VALUES(note);

-- Sectorial DGIS 2018/2020/2022/2024: state/municipality
INSERT INTO data_availability (
    category_id,
    indicator_id,
    territory_level,
    analysis_year,
    source_year,
    is_available,
    availability_status,
    note
)
SELECT
    c.id,
    i.id,
    levels.territory_level,
    years.analysis_year,
    years.analysis_year,
    TRUE,
    'available',
    'Dato sectorial DGIS disponible para el año seleccionado.'
FROM indicators i
JOIN indicator_categories c ON c.id = i.category_id
JOIN (
    SELECT 'state' AS territory_level
    UNION ALL SELECT 'municipality'
) levels
JOIN (
    SELECT 2018 AS analysis_year
    UNION ALL SELECT 2020
    UNION ALL SELECT 2022
    UNION ALL SELECT 2024
) years
WHERE i.code IN (
    'total_doctors',
    'total_nurses',
    'doctors_per_1000',
    'hospital_beds',
    'beds_per_1000',
    'consulting_rooms'
)
ON DUPLICATE KEY UPDATE
    source_year = VALUES(source_year),
    is_available = VALUES(is_available),
    availability_status = VALUES(availability_status),
    note = VALUES(note);

-- Establecimientos DGIS 2026: state/municipality
INSERT INTO data_availability (
    category_id,
    indicator_id,
    territory_level,
    analysis_year,
    source_year,
    is_available,
    availability_status,
    note
)
SELECT
    c.id,
    i.id,
    levels.territory_level,
    2026,
    2026,
    TRUE,
    'available',
    'Catálogo de establecimientos vigente con fuente 2026.'
FROM indicators i
JOIN indicator_categories c ON c.id = i.category_id
JOIN (
    SELECT 'state' AS territory_level
    UNION ALL SELECT 'municipality'
) levels
WHERE i.code = 'health_establishments'
ON DUPLICATE KEY UPDATE
    source_year = VALUES(source_year),
    is_available = VALUES(is_available),
    availability_status = VALUES(availability_status),
    note = VALUES(note);