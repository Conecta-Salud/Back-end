# ConectaSalud Back-end

Back-end de ConectaSalud construido con Quarkus, Java, MySQL, Hibernate ORM y Flyway.

## Setup local

Orden recomendado para levantar un ambiente local limpio:

1. Crear la base de datos con `conecta_salud.sql`.
2. Ejecutar el seed minimo manual oficial, si esta disponible para el ambiente.
3. Configurar `FIREBASE_SERVICE_ACCOUNT` apuntando a un JSON externo al repositorio.
4. Configurar variables de base de datos y CORS.
5. Levantar Quarkus.
6. Cargar CSVs en este orden:
   - poblacion;
   - establecimientos;
   - sectorial.

Los periodos no se cargan por seed manual. Los procesadores CSV deben asegurarlos automaticamente mediante `PeriodCatalogWriter`.

## Variables de entorno

No se deben versionar secretos ni rutas personales. Configura el runtime con variables:

```properties
DB_URL=jdbc:mysql://...
DB_USERNAME=...
DB_PASSWORD=...
CORS_ORIGINS=http://localhost:5173
FIREBASE_SERVICE_ACCOUNT=/absolute/path/outside/repo/service-account.json
```

`FIREBASE_SERVICE_ACCOUNT` debe apuntar a un archivo fuera del repositorio.

## Ejecucion

Modo desarrollo:

```shell
mvnw.cmd quarkus:dev
```

Empaquetado:

```shell
mvnw.cmd clean package
```

Validacion rapida sin tests:

```shell
mvnw.cmd clean package -DskipTests
```

## Seed minimo manual

La carpeta `src/main/resources/db/seed/` documenta el alcance permitido del seed manual minimo.

El seed minimo oficial debe ser Workbench friendly, idempotente y ejecutarse despues de `conecta_salud.sql`. Solo debe contener catalogos base:

- `departments`
- `users`
- `data_sources`
- `indicator_categories`
- `indicators`
- `specialties`
- `infrastructure_types`

No debe contener datos operativos, derivados o de carga:

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

Actualmente no hay un seed SQL minimo oficial versionado en esta carpeta. No agregues seeds historicos o parciales que contradigan el flujo CSV actual.

## Flujo CSV actual

Los flujos reales de carga son:

| sourceType | fileRole |
| --- | --- |
| `population` | `population_municipal_base` |
| `population` | `population_state_national_indicators` |
| `health_establishments` | `establishments_catalog` |
| `health_sectorial` | `sectorial_data` |

Orden funcional recomendado:

1. `population` con `population_municipal_base`.
2. `population` con `population_state_national_indicators`.
3. `health_establishments` con `establishments_catalog`.
4. `health_sectorial` con `sectorial_data`.

## Roles legacy

Estos `fileRole` siguen existiendo en codigo por compatibilidad, pero no son recomendados para el nuevo flujo:

- `population_indicators`
- `population_total`
- `percentage_over_60`
- `healthcare_access_deficiency`
- `total_poverty_population`

No documentarlos como flujo principal ni usarlos para nuevas cargas salvo que se este atendiendo compatibilidad legacy.

## Indicadores y disponibilidad

La tabla central de indicadores es `territory_indicator_values`.

La disponibilidad se expone y persiste con `data_availability`.

Las tablas legacy `state_indicators` y `municipality_indicators` ya no forman parte del flujo actual. Si aparecen en documentacion antigua, esa documentacion debe actualizarse o archivarse. Si aparecen en codigo productivo, revisar antes de eliminar.

## Seguridad

- No versionar archivos JSON reales de Firebase.
- No versionar `target/`.
- No versionar `uploads/` ni archivos CSV cargados por usuarios.
- Mantener secretos en variables de entorno o en gestores externos.
