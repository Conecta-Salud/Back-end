-- Manual seed for DGIS health establishments indicator.
-- Run this after the base catalog seed if `health_establishments` is missing.

INSERT INTO indicator_categories (
    code,
    name,
    description,
    display_order,
    is_active
)
VALUES (
    'health_infrastructure',
    'Infraestructura de salud',
    'Indicadores de infraestructura y establecimientos de salud.',
    30,
    1
)
ON DUPLICATE KEY UPDATE
    id = LAST_INSERT_ID(id),
    name = VALUES(name),
    description = VALUES(description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);

SET @health_infrastructure_category_id = LAST_INSERT_ID();

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
VALUES (
    @health_infrastructure_category_id,
    'health_establishments',
    'Establecimientos de salud',
    'Conteo de establecimientos de salud registrados en el catálogo DGIS.',
    'establecimientos',
    'integer',
    1,
    1,
    'COUNT(*) de health_units activos por año fuente y territorio.',
    10,
    1
)
ON DUPLICATE KEY UPDATE
    category_id = VALUES(category_id),
    name = VALUES(name),
    description = VALUES(description),
    unit = VALUES(unit),
    value_type = VALUES(value_type),
    higher_is_better = VALUES(higher_is_better),
    is_calculated = VALUES(is_calculated),
    formula_description = VALUES(formula_description),
    display_order = VALUES(display_order),
    is_active = VALUES(is_active);
