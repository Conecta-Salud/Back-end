# Seed manual minimo de ConectaSalud

Esta carpeta debe contener un unico seed manual recomendado para ambientes locales o de inicializacion controlada, ejecutado despues de crear la base con `conecta_salud.sql`.

El seed manual minimo oficial debe ser Workbench friendly, idempotente y limitarse a catalogos base:

- `departments`
- `users`
- `data_sources`
- `indicator_categories`
- `indicators`
- `specialties`
- `infrastructure_types`

No debe sembrar datos operativos, territoriales o derivados. En particular, no debe insertar manualmente:

- `periods`
- `states`
- `municipalities`
- `institutions`
- `establishment_types`
- `medical_unit_types`
- `health_units`
- `territory_indicator_values`
- `data_availability`
- `indicator_availability_rules`
- `upload_batches`
- `data_uploads`
- `data_upload_errors`
- logs, exports o datos demo

Los periodos, estados, municipios, unidades de salud, disponibilidad e indicadores territoriales se derivan de los procesadores CSV actuales. Los periodos son asegurados automaticamente por `PeriodCatalogWriter`.

Actualmente no hay un seed SQL minimo oficial versionado en esta carpeta. No agregues seeds manuales parciales o historicos; si se incorpora uno, debe cumplir el alcance anterior y quedar como el unico SQL recomendado.
