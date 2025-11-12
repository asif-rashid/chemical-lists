# Security Guide

## Overview
The **Chemical List Management API** implements a layered security model where all **read operations** are public and **write operations** (create, update, delete) require authentication.  
Two authentication mechanisms are supported, selectable via **Spring profiles**:

1. **API Key Authentication** (`apikey` profile)
2. **OpenID Connect (OIDC)** using **Azure Entra ID** (`oidc` profile)

In the `dev` profile, all security restrictions are disabled for local development.

---

## Security Profiles

| Profile | Authentication Type | Typical Use Case |
|----------|--------------------|------------------|
| `dev` | None (open access) | Local development, testing |
| `apikey` | Static API key via `x-api-key` header | Automated systems or internal service-to-service calls |
| `oidc` | OpenID Connect with Azure Entra ID | Authenticated users via Azure identity |

Each profile is activated using:
```bash
SPRING_PROFILES_ACTIVE=apikey
# or
SPRING_PROFILES_ACTIVE=oidc
````

---

## 1. Public Access Rules

The following endpoints are always public (no authentication required):

```
GET /lists
GET /lists/{listId}
GET /lists/{listId}/chemicals
```

All other HTTP methods (POST, PATCH, DELETE) require authentication under both security profiles.

---

## 2. API Key Authentication (`apikey` Profile)

### Header Format

```
x-api-key: <your-secret-key>
```

### Behavior

* If the provided key matches the configured `API_KEY` environment variable, the request is authorized.
* Invalid or missing keys return a `401 Unauthorized` response using the **Problem Details** format.

### Example Request

```bash
curl -X POST http://localhost:8080/lists \
  -H "x-api-key: my-secret-key" \
  -H "Content-Type: application/json" \
  -d '{"label":"testlist","name":"Toxic Substances","description":"Example list"}'
```

### Environment Variable

```bash
API_KEY=my-secret-key
```

### Implementation Notes

* Implemented via a simple `OncePerRequestFilter` or `AuthenticationFilter` checking the header value.
* Deny by default if header missing or invalid.
* API key is stored as an environment variable or in **Azure Key Vault** in production.

---

## 3. OpenID Connect (OIDC) Authentication (`oidc` Profile)

### Overview

In `oidc` mode, the API delegates authentication to **Azure Entra ID** (Microsoft identity platform).
Users obtain a bearer token and include it in the `Authorization` header.

### Header Format

```
Authorization: Bearer <access_token>
```

### Required Environment Variables

```bash
AZURE_OIDC_ISSUER=https://login.microsoftonline.com/<tenant-id>/v2.0
AZURE_OIDC_CLIENT_ID=<application-client-id>
AZURE_OIDC_AUDIENCE=<api-audience>
```

### Example Request

```bash
curl -X POST http://localhost:8080/lists \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5..." \
  -H "Content-Type: application/json" \
  -d '{"label":"lablist","name":"Laboratory Chemicals","description":"Internal catalog"}'
```

### Behavior

* Spring Security verifies the token signature using Azure’s JWKS endpoint.
* If token is valid, the user’s identity (`sub`, `email`, or `preferred_username`) is stored in the security context.
* `createdBy` and `lastUpdatedBy` fields use this principal name.

### Roles and Claims

No role-based restrictions are applied initially. Any authenticated user can perform write operations.

---

## 4. Error Handling and Problem Details

All authentication and authorization errors use **RFC 9457 (Problem Details)** responses.

| Condition                    | HTTP Status | Type           | Title                         |
| ---------------------------- | ----------- | -------------- | ----------------------------- |
| Missing `x-api-key` header   | 401         | `unauthorized` | Missing authentication header |
| Invalid API key              | 403         | `forbidden`    | Invalid API key               |
| Missing Bearer token         | 401         | `unauthorized` | Missing bearer token          |
| Invalid / expired OIDC token | 403         | `forbidden`    | Invalid access token          |

Example error response:

```json
{
  "type": "https://api.ciri.org/errors/unauthorized",
  "title": "Missing authentication header",
  "status": 401,
  "detail": "The request requires authentication but no credentials were provided.",
  "instance": "/lists"
}
```

---

## 5. Security Configuration Summary

| Endpoint                | Method        | Auth Required | Profile       |
| ----------------------- | ------------- | ------------- | ------------- |
| `/lists`                | GET           | No            | All           |
| `/lists/{id}`           | GET           | No            | All           |
| `/lists/{id}/chemicals` | GET           | No            | All           |
| `/lists`                | POST          | Yes           | apikey / oidc |
| `/lists/{id}`           | PATCH, DELETE | Yes           | apikey / oidc |
| `/lists/{id}/chemicals` | POST, DELETE  | Yes           | apikey / oidc |

---

## 6. Implementation Outline

### API Key Filter Example

```java
@Component
@Order(1)
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("x-api-key");
        if (header == null || !header.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/problem+json");
            response.getWriter().write("{\"title\":\"Missing or invalid API key\",\"status\":401}");
            return;
        }
        chain.doFilter(request, response);
    }
}
```

### OIDC Security Configuration

```java
@Configuration
@EnableWebSecurity
@Profile("oidc")
public class OidcSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/lists/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt())
            .build();
    }
}
```

---

## 7. Azure Deployment Notes

* Use **Azure Key Vault** to store `API_KEY`, `AZURE_OIDC_CLIENT_ID`, and related secrets.
* Configure **App Settings** in Azure App Service to map vault values to environment variables.
* Enable **HTTPS only** for the service.
* Optionally, use **Private Endpoint** for internal network access.

---

## 8. Local Development

* Default profile: `dev` (no authentication)
* Run with API key:

  ```bash
  SPRING_PROFILES_ACTIVE=apikey API_KEY=my-secret mvn spring-boot:run
  ```
* Run with OIDC:

  ```bash
  SPRING_PROFILES_ACTIVE=oidc \
  AZURE_OIDC_ISSUER=https://login.microsoftonline.com/<tenant-id>/v2.0 \
  AZURE_OIDC_CLIENT_ID=<client-id> \
  mvn spring-boot:run
  ```

---

## References

* [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
* [Azure Entra ID OIDC Reference](https://learn.microsoft.com/en-us/entra/identity-platform/v2-protocols-oidc)
* [RFC 9457 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc9457)


