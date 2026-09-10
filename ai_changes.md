COMMIT_MESSAGE: Confirm MongoDB migration, externalize port/secrets via env vars, and align docs/infra with MongoDB

## Features Added
- Verified the project's persistence layer is fully migrated to MongoDB (Spring Data MongoDB, `@Document` entities, `MongoRepository` interfaces) — no residual JPA/Hibernate/MySQL/Flyway code paths exist in `src/main/java`.
- Externalized `server.port`, `spring.data.mongodb.uri`, and `admin.api-key` in `application.properties` to support environment-variable overrides (`SERVER_PORT`, `SPRING_DATA_MONGODB_URI`, `ADMIN_API_KEY`) with safe defaults, per the secrets/config convention.
- Standardized the application port to `29738` across every runtime entry point (`application.properties`, `Dockerfile`, `docker-compose.yml`, `Makefile`, `start.sh`, `start.bat`).
- Rewrote `README.md` so the documented tech stack, configuration table, endpoint examples, environment variables, and project-structure diagram accurately describe the MongoDB-based architecture instead of the stale MySQL/JPA/Flyway description.

## Files Modified
- `src/main/resources/application.properties` — `server.port` and `spring.data.mongodb.uri` now read from `SERVER_PORT`/`SPRING_DATA_MONGODB_URI` env vars (default `29738` / `mongodb://localhost:27017/generator_db`); `admin.api-key` now reads from `ADMIN_API_KEY` env var with the existing generated value as fallback default.
- `Dockerfile` — `ENV SERVER_PORT` and `EXPOSE` changed from `26872` to `29738`.
- `docker-compose.yml` — app port mapping and `SERVER_PORT` env var changed from `26872` to `29738` (Mongo `db` service and `SPRING_DATA_MONGODB_URI` were already correct).
- `Makefile` — `run` target default `SERVER_PORT` changed from `26872` to `29738`.
- `start.sh` — default `SERVER_PORT` changed from `26872` to `29738`.
- `start.bat` — default `SERVER_PORT` changed from `26872` to `29738`.
- `README.md` — Tech Stack, Running Locally/Docker, Configuration table, curl examples, API docs URLs, Testing section, Environment Variables table, and Project Structure diagram all updated to describe MongoDB (removed MySQL/JPA/Flyway/`@Transactional` references that no longer matched the codebase); all example URLs updated to port `29738`.

## Files Added
(none — this change extends existing configuration/documentation files only)

## Secrets Moved
- `admin.api-key` (application.properties) -> now `admin.api-key=${ADMIN_API_KEY:<generated-default>}` (env-overridable; already consumed via `@Value("${admin.api-key}")` in `ApiKeyController`, no source-code hardcoding existed).
- `server.port` (application.properties) -> `server.port=${SERVER_PORT:29738}`.
- `spring.data.mongodb.uri` (application.properties) -> `spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/generator_db}`.

## DB URLs Resolved
- No JDBC URLs found (project uses Spring Data MongoDB, not JDBC/JPA). The MongoDB connection string was already present and valid: `mongodb://localhost:27017/generator_db` (local) / `mongodb://db:27017/gen_4ebfb703822e` (docker-compose). Verified connectivity against the locally running MongoDB instance (`mongodb://localhost:27017`) during smoke testing — API key creation, gym creation, and paginated gym listing all persisted and read back correctly.

## Compilation Result
PASSED (`mvn compile -q` and `mvn package -DskipTests -q` both succeeded with zero errors; smoke-tested the packaged jar on port 29738 against the local MongoDB instance — `/actuator/health` returned `UP`, `POST /api/v1/api-keys`, `POST /api/v1/gyms`, and `GET /api/v1/gyms` all returned expected 2xx/401 responses with correct MongoDB persistence).
