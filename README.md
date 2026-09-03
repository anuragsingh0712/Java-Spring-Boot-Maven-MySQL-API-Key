# Gym Management and Fitness Services Backend

A production-grade, single Spring Boot application for managing gyms, branches, members,
trainers, memberships, workouts, fitness classes, personal training appointments,
attendance, payments/billing, and notifications.

## Tech Stack

- Java 21, Spring Boot 3.2.5, Maven
- Spring Web, Spring Data JPA (Hibernate), Spring Security
- MySQL 8 + Flyway (schema-as-migrations, no Hibernate auto-DDL)
- Jakarta Bean Validation
- springdoc-openapi (Swagger UI)
- Spring WebSocket (STOMP) for live notification broadcast
- API Key authentication (`X-API-Key` header) with role-based authorization

Architecture: `Controller -> Service -> Repository -> MySQL`, single deployable JAR.
No microservices, no message brokers, no distributed components.

## Running locally

Prerequisites: JDK 21, Maven, a reachable MySQL instance matching `application.properties`.

```bash
chmod +x start.sh
SERVER_PORT=26872 ./start.sh
```

This builds the jar (`mvn package -DskipTests`) and starts it on port `26872`
(override with `SERVER_PORT`). Flyway automatically creates the schema and seeds
demo data on first boot.

On Windows: `start.bat`.

## Running with Docker

```bash
docker compose up --build
```

This starts the app (port 26872) and a MySQL 8 container together.

## Configuration

Key properties in `src/main/resources/application.properties`:

| Property | Purpose |
|---|---|
| `server.port` | `26872` |
| `spring.datasource.url/username/password` | MySQL connection |
| `spring.jpa.hibernate.ddl-auto` | `validate` (Flyway owns the schema) |
| `spring.flyway.locations` | `classpath:db/migration` |
| `springdoc.swagger-ui.path` | `/docs` |
| `springdoc.api-docs.path` | `/api-docs` |
| `admin.api-key` | Bootstrap secret required to mint/list/revoke API keys |

## Authentication

All business endpoints require a valid API key:

```
X-API-Key: <key>
```

API keys carry a **role** (`SUPER_ADMIN`, `GYM_ADMIN`, `BRANCH_MANAGER`, `TRAINER`,
`RECEPTIONIST`, `MEMBER`) and a **status** (`ACTIVE`, `EXPIRED`, `REVOKED`). Missing or
invalid keys return `401`; keys without sufficient role for an operation return `403`.

### Creating the first API key (admin-gated)

Key management (`/api/v1/api-keys/**`) is gated by a separate `X-Admin-Key` header
(never by `X-API-Key`), checked against the `admin.api-key` value generated into
`application.properties`.

```bash
ADMIN_KEY=$(grep '^admin.api-key=' src/main/resources/application.properties | cut -d'=' -f2)

curl -X POST http://localhost:26872/api/v1/api-keys \
  -H "Content-Type: application/json" \
  -H "X-Admin-Key: $ADMIN_KEY" \
  -d '{"name":"demo-super-admin"}'
```

The response's `apiKey` field is shown **once** — store it securely. Use it as
`X-API-Key` on every other endpoint.

## API Documentation

- Swagger UI: `http://localhost:26872/docs`
- OpenAPI JSON: `http://localhost:26872/api-docs`
- Health check: `http://localhost:26872/actuator/health`
- WebSocket (STOMP over SockJS) endpoint for live notifications: `ws://localhost:26872/ws`,
  topic `/topic/notifications`

## Endpoints (prefix `/api/v1`, offset pagination via `page`/`size`, default size 20)

| Resource | Endpoints |
|---|---|
| API Keys | `POST/GET /api-keys`, `DELETE /api-keys/{id}` (admin-gated) |
| Gyms | `POST/GET /gyms`, `GET/PUT/DELETE /gyms/{id}` |
| Branches | `POST/GET /branches`, `GET/PUT/DELETE /branches/{id}` |
| Members | `POST/GET /members`, `GET/PUT/DELETE /members/{id}` |
| Trainers | `POST/GET /trainers`, `GET/PUT/DELETE /trainers/{id}` |
| Membership Plans | `POST/GET /membership-plans`, `GET/PUT/DELETE /membership-plans/{id}` |
| Memberships | `POST /memberships/purchase`, `GET /memberships`, `GET /memberships/{id}`, `GET /memberships/member/{memberId}/history`, `POST /memberships/{id}/renew|activate|pause|resume|cancel|upgrade` |
| Workout Programs | `POST/GET /workout-programs`, `GET/PUT/DELETE /workout-programs/{id}` |
| Workout Assignments | `POST/GET /workout-assignments`, `GET /workout-assignments/{id}`, `GET /workout-assignments/member/{memberId}`, `PUT /workout-assignments/{id}/progress`, `DELETE /workout-assignments/{id}` |
| Fitness Classes | `POST/GET /fitness-classes`, `GET /fitness-classes/{id}`, `POST /fitness-classes/{id}/cancel`, `DELETE /fitness-classes/{id}` |
| Class Registrations | `POST/GET /class-registrations`, `GET /class-registrations/{id}`, `GET /class-registrations/member/{memberId}`, `POST /class-registrations/{id}/cancel` |
| Appointments | `POST/GET /appointments`, `GET /appointments/{id}`, `GET /appointments/member/{memberId}`, `POST /appointments/{id}/confirm|cancel|complete|no-show` |
| Attendance | `POST /attendance/check-in`, `POST /attendance/check-out/{memberId}`, `GET /attendance`, `GET /attendance/{id}`, `GET /attendance/member/{memberId}`, `GET /attendance/branch/{branchId}` |
| Payments | `POST/GET /payments`, `GET /payments/{id}` |
| Refunds | `POST/GET /refunds`, `GET /refunds/{id}` |
| Notifications | `GET /notifications`, `PUT /notifications/{id}/read` |

Idempotency: `Idempotency-Key` header (or body field) supported on `POST /payments`,
`POST /refunds`, `POST /memberships/purchase`, `POST /memberships/{id}/renew`,
`POST /class-registrations`, and `POST /appointments`.

## Business Rules Enforced

- Blocked/suspended/expired/inactive members cannot check in, register for classes,
  book appointments, or purchase services requiring an active membership.
- Closed/under-maintenance branches reject new class scheduling.
- Only active trainers can be assigned to programs, classes, or appointments.
- Trainer/member double-booking is prevented via overlap checks on appointments.
- Fitness classes never exceed capacity; overflow is waitlisted and automatically
  promoted (FIFO) when a confirmed registration is cancelled.
- Membership purchase/renewal: membership starts `PENDING`, payment is processed,
  and only a `SUCCESS` payment activates/extends the membership — failures leave
  the membership `PENDING` with no partial/inconsistent state (`@Transactional`).
- Refunds validate the payment is `SUCCESS`/`PARTIALLY_REFUNDED` and the amount does
  not exceed the remaining refundable balance.
- Payment/refund/registration/appointment idempotency keys short-circuit duplicate
  submissions and return the original record.

## Testing

Automated endpoint verification was performed with `curl` against a running instance
(see `/api_tests/test_results.md` and `/api_test_report.xlsx` for the full pass/fail
matrix). The project also ships with `spring-boot-starter-test`, `spring-security-test`,
and Testcontainers (MySQL) dependencies pre-wired in `pom.xml` for teams that want to
add JUnit 5/Mockito/Testcontainers integration tests going forward
(`mvn test`, requires a Docker daemon for Testcontainers).

## Environment Variables

| Variable | Default | Purpose |
|---|---|---|
| `SERVER_PORT` | `26872` | Overrides `server.port` at launch (start.sh/start.bat/Docker) |

## Project Structure

```
src/main/java/com/example/app/
├── AppApplication.java
├── config/        SecurityConfig, CorsConfig, WebSocketConfig
├── security/       ApiKey, ApiKeyRepository, ApiKeyFilter, ApiKeyService, ApiRole, ApiKeyStatus
├── entity/          JPA entities + enums
├── repository/       Spring Data JPA repositories
├── service/            Business logic (@Transactional where required)
├── controller/          REST controllers (thin, @Tag/@Operation annotated)
├── dto/                  Request/response DTOs per module
└── exception/              GlobalExceptionHandler + domain exceptions
src/main/resources/
├── application.properties
└── db/migration/            Flyway V1 (schema), V2 (seed data)
```
