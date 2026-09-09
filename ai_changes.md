COMMIT_MESSAGE: Migrate persistence layer from MongoDB to PostgreSQL (JPA/Hibernate)

## Features Added
- Replaced the MongoDB document store with a relational PostgreSQL database across the
  entire persistence layer.
- Converted all 16 `@Document` Mongo entities to JPA `@Entity` classes (`@Id` with
  `@GeneratedValue(strategy = GenerationType.UUID)` to preserve String ids, `@Enumerated
  (EnumType.STRING)` on every enum field, `@Column(unique = true)` for previously
  `@Indexed(unique = true)` fields, `@Table(indexes = ...)` for non-unique indexes).
- Converted `WorkoutProgram.exercises` (previously an embedded Mongo document list) to a
  JPA `@ElementCollection` of an `@Embeddable` `Exercise` value type, stored in a new
  `workout_program_exercises` table — preserving the same "list of value objects" shape.
- Converted the compound unique index on `ClassRegistration` (fitnessClassId + memberId)
  to a JPA `@Table(uniqueConstraints = @UniqueConstraint(...))`.
- Converted all 16 `MongoRepository<T, String>` repository interfaces to
  `JpaRepository<T, String>`. All existing derived-query methods (`findByX`, `countByX`,
  `Page<T> findByX(..., Pageable)`) work unchanged since both Mongo and JPA repositories
  share the same Spring Data query-derivation and `Pageable`/`Page` abstractions.
- Rewrote `AppointmentRepository`'s two native Mongo JSON `@Query` methods
  (`findTrainerOverlaps`, `findMemberOverlaps`) as equivalent JPQL `@Query` statements
  using the same positional parameters.
- Switched auditing from `@EnableMongoAuditing`/Mongo `@Id` annotations to
  `@EnableJpaAuditing` + `@MappedSuperclass` + `@EntityListeners(AuditingEntityListener
  .class)` on `BaseAuditEntity` (createdAt/updatedAt still use the same
  `@CreatedDate`/`@LastModifiedDate` Spring Data annotations, which work identically for
  JPA).
- Offset-based pagination (`limit=20` default, via `Pageable`/`PageResponse`) required no
  code changes: `JpaRepository` extends `PagingAndSortingRepository` exactly like
  `MongoRepository` did, so every existing controller/service `Pageable` parameter and
  `PageResponse.of(...)` wrapper continue to work unchanged against Postgres.
- Updated `pom.xml`: removed `spring-boot-starter-data-mongodb` and the
  `testcontainers:mongodb` test dependency; added `spring-boot-starter-data-jpa`, the
  `postgresql` JDBC driver (runtime scope), and swapped the test-only
  `testcontainers:mongodb` dependency for `testcontainers:postgresql`.
- Updated `application.properties`: replaced `spring.data.mongodb.*` settings with
  `spring.datasource.url/username/password` (externalized via env vars with safe
  fallbacks) and `spring.jpa.*` (Hibernate `ddl-auto=update`, PostgreSQL dialect,
  `open-in-view=false`). Removed the Mongo-standalone-transaction caveat comment (no
  longer applicable — PostgreSQL supports full ACID `@Transactional` semantics).
- Updated Docker/Compose/Makefile/start scripts and README to reference a PostgreSQL 16
  container/JDBC URL instead of MongoDB 7, and to the required server port `29586`
  (was `26872`).
- Existing infra features already present and left untouched (out of scope for this
  request): API key authentication, CORS configuration, WebSocket/STOMP notification
  broadcast, `/api/v1` prefix, offset pagination defaults.

## Files Modified
- pom.xml — swapped MongoDB starter/testcontainers for JPA + PostgreSQL driver/testcontainers
- src/main/java/com/example/app/AppApplication.java — `@EnableMongoAuditing` -> `@EnableJpaAuditing`
- src/main/java/com/example/app/entity/BaseAuditEntity.java — `@MappedSuperclass` + `AuditingEntityListener`
- src/main/java/com/example/app/entity/Appointment.java — JPA entity mapping
- src/main/java/com/example/app/entity/Attendance.java — JPA entity mapping
- src/main/java/com/example/app/entity/Branch.java — JPA entity mapping + `@Table` index
- src/main/java/com/example/app/entity/ClassRegistration.java — JPA entity + composite unique constraint
- src/main/java/com/example/app/entity/Exercise.java — `@Embeddable`
- src/main/java/com/example/app/entity/FitnessClass.java — JPA entity mapping
- src/main/java/com/example/app/entity/Gym.java — JPA entity mapping
- src/main/java/com/example/app/entity/Member.java — JPA entity mapping
- src/main/java/com/example/app/entity/Membership.java — JPA entity mapping
- src/main/java/com/example/app/entity/MembershipPlan.java — JPA entity mapping
- src/main/java/com/example/app/entity/Notification.java — JPA entity mapping
- src/main/java/com/example/app/entity/Payment.java — JPA entity mapping
- src/main/java/com/example/app/entity/Refund.java — JPA entity mapping
- src/main/java/com/example/app/entity/Trainer.java — JPA entity mapping
- src/main/java/com/example/app/entity/WorkoutAssignment.java — JPA entity mapping
- src/main/java/com/example/app/entity/WorkoutProgram.java — JPA entity + `@ElementCollection`
- src/main/java/com/example/app/security/ApiKey.java — JPA entity mapping
- src/main/java/com/example/app/repository/*.java (all 15 files) — `MongoRepository` -> `JpaRepository`
- src/main/java/com/example/app/repository/AppointmentRepository.java — Mongo `@Query` JSON -> JPQL `@Query`
- src/main/java/com/example/app/security/ApiKeyRepository.java — `MongoRepository` -> `JpaRepository`
- src/main/resources/application.properties — PostgreSQL datasource/JPA config, port 29586
- docker-compose.yml — Postgres 16 service instead of Mongo 7, port 29586
- Dockerfile — port 29586
- Makefile — port 29586
- start.sh / start.bat — port 29586
- README.md — PostgreSQL/JPA references, port 29586, env var table

## Files Added
(none — this was a persistence-layer conversion of existing files, not new features)

## Secrets Moved
(none newly discovered — `admin.api-key` was already externalized via
`application.properties`; no hardcoded credentials existed in Java source)

## DB URLs Resolved
- mongodb://localhost:27017/generator_db (dev) -> jdbc:postgresql://localhost:5432/gen_8c72c02afb70
  (username: myuser, password: mypassword — externalized as `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`
  env vars with these values as fallback defaults in application.properties)
- mongodb://db:27017/gen_4ebfb703822e (docker-compose) -> jdbc:postgresql://db:5432/gen_8c72c02afb70

## Compilation Result (PASSED / FAILED)
PASSED — `mvn compile -q` and `mvn package -DskipTests -q` both succeed with zero errors.
Manually verified end-to-end against the live PostgreSQL database: server boots, connects
via HikariCP, Hibernate auto-creates the schema, and the following flows were smoke
tested successfully: API key creation (admin-gated), Gym create/list with offset
pagination envelope, Branch/Member/Trainer create, WorkoutProgram create with embedded
Exercise `@ElementCollection`, MembershipPlan create + membership purchase, Appointment
create followed by a correctly-rejected 409 conflict on an overlapping appointment
(exercising the JPQL-rewritten overlap-detection query).
