# Jira Clone (Spring Boot)

A backend ticketing system built while learning Java, Spring Boot, MySQL, DBeaver, and Docker — modeled after Jira's core concepts (tickets/tasks, assignees, status workflow, projects, auth, and access control).

## Stack
- Java 25
- Spring Boot 4.1 (Web, Data JPA, Validation, Actuator)
- Spring Security 7 + JWT (`jjwt`) for stateless authentication/authorization
- MySQL (via Docker)
- Docker + Docker Compose (app and MySQL both run containerized)
- Flyway for versioned schema migrations
- Testcontainers for integration tests (spins up a real, disposable MySQL per test run)
- springdoc-openapi for interactive API docs (Swagger UI)
- GitHub Actions for CI
- DBeaver for schema inspection

## Current state

**Domain model**
- `User`, `Task`, `Project` entities
- `Task` ↔ `User` many-to-many (`task_assignees` join table)
- `Task` → `Project` many-to-one
- `TaskStatus` enum (todo / in_progress / done), `Role` enum (USER / ADMIN)

**API**
- Full CRUD for `/api/tasks`, `/api/users`, `/api/projects`, plus `GET /api/projects/{id}/tasks`
- Pagination and sorting on all list endpoints, plus status filtering on `GET /api/tasks`
- Request/response DTOs at the API boundary
- Bean Validation on all inputs, with a global exception handler producing consistent JSON error responses
- Interactive docs at `/swagger-ui/index.html`

**Auth & access control**
- `POST /api/auth/register` / `POST /api/auth/login` — passwords hashed with BCrypt, login issues a signed JWT
- Every other endpoint requires a valid `Authorization: Bearer <token>` header
- Authorization rules:
  - Projects: only `ADMIN` can create/update/delete; any authenticated user can read
  - Tasks: only a task's assignees (or an `ADMIN`) can update/delete it; any authenticated user can create/read
- Role changes take effect immediately

**Data & testing**
- Schema is owned by Flyway migrations (`src/main/resources/db/migration`)
- Hibernate runs with `ddl-auto=validate` — it checks entities against the Flyway-built schema rather than generating it
- Full test suite: unit tests (service layer), `@WebMvcTest` controller-slice tests, and a real integration test backed by Testcontainers — all self-contained, no manual DB setup required
- CI runs the full suite on every push/PR via GitHub Actions

## Running locally

**With Docker Compose (recommended)**

1. Create a `.env` file in the project root with `MYSQL_ROOT_PASSWORD`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` (generate the last one with `openssl rand -base64 32`).
2. `docker compose up --build`

This builds the app image, starts MySQL, creates a scoped `ticketing_app` DB user via `db-init/init.sql`, and runs Flyway migrations automatically. The API is available at `localhost:8080`.

**Running the app directly (MySQL still via Docker)**

1. Start a MySQL container (e.g. `docker run` or your existing one) — Flyway will build the schema automatically on first run.
2. Set three environment variables: `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (generate the latter with `openssl rand -base64 32`).
3. `./mvnw spring-boot:run`

## Running tests

```
./mvnw test
```
No local MySQL or env vars required — the integration test spins up its own MySQL via Testcontainers, and Flyway builds its schema from the same migrations used in step 1 above.

## Roadmap
- Deploy to a live host
- Frontend (last, once there's something live to point it at)
