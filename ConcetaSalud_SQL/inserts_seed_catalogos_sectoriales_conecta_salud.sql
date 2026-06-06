USE conecta_salud;

-- ============================================================
-- TIPOS DE ESTABLECIMIENTO
-- ============================================================

INSERT INTO establishment_types (name)
VALUES
('DE APOYO'),
('DE ASISTENCIA SOCIAL'),
('DE CONSULTA EXTERNA'),
('DE HOSPITALIZACION'),
('NO ESPECIFICADO')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================
-- TIPOS DE UNIDAD MÉDICA
-- ============================================================

INSERT INTO medical_unit_types (name)
VALUES
('Clínica Hospital'),
('Hospital Móvil'),
('Hospital General'),
('Centro de Salud'),
('Unidad Médica'),
('No especificado')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================
-- ESPECIALIDADES
-- ============================================================

INSERT INTO specialties (code, name)
VALUES
('medicos_generales', 'Médicos generales'),
('pediatras', 'Pediatras'),
('ginecoobstetras', 'Ginecoobstetras'),
('cirujanos', 'Cirujanos'),
('geriatras', 'Geriatras'),
('oftalmologos', 'Oftalmólogos'),
('traumatologos', 'Traumatólogos'),
('dermatologos', 'Dermatólogos'),
('odontologos', 'Odontólogos'),
('cardiologos', 'Cardiólogos'),
('urgenciologos', 'Urgenciólogos'),
('internistas', 'Internistas'),
('anestesiologos', 'Anestesiólogos')
ON DUPLICATE KEY UPDATE
    name = VALUES(name);

-- ============================================================
-- INFRAESTRUCTURA
-- ============================================================

INSERT INTO infrastructure_types (code, name, unit)
VALUES
('total_camas_hospitalizacion', 'Total de camas de hospitalización', 'unidades'),
('total_consultorios', 'Total de consultorios', 'unidades'),
('total_quirofanos', 'Total de quirófanos', 'unidades')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    unit = VALUES(unit);