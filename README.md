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
# The example ships with working defaults for local Docker —
# adjust ports/credentials only if needed (see Configuration below)
cp .env.example .env

# 3. Start the full stack
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

### Coverage & static analysis

Unit coverage is enforced by a JaCoCo gate: `./mvnw verify` **fails** if unit line coverage drops below 80%.

Static analysis runs on SonarQube via the Maven plugin. With a SonarQube server up, run:

```bash
./mvnw clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<token>
```

The token is passed on the command line (or `SONAR_TOKEN`) — never commit it.

### Build the JAR

```bash
./mvnw clean package
```

Output: `target/restaurant_management_v2-*.jar`

### Add a schema migration

Liquibase is already configured. Add a changeset XML under `src/main/resources/db/changelog/changes/` and include it from `db.changelog-master.xml`. Migrations run automatically on app startup. Hibernate is set to `ddl-auto: validate` — it will refuse to start if the schema does not match the entities.

### Generate a migration diff (`mvn liquibase:diff`)

The Liquibase Maven plugin reads database credentials from `src/main/resources/liquibase.properties`, which is **gitignored** (it holds local credentials). Before running a diff, copy the template and fill it in:

```bash
cp src/main/resources/liquibase.properties.example src/main/resources/liquibase.properties
# edit url / username / password to match your local database

./mvnw liquibase:diff
```

`referenceUrl` points at the Hibernate entities, so the diff compares your live database against the current `@Entity` model.

---

## Reference

### Stack

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| PostgreSQL | 18 |
| Hibernate | 7 |
| Springdoc OpenAPI | 3.0.2 |

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
| `JWT_SECRET` | Base64-encoded HS256 signing key (decodes to ≥32 bytes). Generate: `openssl rand -base64 32` | Yes | — (app fails to start if unset) |
| `JWT_ACCESS_TOKEN_EXPIRATION_TIME` | Access token lifetime, in minutes | No | `15` |

> `SPRING_DATASOURCE_DATABASE` and `SPRING_DATASOURCE_URL` are intentionally separate. The URL supports full JDBC flexibility. Keep both consistent in `.env`.

### API Conventions

| Detail | Value |
|---|---|
| Pagination | Page size: 20 (default), 100 (max), 1-indexed |
| Error format | [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457) |
| OpenAPI spec | `GET /v3/api-docs` |
| Swagger UI | `GET /swagger-ui.html` |
| Authentication | JWT Bearer (HS256). Get a token via `POST /api/v1/auth/login`, then send `Authorization: Bearer <token>`. See [docs/authentication.md](docs/authentication.md) |

### API Endpoints

Base path: `/api/v1`. Full request/response schemas are available in the Swagger UI and in the [Postman collection](postman/Restaurant%20Management%20API%20-%20V2%20-%202.postman_collection.json).

#### Users — `/api/v1/users`

| Method | Path | Description |
|---|---|---|
| `POST` | `/users` | Create a user |
| `GET` | `/users` | List users (paginated) |
| `GET` | `/users/{id}` | Get a user by ID |
| `PATCH` | `/users/{id}` | Update a user |
| `DELETE` | `/users/{id}` | Delete a user |

#### User Types — `/api/v1/users-type`

| Method | Path | Description |
|---|---|---|
| `POST` | `/users-type` | Create a user type |
| `GET` | `/users-type` | List user types (paginated) |
| `GET` | `/users-type/{id}` | Get a user type by ID |
| `PUT` | `/users-type/{id}` | Update a user type |
| `POST` | `/users-type/bind` | Bind a user to a user type |
| `DELETE` | `/users-type/{id}` | Delete a user type |

#### Restaurants — `/api/v1/restaurants`

| Method | Path | Description |
|---|---|---|
| `POST` | `/restaurants` | Create a restaurant (owner must be an existing user) |
| `GET` | `/restaurants` | List restaurants (paginated) |
| `GET` | `/restaurants/{id}` | Get a restaurant by ID |
| `PUT` | `/restaurants/{id}` | Update a restaurant |
| `DELETE` | `/restaurants/{id}` | Delete a restaurant |

#### Menu Items — `/api/v1/menu-items`

| Method | Path | Description |
|---|---|---|
| `POST` | `/menu-items` | Create a menu item |
| `GET` | `/menu-items` | List menu items (paginated) |
| `GET` | `/menu-items/{id}` | Get a menu item by ID |
| `GET` | `/menu-items/restaurant/{restaurantId}` | List menu items of a restaurant |
| `PATCH` | `/menu-items/{id}` | Partially update a menu item |
| `DELETE` | `/menu-items/{id}` | Delete a menu item (soft delete) |

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

Spring Boot loads `.env` natively via `spring.config.import: optional:file:.env[.properties]` in `application.yaml` — no dependency, no manual `export`. This keeps secrets out of `application.yaml` and out of version control, while still allowing Docker Compose and local dev to share the same configuration source. Real environment variables still take precedence over the `.env` file, so Docker and prod are unaffected.
