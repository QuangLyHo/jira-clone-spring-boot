# Team Backlog

A full-stack project/task tracking app (Spring Boot backend + React frontend) built while learning Java, Spring Boot, MySQL, DBeaver, and Docker — supports projects, a per-project backlog of tasks, assignees, status workflow, JWT auth, and role-based access control.

## Live

- App: https://team-backlog.vercel.app
- API docs (Swagger UI): https://jira-clone-spring-boot.onrender.com/swagger-ui/index.html

The backend runs on Render's free tier, which spins down after ~15 minutes of inactivity. The first request after that can take up to a minute to wake back up.

## Stack

**Backend**
- Java 25
- Spring Boot 4.1 (Web, Data JPA, Validation, Actuator)
- Spring Security 7 + JWT (`jjwt`) for stateless authentication/authorization
- MySQL (Aiven in production, Docker locally)
- Docker + Docker Compose (app and MySQL both run containerized)
- Flyway for versioned schema migrations
- Testcontainers for integration tests (spins up a real, disposable MySQL per test run)
- springdoc-openapi for interactive API docs (Swagger UI)
- GitHub Actions for CI
- DBeaver for schema inspection

**Frontend**
- React + Vite
- Plain CSS design system (no UI framework)

**Deployment**
- Backend: Render (Docker-based deploy)
- Database: Aiven (managed MySQL)
- Frontend: Vercel

## Current state

**Domain model**
- `User`, `Task`, `Project` entities
- `Task` ↔ `User` many-to-many (`task_assignees` join table)
- `Task` → `Project` many-to-one
- `TaskStatus` enum (todo / in_progress / done), `Role` enum (USER / ADMIN)

**API**
- Full CRUD for `/api/tasks`, `/api/users`, `/api/projects`, plus `GET /api/projects/{id}/tasks`
- `PATCH /api/tasks/{id}/status` for status-only updates, separate from the full `PUT` update
- Pagination and sorting on all list endpoints, plus status filtering on `GET /api/tasks`
- Request/response DTOs at the API boundary
- Bean Validation on all inputs, with a global exception handler producing consistent JSON error responses
- Interactive docs at `/swagger-ui/index.html`

**Auth & access control**
- `POST /api/auth/register` / `POST /api/auth/login` — passwords hashed with BCrypt, login issues a signed JWT
- Every other endpoint requires a valid `Authorization: Bearer <token>` header
- A custom `AuthenticationEntryPoint` returns 401 for missing/invalid/expired tokens, keeping 403 reserved for "authenticated but not permitted" — the frontend uses this distinction to redirect to login only on true auth failures
- Authorization rules:
  - Projects: only `ADMIN` can create/update/delete; any authenticated user can read
  - Tasks: only a task's assignees (or an `ADMIN`) can update/delete it; any authenticated user can create/read
- Role changes take effect immediately

**Frontend**
- Login/signup with JWT persisted in `localStorage`
- Project list with creation (admin-gated)
- Per-project backlog view: create tasks with an assignee picker, view tasks, change status via a status dropdown
- Automatic logout/redirect on a 401 from any API call

**Data & testing**
- Schema is owned by Flyway migrations (`src/main/resources/db/migration`)
- Hibernate runs with `ddl-auto=validate` — it checks entities against the Flyway-built schema rather than generating it
- Full test suite: unit tests (service layer), `@WebMvcTest` controller-slice tests, and a real integration test backed by Testcontainers — all self-contained, no manual DB setup required
- CI runs the full suite on every push/PR via GitHub Actions

## Running locally

**Backend, with Docker Compose (recommended)**

1. Create a `.env` file in the project root with `MYSQL_ROOT_PASSWORD`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` (generate the last one with `openssl rand -base64 32`).
2. `docker compose up --build`

This builds the app image, starts MySQL, creates a scoped `ticketing_app` DB user via `db-init/init.sql`, and runs Flyway migrations automatically. The API is available at `localhost:8080`.

**Backend, running directly (MySQL still via Docker)**

1. Start a MySQL container (e.g. `docker run` or your existing one) — Flyway will build the schema automatically on first run.
2. Set three environment variables: `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (generate the latter with `openssl rand -base64 32`).
3. `./mvnw spring-boot:run`

**Frontend**

1. `cd frontend`
2. Create a `.env` file with `VITE_API_URL` pointing at your backend (e.g. `http://localhost:8080`).
3. `npm install`
4. `npm run dev`

## Running tests

```
./mvnw test
```
No local MySQL or env vars required — the integration test spins up its own MySQL via Testcontainers, and Flyway builds its schema from the same migrations used in step 1 above.

## Roadmap

- Team/workspace scoping — currently every authenticated user can see every project; adding team membership so projects are scoped to a team is the next planned feature
