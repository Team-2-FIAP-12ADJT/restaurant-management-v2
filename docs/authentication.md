# Authentication & Authorization

The API uses stateless **JWT bearer** authentication (HS256), implemented with Spring
Security's OAuth2 Resource Server. There are no server-side sessions.

## Login

```
POST /api/v1/auth/login
Content-Type: application/json

{ "login": "admin", "password": "Senh@1234" }
```

Response `200 OK`:

```json
{ "accessToken": "<jwt>", "expiresAt": "2026-07-11T12:34:56Z" }
```

Invalid login **or** password returns `401` with the same message (`Login ou senha
inválidos`) — the response does not reveal which field was wrong. A soft-deleted
account cannot log in (returns `401`).

Send the token on every protected request:

```
Authorization: Bearer <jwt>
```

The token carries `sub` (user id), `login`, and a `roles` claim. The role is derived
from the user's **UserType** (e.g. `Admin` → authority `ROLE_ADMIN`, `Dono` →
`ROLE_DONO`; uppercased and diacritic-stripped). A user with no UserType gets an empty
`roles` claim and is rejected (`403`) on any role-restricted route.

`Admin` is the system administrator. `Dono` and `Cliente` are business roles — `Dono`
keeps administrative access to the routes below (they are guarded by
`hasAnyRole('ADMIN', 'DONO')`).

## Authorization matrix

| Endpoint | Method | Rule |
|---|---|---|
| `/api/v1/auth/login` | POST | Public |
| `/api/v1/users`, `/users/**` | POST · GET · PATCH · DELETE | `ROLE_ADMIN` or `ROLE_DONO` |
| `/api/v1/users-type/**` | all | `ROLE_ADMIN` or `ROLE_DONO` |
| `/api/v1/restaurants` | POST | `ROLE_ADMIN` or `ROLE_DONO` |
| `/api/v1/restaurants/{id}` | PUT · DELETE | `ROLE_ADMIN` or `ROLE_DONO` |
| `/api/v1/restaurants/**` | GET | Any authenticated user |
| `/api/v1/menu-items/**` | POST · PATCH · DELETE | `ROLE_ADMIN` or `ROLE_DONO` |
| `/api/v1/menu-items/**` | GET | Any authenticated user |
| `/v3/api-docs`, `/swagger-ui.html` | GET | Public |

- No token on a protected route → `401` (RFC 9457 problem+json).
- Valid token without the required role → `403`.

Authorization is scalable to new user types: a `UserType` created at runtime flows into
the `roles` claim automatically. Route rules that name a specific role
(`hasAnyRole('ADMIN', 'DONO')`) are code, so a new role with its own rules requires a
deploy.

## Bootstrap user

A Liquibase seed (`db/changelog/seeds/0002-seed-admin-user.xml`) creates an `Admin` user
for first access:

| Field | Value |
|---|---|
| login | `admin` |
| password | `Senh@1234` *(dev only — change for real deployments)* |
| user type | `Admin` |

## Configuration

| Variable | Description | Default |
|---|---|---|
| `JWT_SECRET` | Base64 HS256 key, decodes to ≥32 bytes (`openssl rand -base64 32`). **Required** — the app fails to start if unset (no fallback; tests inject an ephemeral key) | — |
| `JWT_ACCESS_TOKEN_EXPIRATION_TIME` | Access token lifetime, in minutes | `15` |

## Postman

The collection (`postman/`) has an **Auth → Login** request. Run it once: a test script
stores `accessToken` as a collection variable, and every other request inherits it via
collection-level bearer auth. Credentials come from the `adminLogin` / `adminPassword`
variables.

## Not yet implemented (follow-ups)

- Refresh token (rotation, logout/revocation).
- Ownership checks (a `Cliente` acting only on its own resource).
- Public self-service registration.
