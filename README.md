# Restaurant Management V2

Spring Boot 4 REST API skeleton for restaurant management — built for FIAP.

## What this is

A structured project foundation with infrastructure fully configured: Docker, PostgreSQL, OpenAPI documentation, RFC 9457 error handling, and paginated endpoints. Business features are implemented incrementally on top of this base.

---

## Quick Start

> Get the app running in under 5 minutes.

### Prerequisites

- Docker + Docker Compose

### Steps

```bash
# 1. Clone and enter the project
git clone <repo-url>
cd restaurant-management-v2

# 2. Create your local environment file
cp .env.example .env

# 3. Fill in the required variables (see Configuration below)
# Minimum viable .env for local Docker:
#   SPRING_DATASOURCE_DATABASE=restaurant_db
#   SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/restaurant_db
#   SPRING_DATASOURCE_USERNAME=postgres
#   SPRING_DATASOURCE_PASSWORD=postgres
#   SPRING_DATASOURCE_PORT=5432
#   APP_PORT=8080

# 4. Start the full stack
docker compose up --build
```

App: `http://localhost:{APP_PORT}`

Swagger UI: `http://localhost:{APP_PORT}/swagger-ui.html`

---

## How-to Guides

### Run locally (without Docker app container)

```bash
# Start only the database
docker compose up db -d

# Run the app
./mvnw spring-boot:run
```

### Enable SQL logging

Set in `.env`:

```env
SPRING_PROFILES_ACTIVE=dev
```

Activates `application-dev.yaml`, which enables `show-sql`, `format_sql`, and `use_sql_comments`. **Never enable in production** — logs may expose sensitive query data.

### Run tests

```bash
./mvnw test

# Single test class
./mvnw test -Dtest=MyTestClass

# Full build + verify
./mvnw verify
```

### Build the JAR

```bash
./mvnw clean package
```

Output: `target/restaurant_management_v2-*.jar`

### Add a schema migration

When implementing persistence, add Flyway or Liquibase to `pom.xml` and create migration scripts under `src/main/resources/db/`. Hibernate is set to `ddl-auto: validate` — it will refuse to start if the schema does not match the entities.

---

## Reference

### Stack

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| PostgreSQL | 18 |
| Hibernate | 6 |
| Springdoc OpenAPI | 3.0.2 |
| spring-dotenv | 4.0.0 |

### Environment Variables

| Variable | Description | Required | Default |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | Full JDBC connection URL | Yes | — |
| `SPRING_DATASOURCE_USERNAME` | Database username | Yes | — |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Yes | — |
| `SPRING_DATASOURCE_DATABASE` | Database name (used to initialize the container) | Yes | — |
| `SPRING_DATASOURCE_PORT` | PostgreSQL host port | Yes | `5432` |
| `APP_PORT` | Application host port | Yes | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profile (`dev` enables SQL logging) | No | — |

> `SPRING_DATASOURCE_DATABASE` and `SPRING_DATASOURCE_URL` are intentionally separate. The URL supports full JDBC flexibility. Keep both consistent in `.env`.

### API Conventions

| Detail | Value |
|---|---|
| Pagination | Page size: 20 (default), 100 (max), 1-indexed |
| Error format | [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457) |
| OpenAPI spec | `GET /v3/api-docs` |
| Swagger UI | `GET /swagger-ui.html` |

### Docker

Multi-stage build on **Java 25**:

- **Build stage:** `maven:3.9-eclipse-temurin-25`
- **Runtime stage:** `eclipse-temurin:25-jre-jammy`
- **App healthcheck:** TCP check via `bash /dev/tcp/localhost/{APP_PORT}` (60s start period, 30s interval)
- **DB healthcheck:** `pg_isready` — app container only starts after DB passes

---

## Explanation

### Why `ddl-auto: validate`?

Hibernate validates the schema against entities on startup but never creates or modifies tables. All schema changes must go through explicit migration scripts (Flyway/Liquibase). This prevents accidental data loss and enforces reviewed, versioned schema evolution.

### Why environment variables for everything?

`spring-dotenv` loads `.env` automatically — no manual `export` needed. This keeps secrets out of `application.yaml` and out of version control, while still allowing Docker Compose and local dev to share the same configuration source.
