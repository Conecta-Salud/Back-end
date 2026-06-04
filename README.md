# ConectaSalud

ConectaSalud es un sistema para integrar, procesar y consultar informacion de salud publica de Mexico. El backend centraliza cargas CSV de poblacion, establecimientos y datos sectoriales; materializa indicadores territoriales; expone disponibilidad de datos; y alimenta vistas de dashboard, mapa, ranking y comparacion.

Este repositorio contiene el backend de ConectaSalud.

## Descripcion del Proyecto

El backend permite:

- autenticar usuarios mediante Firebase Auth;
- administrar usuarios y departamentos;
- cargar archivos CSV administrativos;
- validar y procesar datos poblacionales, establecimientos y salud sectorial;
- persistir indicadores agregados en `territory_indicator_values`;
- exponer disponibilidad de informacion con `data_availability`;
- consultar indicadores para dashboard, mapa, ranking y comparacion;
- registrar actividad administrativa relevante.

La tabla principal para indicadores agregados es `territory_indicator_values`. Los periodos se crean automaticamente desde los procesadores CSV mediante `PeriodCatalogWriter`; no deben cargarse por seed manual.

## Tecnologias Utilizadas

- Java 17
- Quarkus 3.31
- Maven Wrapper
- MySQL
- Hibernate ORM / Panache
- Flyway
- Firebase Admin SDK / Firebase Auth
- RESTEasy JSON-B
- Docker
- Google Cloud Build / Cloud Run

## Requisitos Previos

- JDK 17 o superior.
- MySQL 8 o compatible.
- Acceso a una base de datos `conecta_salud`.
- Archivo JSON de Firebase Admin SDK fuera del repositorio.
- Variables de entorno configuradas para base de datos, CORS y Firebase.

Opcional para despliegue:

- Docker.
- Google Cloud SDK.
- Proyecto Google Cloud con Artifact Registry y Cloud Run.

## Instalacion Local

1. Clonar el repositorio.
2. Crear la base de datos MySQL.
3. Crear la estructura con `conecta_salud.sql` si aplica al ambiente.
4. Ejecutar el seed minimo manual oficial:

```text
src/main/resources/db/seed/conecta_salud_seed_manual_minimo_workbench.sql
```

5. Configurar variables de entorno.
6. Levantar Quarkus.
7. Cargar CSVs en el orden funcional recomendado.

## Variables de Entorno

No versionar secretos ni rutas personales. Configurar el runtime con:

```properties
DB_URL=jdbc:mysql://localhost:3306/conecta_salud
DB_USERNAME=usuario_mysql
DB_PASSWORD=password_mysql
CORS_ORIGINS=http://localhost:5173
FIREBASE_SERVICE_ACCOUNT=/absolute/path/outside/repo/service-account.json
```

Detalle:

- `DB_URL`: URL JDBC de MySQL.
- `DB_USERNAME`: usuario de base de datos.
- `DB_PASSWORD`: password de base de datos.
- `CORS_ORIGINS`: origenes permitidos para frontend.
- `FIREBASE_SERVICE_ACCOUNT`: ruta absoluta a la credencial JSON de Firebase Admin SDK.

`FIREBASE_SERVICE_ACCOUNT` debe apuntar a un archivo externo al repositorio. No guardar JSON reales de Firebase dentro de `src/main/resources` ni en ninguna carpeta del proyecto.

Puedes usar `.env_example` como referencia, pero `.env` real no debe versionarse.

## Como Ejecutar el Proyecto

Modo desarrollo en Windows:

```shell
mvnw.cmd quarkus:dev
```

Modo desarrollo en Linux/macOS:

```shell
./mvnw quarkus:dev
```

Compilar paquete:

```shell
mvnw.cmd clean package
```

Validacion rapida sin tests:

```shell
mvnw.cmd clean package -DskipTests
```

La API local queda disponible por defecto en:

```text
http://localhost:8080
```

## Endpoints Principales

- `/users`
- `/departments`
- `/states`
- `/municipalities`
- `/periods`
- `/health-units`
- `/dashboard`
- `/comparison`
- `/comparison/summary`
- `/api/v1/map`
- `/api/v1/data-availability`
- `/api/v1/admin/uploads`
- `/admin/overview`
- `/admin/activity-logs`

## Flujo CSV Actual

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

## Seed Minimo Manual

La carpeta `src/main/resources/db/seed/` documenta el alcance permitido del seed manual minimo.

El seed minimo oficial es Workbench friendly, idempotente y debe ejecutarse despues de crear la estructura con `conecta_salud.sql`.

Archivo recomendado:

```text
src/main/resources/db/seed/conecta_salud_seed_manual_minimo_workbench.sql
```

Solo debe contener catalogos base:

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

## Roles Legacy

Estos `fileRole` siguen existiendo en codigo por compatibilidad, pero no son recomendados para el nuevo flujo:

- `population_indicators`
- `population_total`
- `percentage_over_60`
- `healthcare_access_deficiency`
- `total_poverty_population`

No documentarlos como flujo principal ni usarlos para nuevas cargas salvo que se este atendiendo compatibilidad legacy.

## Links Deployados

Backend Cloud Run:

- Pendiente de confirmar URL publica.

Configuracion de despliegue detectada en `cloudbuild.yaml`:

- Proyecto Google Cloud: `conecta-salud-494022`
- Region: `us-central1`
- Servicio Cloud Run: `backendn`
- Repositorio Artifact Registry: `backrepo`
- Imagen: `backendn`

Frontend:

- Pendiente de documentar.

## Usuarios de Prueba

No hay credenciales de prueba publicas versionadas en este repositorio.

La autenticacion usa Firebase Auth. Si se requieren usuarios de prueba, deben crearse en Firebase y documentarse fuera del repositorio o con placeholders sin contrasenas reales.

## Seguridad

- Mantener secretos en variables de entorno o gestores externos.
- No versionar credenciales JSON reales de Firebase.
- No versionar `.env`.
- No versionar archivos generados por build.
- No versionar CSVs cargados por pruebas.

Archivos que no deben versionarse:

- `.env`
- `target/`
- `uploads/`
- credenciales JSON de Firebase
- CSVs cargados por pruebas

Para compartir el backend, generar el ZIP excluyendo:

- `target/`
- `uploads/`
- `.env`

## Notas de Consistencia

La tabla central de indicadores es `territory_indicator_values`.

La disponibilidad se expone y persiste con `data_availability`.

Las tablas legacy `state_indicators` y `municipality_indicators` ya no forman parte del flujo actual. Si aparecen en documentacion antigua, esa documentacion debe actualizarse o archivarse. Si aparecen en codigo productivo, revisar antes de eliminar.
