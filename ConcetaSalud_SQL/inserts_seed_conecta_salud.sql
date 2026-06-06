USE conecta_salud;

-- ============================================================
-- DEPARTAMENTOS BASE
-- ============================================================

INSERT INTO departments (name)
VALUES
('Unidad de Análisis'),
('Dirección de Planeación'),
('Secretaría de Planeación Regional')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================
-- PERIODOS BASE
-- ============================================================

INSERT INTO periods (period_year, status, description)
VALUES
(2018, 'published', 'Año fuente disponible en indicadores sectoriales y vulnerabilidad.'),
(2020, 'published', 'Año base poblacional INEGI y fuente sectorial.'),
(2022, 'published', 'Año fuente disponible en indicadores sectoriales y vulnerabilidad.'),
(2024, 'published', 'Año fuente disponible en indicadores sectoriales y vulnerabilidad.'),
(2026, 'open', 'Año de catálogo vigente de establecimientos DGIS.')
ON DUPLICATE KEY UPDATE
    status = VALUES(status),
    description = VALUES(description);

-- ============================================================
-- FUENTES DE DATOS
-- ============================================================

INSERT INTO data_sources (
    code,
    name,
    institution,
    description,
    refresh_frequency
)
VALUES
(
    'inegi_population',
    'Indicadores poblacionales INEGI',
    'INEGI',
    'Datos poblacionales por México, entidad federativa y municipio.',
    'Decenal para población base'
),
(
    'coneval_healthcare_deficiency',
    'Carencia por acceso a servicios de salud',
    'CONEVAL / INEGI',
    'Indicador de carencia por acceso a servicios de salud disponible para México y entidades federativas.',
    'Bienal'
),
(
    'coneval_poverty',
    'Población en situación de pobreza',
    'CONEVAL / INEGI',
    'Indicador de población en situación de pobreza disponible para México y entidades federativas.',
    'Bienal'
),
(
    'dgis_sectorial',
    'BD Abiertos Sectorial DGIS',
    'DGIS Gobierno de México',
    'Información sectorial de personal médico, enfermería, especialistas, camas, consultorios e infraestructura.',
    'Bienal'
),
(
    'dgis_establishments',
    'Catálogo de establecimientos de salud DGIS',
    'DGIS Gobierno de México',
    'Catálogo vigente de establecimientos de salud.',
    'Variable'
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    institution = VALUES(institution),
    description = VALUES(description),
    refresh_frequency = VALUES(refresh_frequency);

-- ============================================================
-- CATEGORÍAS
-- ============================================================

INSERT INTO indicator_categories (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES
(
    'population',
    'Población',
    'Indicadores poblacionales base.',
    1,
    TRUE
),
(
    'population_vulnerability',
    'Vulnerabilidad poblacional',
    'Indicadores de carencia y pobreza disponibles a nivel nacional y estatal.',
    2,
    TRUE
),
(
    'medical_coverage',
    'Cobertura médica',
    'Indicadores de personal médico y enfermería.',
    3,
    TRUE
),
(
    'hospital_infrastructure',
    'Infraestructura hospitalaria',
    'Indicadores de camas, consultorios e infraestructura física.',
    4,
    TRUE
),
(
    'establishments',
    'Establecimientos de salud',
    'Indicadores de unidades y establecimientos de salud.',
    5,
    TRUE
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

-- ============================================================
-- INDICADORES
-- ============================================================

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'total_population',
    'Población total',
    'Población total del territorio.',
    'personas',
    'integer',
    TRUE,
    FALSE,
    NULL,
    1,
    TRUE
FROM indicator_categories c
WHERE c.code = 'population'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'percentage_over_60',
    'Porcentaje de población de 60 años y más',
    'Porcentaje de población adulta mayor respecto a la población total.',
    'porcentaje',
    'percentage',
    FALSE,
    FALSE,
    NULL,
    2,
    TRUE
FROM indicator_categories c
WHERE c.code = 'population'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'healthcare_access_deficiency',
    'Carencia por acceso a servicios de salud',
    'Población con carencia por acceso a servicios de salud.',
    'personas',
    'integer',
    FALSE,
    FALSE,
    NULL,
    1,
    TRUE
FROM indicator_categories c
WHERE c.code = 'population_vulnerability'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'total_poverty_population',
    'Población en situación de pobreza',
    'Población en situación de pobreza.',
    'personas',
    'integer',
    FALSE,
    FALSE,
    NULL,
    2,
    TRUE
FROM indicator_categories c
WHERE c.code = 'population_vulnerability'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'total_doctors',
    'Total de médicos',
    'Cantidad total de médicos registrados.',
    'personas',
    'integer',
    TRUE,
    FALSE,
    NULL,
    1,
    TRUE
FROM indicator_categories c
WHERE c.code = 'medical_coverage'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'total_nurses',
    'Total de enfermeras',
    'Cantidad total de enfermeras registradas.',
    'personas',
    'integer',
    TRUE,
    FALSE,
    NULL,
    2,
    TRUE
FROM indicator_categories c
WHERE c.code = 'medical_coverage'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'doctors_per_1000',
    'Médicos por cada 1,000 habitantes',
    'Tasa de médicos por cada 1,000 habitantes usando población base disponible.',
    'tasa',
    'rate',
    TRUE,
    TRUE,
    'total_doctors / total_population * 1000',
    3,
    TRUE
FROM indicator_categories c
WHERE c.code = 'medical_coverage'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'hospital_beds',
    'Camas hospitalarias',
    'Cantidad de camas hospitalarias registradas.',
    'unidades',
    'integer',
    TRUE,
    FALSE,
    NULL,
    1,
    TRUE
FROM indicator_categories c
WHERE c.code = 'hospital_infrastructure'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'beds_per_1000',
    'Camas por cada 1,000 habitantes',
    'Tasa de camas hospitalarias por cada 1,000 habitantes usando población base disponible.',
    'tasa',
    'rate',
    TRUE,
    TRUE,
    'hospital_beds / total_population * 1000',
    2,
    TRUE
FROM indicator_categories c
WHERE c.code = 'hospital_infrastructure'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'consulting_rooms',
    'Consultorios',
    'Cantidad de consultorios registrados.',
    'unidades',
    'integer',
    TRUE,
    FALSE,
    NULL,
    3,
    TRUE
FROM indicator_categories c
WHERE c.code = 'hospital_infrastructure'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

INSERT INTO indicators (
    category_id,
    code,
    name,
    description,
    unit,
    value_type,
    higher_is_better,
    is_calculated,
    formula_description,
    display_order,
    is_active
)
SELECT
    c.id,
    'health_establishments',
    'Establecimientos de salud',
    'Cantidad de establecimientos de salud registrados.',
    'unidades',
    'integer',
    TRUE,
    FALSE,
    NULL,
    1,
    TRUE
FROM indicator_categories c
WHERE c.code = 'establishments'
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);