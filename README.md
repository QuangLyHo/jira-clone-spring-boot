# Jira Clone (Spring Boot)

A backend ticketing system built while learning Java, Spring Boot, MySQL, DBeaver, and Docker — modeled after Jira's core concepts (tickets/tasks, assignees, status workflow).

## Stack
- Java 25
- Spring Boot 4.1 (Web, Data JPA, Actuator)
- MySQL (via Docker)
- DBeaver for schema design/inspection

## Current state
- `User` and `Task` entities with a many-to-many relationship (`task_assignees` join table)
- `TaskStatus` enum (todo / in_progress / done)
- Repository layer with a custom JPQL query (`JOIN FETCH`) to avoid N+1 when loading task assignees
- Schema is hand-designed in MySQL; Hibernate runs with `ddl-auto=validate` so entities are checked against the schema rather than generating it

## Running locally
1. Start a MySQL container and create the `data_normalization` database/schema.
2. Set `DB_USERNAME` and `DB_PASSWORD` environment variables.
3. `./mvnw spring-boot:run`

## Roadmap
- REST controllers + service layer for tickets
- Projects/boards, priorities, labels, comments
- Status transition rules
- Docker Compose for app + MySQL
- Versioned schema migrations (Flyway)
