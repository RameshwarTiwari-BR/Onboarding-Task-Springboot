# Spring Boot + Keycloak + PostgreSQL Onboarding Service

A stateless RESTful backend built with **Java 21**, **Spring Boot 4.1.2**, **Spring Security (OAuth2 Client + Resource Server)**, **Keycloak**, and **PostgreSQL 16** (managed with **Flyway**).

This project implements a dual-client identity architecture where Keycloak handles authentication, password hashing, and JWT issuance, while PostgreSQL stores domain entities (`users`, `user_identities`, `projects`, `project_members`, `tasks`) with strict role-based and membership-based access control.

---

## 1. Tech Stack

- **Language & Runtime:** Java 21
- **Framework:** Spring Boot 4.1.2 (Spring WebMVC, Spring Data JPA, Spring Security)
- **Identity Provider (IdP):** Keycloak (`Onboarding` realm)
- **Database:** PostgreSQL 16 + Flyway Migrations
- **HTTP Client:** Spring `RestClient` + `OAuth2AuthorizedClientManager`
- **API Documentation:** `springdoc-openapi-starter-webmvc-ui` (Swagger UI)
- **Rate Limiting:** Bucket4j (Token Bucket filter per IP)

---

## 2. System Architecture & Keycloak Setup

The system separates **Identity Management (Keycloak)** from **Application Domain Data (PostgreSQL)**:

```text
+------------------+       POST /users or /login       +-----------------------+
|  Client / UI /   | --------------------------------> |                       |
|  Postman /       |                                   |   Spring Boot 4.1.2   |
|  Swagger UI      | <-------------------------------- |   (Port 8080)         |
+------------------+   201/200 + Signed JWT Tokens     +-----------+-----------+
         |                                                         |
         |  Protected API Calls                                    | Admin API & Token Exchange
         |  Authorization: Bearer <JWT>                            | (RestClient)
         v                                                         v
+------------------+   1. Startup: OIDC Discovery      +-----------------------+
|   Spring Boot    | --------------------------------> |       Keycloak        |
|  Resource Server |   2. First JWT: Fetch JWKS Keys   |   Realm: Onboarding   |
|  (Local Verify)  | --------------------------------> |   (Port 8081)         |
+--------+---------+   (Cached in RAM via Nimbus)      +-----------------------+
         |
         | Resolve (iss, sub) -> users.id
         v
+------------------+
|  PostgreSQL 16   |
|  users           |
|  user_identities |
|  projects        |
|  project_members |
|  tasks           |
+------------------+
```

### Keycloak Clients (`Onboarding` Realm)
1. **`onboarding-admin` (Confidential Service Account):**
   - **Client Authentication:** `ON` (uses `client_id` + `client_secret`)
   - **Service Accounts Roles:** `ON` with `realm-management` -> `manage-users`
   - **Default Client Scope:** `kc-admin-api` (required for Keycloak 26.8.2+ Admin API audience validation)
   - **Purpose:** Used exclusively by `KeycloakAdminService` (`client_credentials` grant) to create and roll back users via the Keycloak Admin REST API.
2. **`onboarding-client` (Public Login Client):**
   - **Client Authentication:** `OFF` (no secret)
   - **Enabled Flows:** `Standard flow` and `Direct access grants` (`grant_type=password`)
   - **Purpose:** Used to authenticate end-users and issue signed `access_token` (JWT) and `refresh_token` pairs.

---

## 3. Database Schema & Identity Bridge

Passwords are **never** stored in PostgreSQL. Instead, `public.user_identities` bridges external Keycloak accounts to internal PostgreSQL user UUIDs:

- **`users`:** Internal application profile (`id`, `email`, `first_name`, `last_name`, `status`).
- **`user_identities`:** Maps `(issuer, subject)` -> `user_id`, enforcing `provider = 'KEYCLOAK'` and `password_hash IS NULL` via database check constraints.
- **`projects`:** Project records linked to `created_by` (`users.id`).
- **`project_members`:** Enforces project roles (`OWNER`, `ADMIN`, `MEMBER`) and a single-owner partial unique index (`uq_project_single_owner`).
- **`tasks`:** Enforces composite foreign keys (`fk_tasks_creator_membership` and `fk_tasks_assignee_membership`) ensuring both creator and assignee belong to `project_members`.

---

## 4. End-to-End Request Lifecycle

### Phase A: Application Startup (Public Key Discovery)
1. Spring Boot reads `spring.security.oauth2.resourceserver.jwt.issuer-uri` (`http://localhost:8081/realms/Onboarding`).
2. `JwtDecoders.fromIssuerLocation(...)` calls Keycloak's `/.well-known/openid-configuration` endpoint, discovers `jwks_uri` (`/protocol/openid-connect/certs`), and instantiates the `NimbusJwtDecoder` bean in the `ApplicationContext`.
3. On the **first incoming request containing a `Bearer` JWT**, `NimbusJwtDecoder` fetches the RSA public keys (`JWKS`) and caches them in RAM. All subsequent JWT validations happen locally in memory (<1 ms) with zero network calls to Keycloak.

### Phase B: User Signup (`POST /api/v1/users`)
1. `RateLimitFilter` verifies the client IP is within the auth rate limit (5 requests/min).
2. `SecurityFilterChain` allows unauthenticated access (`permitAll()`).
3. `UserService` checks `users` for duplicate emails.
4. `KeycloakAdminService` acquires a cached `onboarding-admin` token via `OAuth2AuthorizedClientManager` and calls `POST /admin/realms/Onboarding/users` to create the user in Keycloak, extracting the new Keycloak UUID (`sub`) from the `Location` response header.
5. `UserService` saves the user in `public.users` and links `subject` in `public.user_identities`.
   - *Compensating Rollback:* If any PostgreSQL insert or token exchange fails, `KeycloakAdminService.deleteKeycloakUser(sub)` deletes the user from Keycloak so no orphan records remain.
6. `KeycloakAdminService` exchanges the user's credentials via `onboarding-client` (`POST /protocol/openid-connect/token`) and returns `201 Created` with `UserResponse` + `TokenResponse` (`access_token` and `refresh_token`).

### Phase C: Returning User Login (`POST /api/v1/auth/login`)
1. `AuthController` forwards `email` and `password` to `KeycloakAdminService.fetchUserToken()`.
2. Keycloak verifies credentials and signs a fresh JWT with its Realm Private Key (`RS256`).
3. Invalid credentials (`400 invalid_grant` / `401`) are translated into a clean `401 Unauthorized` response.

### Phase D: Protected APIs (`/projects`, `/members`, `/tasks`)
1. Client sends `Authorization: Bearer <access_token>`.
2. Spring Security's `BearerTokenAuthenticationFilter` verifies the `RS256` signature, `iss`, and `exp` locally using the cached public key and stores the `Jwt` in `SecurityContextHolder`.
3. `CurrentUserService` extracts `(iss, sub)` from the JWT, resolves the linked `User` via `user_identities`, verifies `status == "ACTIVE"`, and provides the internal `users.id` UUID.
4. Services enforce membership and role rules:
   - **`POST /api/v1/projects`:** Creates the project and automatically inserts the creator as `OWNER` in `project_members`.
   - **`POST /api/v1/projects/{projectId}/members`:** Allows `OWNER` or `ADMIN` to invite registered users as `ADMIN` or `MEMBER`.
   - **`GET / PUT / DELETE` Projects & Tasks:** Scoped strictly to verified project members and roles.

---

## 5. Key Endpoints

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/users` | Public | Register user in Keycloak + Postgres and return initial JWTs |
| `POST` | `/api/v1/auth/login` | Public | Authenticate existing user and return `access_token` + `refresh_token` |
| `GET` | `/api/v1/actuator/health` | Public | Health check for application and PostgreSQL connection |
| `GET` | `/swagger-ui.html` | Public | Interactive OpenAPI / Swagger UI documentation |
| `POST` | `/api/v1/projects` | Bearer JWT | Create a project and assign caller as `OWNER` |
| `GET` | `/api/v1/projects` | Bearer JWT | List all projects where the caller is a member |
| `POST` | `/api/v1/projects/{id}/members` | Bearer JWT | Add a registered user (`ADMIN` or `MEMBER`) to a project |
| `GET` | `/api/v1/projects/{id}/members` | Bearer JWT | List all members of a project |
| `POST` | `/api/v1/projects/{id}/tasks` | Bearer JWT | Create a task (validates creator & assignee membership) |
| `GET` | `/api/v1/tasks` | Bearer JWT | Paginated task list filtered by creator and optional `status` |

---

## 6. Configuration (`application.yml`)

```yaml
spring:
  application:
    name: springboot-onboarding

  datasource:
    url: jdbc:postgresql://localhost:5432/springboot_onboarding
    username: postgres
    password: SpringSecure123

  flyway:
    enabled: true

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081/realms/Onboarding
      client:
        registration:
          keycloak-admin:
            provider: keycloak
            client-id: onboarding-admin
            client-secret: ${KEYCLOAK_ADMIN_SECRET}
            authorization-grant-type: client_credentials
            scope: kc-admin-api
        provider:
          keycloak:
            token-uri: http://localhost:8081/realms/Onboarding/protocol/openid-connect/token

  jpa:
    hibernate:
      ddl-auto: validate

keycloak:
  admin:
    base-url: http://localhost:8081
    realm: Onboarding
    issuer: ${spring.security.oauth2.resourceserver.jwt.issuer-uri}
    login-client-id: onboarding-client
```

---

## 7. Running Locally

1. **Start PostgreSQL & Keycloak:** Ensure PostgreSQL is running on port `5432` (`springboot_onboarding` database) and Keycloak is running on port `8081` with the `Onboarding` realm configured.
2. **Set Environment Secret:**
   ```bash
   export KEYCLOAK_ADMIN_SECRET=<your-onboarding-admin-client-secret>
   ```
3. **Run Spring Boot:**
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Explore & Test APIs:** Open `http://localhost:8080/swagger-ui.html`, register a user via `POST /api/v1/users`, copy the `access_token`, click **Authorize**, and test protected endpoints.
