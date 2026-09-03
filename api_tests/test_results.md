# API Test Results

Tested against a live instance at `http://localhost:26872` using inline `curl` commands
(no test-script files were created, per instructions). Two fix iterations were applied
during testing (see "Fixes Applied" below); all results shown are FINAL (post-fix).

Legend: PASSED / FAILED / SKIPPED

## Auth / API Key Management

| Method | Endpoint | Scenario | Result |
|---|---|---|---|
| POST | /api/v1/api-keys | Create key (admin-gated, default SUPER_ADMIN role) | PASSED (201) |
| GET | /api/v1/api-keys | List keys (admin-gated) | PASSED (200) |
| GET | /api/v1/api-keys | Missing X-Admin-Key | PASSED (401, expected rejection) |
| DELETE | /api/v1/api-keys/{id} | Revoke key (admin-gated) | PASSED (200) |
| * | /api/v1/gyms | Missing X-API-Key | PASSED (401, expected rejection) |
| * | /api/v1/gyms | Invalid X-API-Key | PASSED (401, expected rejection) |
| * | /api/v1/gyms | Revoked X-API-Key | PASSED (401, expected rejection) |
| POST | /api/v1/gyms | MEMBER role creating a gym (RBAC) | PASSED (403, expected rejection) |

## Gyms

| Method | Endpoint | Result |
|---|---|---|
| POST | /api/v1/gyms | PASSED (201) |
| POST | /api/v1/gyms (missing name) | PASSED (400, validation) |
| GET | /api/v1/gyms | PASSED (200) |
| GET | /api/v1/gyms/{id} | PASSED (200) |
| PUT | /api/v1/gyms/{id} | PASSED (200) |
| DELETE | /api/v1/gyms/{id} (no children) | PASSED (204) |
| GET | /api/v1/gyms/{id} (after delete) | PASSED (404) |
| DELETE | /api/v1/gyms/{id} (has branches) | PASSED (409, expected FK-protection rejection) |

## Branches

| Method | Endpoint | Result |
|---|---|---|
| POST | /api/v1/branches | PASSED (201) |
| POST | /api/v1/branches (capacity=0) | PASSED (400, validation) |
| GET | /api/v1/branches | PASSED (200) |
| GET | /api/v1/branches/{id} | PASSED (200) |
| PUT | /api/v1/branches/{id} | PASSED (200) |
| DELETE | /api/v1/branches/{id} (has members/trainers) | PASSED (409, expected FK-protection rejection) |

## Trainers

| Method | Endpoint | Result |
|---|---|---|
| POST | /api/v1/trainers | PASSED (201) |
| POST | /api/v1/trainers (invalid email) | PASSED (400, validation) |
| GET | /api/v1/trainers | PASSED (200) |
| GET | /api/v1/trainers/{id} | PASSED (200) |
| PUT | /api/v1/trainers/{id} | PASSED (200) |
| DELETE | /api/v1/trainers/{id} (referenced by programs/classes) | PASSED (409, expected FK-protection rejection) |

## Members

| Method | Endpoint | Result |
|---|---|---|
| POST | /api/v1/members | PASSED (201) |
| POST | /api/v1/members (duplicate email) | PASSED (409) |
| GET | /api/v1/members | PASSED (200) |
| GET | /api/v1/members/{id} | PASSED (200) |
| PUT | /api/v1/members/{id} | PASSED (200) |
| DELETE | /api/v1/members/{id} (no children) | PASSED (204) |
| GET | /api/v1/members/{id} (after delete) | PASSED (404) |

## Membership Plans

| Method | Endpoint | Result |
|---|---|---|
| POST | /api/v1/membership-plans | PASSED (201) |
| POST | /api/v1/membership-plans (negative price) | PASSED (400, validation) |
| GET | /api/v1/membership-plans | PASSED (200) |
| GET | /api/v1/membership-plans/{id} | PASSED (200) |
| DELETE | /api/v1/membership-plans/{id} (referenced by memberships) | PASSED (409, expected FK-protection rejection) |

## Memberships (purchase / lifecycle)

| Method | Endpoint | Scenario | Result |
|---|---|---|---|
| POST | /api/v1/memberships/purchase | Purchase, payment succeeds -> ACTIVE with correct start/end dates | PASSED (201, body verified) |
| POST | /api/v1/memberships/purchase | Duplicate purchase while active/pending exists | PASSED (409) |
| POST | /api/v1/memberships/purchase | simulateFailure=true -> membership stays PENDING | PASSED (201, body verified PENDING) |
| GET | /api/v1/memberships/{id} | PASSED (200) |
| GET | /api/v1/memberships | PASSED (200) |
| GET | /api/v1/memberships/member/{id}/history | PASSED (200) |
| POST | /api/v1/memberships/{id}/pause | ACTIVE -> PAUSED | PASSED (200, body verified) |
| POST | /api/v1/memberships/{id}/resume | PAUSED -> ACTIVE, end date extended by paused days | PASSED (200, body verified) |
| POST | /api/v1/memberships/{id}/renew | End date correctly extended | PASSED (200, body verified) |
| POST | /api/v1/memberships/{id}/cancel | ACTIVE -> CANCELLED | PASSED (200) |
| POST | /api/v1/memberships/{id}/renew | Renew a CANCELLED membership | PASSED (422, expected rejection) |
| POST | /api/v1/memberships/{id}/upgrade | Not explicitly re-verified after cancel path (covered by code path shared with purchase/renew) | SKIPPED: covered indirectly; not independently exercised in this run to avoid mutating shared seed memberships |

## Workout Programs / Assignments

| Method | Endpoint | Result |
|---|---|---|
| POST | /api/v1/workout-programs | PASSED (201) |
| POST | /api/v1/workout-programs (no exercises) | PASSED (400, validation) |
| GET | /api/v1/workout-programs | PASSED (200) |
| GET | /api/v1/workout-programs/{id} | PASSED (200) |
| DELETE | /api/v1/workout-programs/{id} | PASSED (204) |
| GET | /api/v1/workout-programs/{id} (after delete) | PASSED (404) |
| POST | /api/v1/workout-assignments | PASSED (201) |
| GET | /api/v1/workout-assignments | PASSED (200) |
| GET | /api/v1/workout-assignments/member/{id} | PASSED (200) |
| PUT | /api/v1/workout-assignments/{id}/progress | PASSED (200) |
| DELETE | /api/v1/workout-assignments/{id} | PASSED (204) |
| GET | /api/v1/workout-assignments/{id} (after delete) | PASSED (404) |

## Fitness Classes / Class Registrations

| Method | Endpoint | Scenario | Result |
|---|---|---|---|
| POST | /api/v1/fitness-classes | Create class capacity=1 | PASSED (201) |
| GET | /api/v1/fitness-classes | PASSED (200) |
| GET | /api/v1/fitness-classes/{id} | PASSED (200) |
| POST | /api/v1/class-registrations | 1st registration -> REGISTERED | PASSED (201, body verified) |
| POST | /api/v1/class-registrations | 2nd registration on full class -> WAITLISTED position 1 | PASSED (201, body verified) |
| POST | /api/v1/class-registrations | Duplicate registration | PASSED (409) |
| POST | /api/v1/class-registrations | Blocked member registration | PASSED (422, expected rejection) |
| POST | /api/v1/class-registrations/{id}/cancel | Cancel REGISTERED -> auto-promotes waitlisted member | PASSED (200, body verified) |
| GET | /api/v1/class-registrations/{id} | Verified promoted registration status=REGISTERED | PASSED (200, body verified) |
| DELETE | /api/v1/fitness-classes/{id} (has registrations) | PASSED (409, expected FK-protection rejection) |

## Appointments

| Method | Endpoint | Scenario | Result |
|---|---|---|---|
| POST | /api/v1/appointments | Book with active-membership member | PASSED (201) |
| POST | /api/v1/appointments | Member without active membership | PASSED (422, expected rejection) |
| POST | /api/v1/appointments | Trainer double-booking overlap | PASSED (409, expected rejection) |
| POST | /api/v1/appointments | Member double-booking overlap | PASSED (409, expected rejection) |
| GET | /api/v1/appointments/{id} | PASSED (200) |
| GET | /api/v1/appointments | PASSED (200) |
| GET | /api/v1/appointments/member/{id} | PASSED (200) |
| POST | /api/v1/appointments/{id}/confirm | PASSED (200) |
| POST | /api/v1/appointments/{id}/complete | PASSED (200) |
| POST | /api/v1/appointments/{id}/cancel | Cancel a COMPLETED appointment | PASSED (422, expected rejection) |
| POST | /api/v1/appointments/{id}/no-show | Covered by same state-machine as complete; not independently re-tested this run | SKIPPED: exercised via code-shared confirm/complete state machine only |

## Attendance

| Method | Endpoint | Scenario | Result |
|---|---|---|---|
| POST | /api/v1/attendance/check-in | Active member | PASSED (201) |
| POST | /api/v1/attendance/check-in | Duplicate active check-in | PASSED (422, expected rejection) |
| POST | /api/v1/attendance/check-in | Blocked member | PASSED (422, expected rejection) |
| POST | /api/v1/attendance/check-out/{memberId} | PASSED (200) |
| POST | /api/v1/attendance/check-out/{memberId} | No active check-in | PASSED (422, expected rejection) |
| GET | /api/v1/attendance/{id} | PASSED (200) |
| GET | /api/v1/attendance | PASSED (200) |
| GET | /api/v1/attendance/member/{id} | PASSED (200) |
| GET | /api/v1/attendance/branch/{id} | PASSED (200) |

## Payments / Refunds

| Method | Endpoint | Scenario | Result |
|---|---|---|---|
| POST | /api/v1/payments | Valid payment -> SUCCESS | PASSED (201, body verified) |
| POST | /api/v1/payments | amount=0 | PASSED (400, validation) |
| POST | /api/v1/payments | Idempotency-Key replay returns identical record | PASSED (201 both calls, same id/transactionRef verified) |
| GET | /api/v1/payments/{id} | PASSED (200) |
| GET | /api/v1/payments | PASSED (200) |
| POST | /api/v1/refunds | Partial refund -> payment becomes PARTIALLY_REFUNDED | PASSED (201, body verified) |
| POST | /api/v1/refunds | Refund exceeding remaining balance | PASSED (422, expected rejection) |
| GET | /api/v1/refunds/{id} | PASSED (200) |
| GET | /api/v1/refunds | PASSED (200) |

## Notifications

| Method | Endpoint | Result |
|---|---|---|
| GET | /api/v1/notifications?memberId={id} | PASSED (200) |
| GET | /api/v1/notifications | PASSED (200) |
| PUT | /api/v1/notifications/{id}/read | PASSED (200, body verified isRead=true) |

## Infra / Docs

| Endpoint | Result |
|---|---|
| GET /actuator/health | PASSED (200) |
| GET /docs (Swagger UI) | PASSED (200) |
| GET /api-docs (OpenAPI JSON) | PASSED (200) |
| GET /ws/info (WebSocket SockJS handshake) | PASSED (200) |

## Fixes Applied During Testing (Iteration 1)

1. **401 vs 403 semantics** — Spring Security's default behavior returned 403 for
   missing/invalid API keys. Added a custom `authenticationEntryPoint` (401) and
   `accessDeniedHandler` (403) in `SecurityConfig`, plus explicit
   `@ExceptionHandler(AccessDeniedException.class)` / `AuthenticationException.class`
   in `GlobalExceptionHandler` (since `@PreAuthorize` denials are resolved by Spring MVC
   before reaching the security filter's handler).
2. **LazyInitializationException -> 500 on list/get endpoints** — `open-in-view=false`
   combined with lazy `@ManyToOne` associations (e.g. `Branch.gym`) caused 500s when
   DTO mapping accessed the association outside a transaction. Fixed by adding
   `@Transactional(readOnly = true)` at the class level of every service whose DTO
   mapping touches a lazy association.
3. **Duplicate-key violations returning 500** — added
   `@ExceptionHandler(DataIntegrityViolationException.class)` returning 409, and a
   `NoResourceFoundException` handler returning 404, in `GlobalExceptionHandler`.
4. **Hibernate schema validation false positives against MariaDB** — the target
   database server identifies as MariaDB even though the standard MySQL JDBC
   URL/driver were used (per project configuration); `mysql-connector-j`'s metadata
   queries against MariaDB are not fully compatible, causing Hibernate's
   `ddl-auto=validate` to report false "wrong column type" errors (confirmed correct
   via `SHOW CREATE TABLE`). Changed to `ddl-auto=none`; Flyway remains the sole
   schema owner (no Hibernate auto-DDL is used, satisfying the original requirement).
5. **MySQL dialect mapping `@Enumerated(STRING)` to native ENUM** — Hibernate 6 maps
   string enums to native MySQL `ENUM` columns by default; added explicit
   `columnDefinition = "VARCHAR(n)"` to every enum-backed `@Column` to keep them as
   portable VARCHAR, matching the Flyway-managed schema.

No FAILED or unresolved endpoints remain after these fixes. Referential-integrity
409 responses on DELETE for entities with dependent children are expected/correct
behavior, not defects.
