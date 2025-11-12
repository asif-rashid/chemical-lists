# Chemical List Management API

## Overview
The **Chemical List Management API** is a RESTful application built with **Spring Boot** and **Spring Framework**, designed to create and manage chemical lists and their associated chemicals.  
It enables users to define chemical lists (with labels, names, and descriptions) and associate one or more **CIRI-IDs** representing chemicals.  

The application uses **Redis** as its backend store (Azure Cache for Redis with AOF persistence).  
All **read** operations are public, while **write** operations (create, update, delete) require authentication using either **API key** or **OpenID Connect (OIDC)**.

---

## Key Features
- Create and manage **chemical lists**
- Add or remove chemicals by **CIRI-ID**
- Support for **single** and **batch** additions
- Idempotent operations for duplicates
- **Redis-based persistence** with simple key structures (no RediSearch)
- **Public GET endpoints**; authentication required for POST, PATCH, DELETE
- **Spring profiles** for `dev`, `apikey`, and `oidc`
- **Problem Details (RFC 9457)** compliant error responses
- **Testcontainers** support for local Redis integration testing
- Deployable to **Azure App Service** with environment variable configuration

---

## Core Concepts

### Chemical List
| Field | Description | Constraints |
|--------|--------------|-------------|
| listId | System-generated numeric ID | Redis key |
| label | Unique short identifier | Max 30 chars |
| name | Descriptive name | Max 200 chars |
| description | Optional extended text | Max 2000 chars |
| createdBy | Username or principal | Auto-filled |
| createdDate | ISO datetime | Auto-filled |
| lastUpdatedBy | Username or principal | Auto-filled |
| lastUpdated | ISO datetime | Auto-filled |

### Chemical Membership
Each list may contain one or more **CIRI-IDs** (strings).  
Adding a duplicate ID is **idempotent** and does not raise an error.

---

## Technology Stack
- **Language:** Java 21  
- **Framework:** Spring Boot (current GA) with Spring Modulith  
- **Database:** Redis (Azure Cache for Redis, AOF enabled)  
- **Security:** API Key or OIDC (Azure Entra ID)  
- **Build Tool:** Maven  
- **Testing:** JUnit 5, Testcontainers  
- **Deployment:** Azure App Service (Linux, container or build-on-push)

---

## Project Structure

```
chemical-list-api/
├── src/main/java/.../chemical/
│ ├── modulith/ (Spring Modulith modules)
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── config/
│ └── model/
├── src/test/java/.../chemical/
├── openapi.yaml
├── api-examples.http
├── architecture.md
├── security.md
├── problem-details.md
├── redis-model.md
├── testing.md
├── .env.example
└── README.md
```

## Profiles

| Profile | Description | Authentication |
|----------|--------------|----------------|
| `dev` | Local development with no authentication | None |
| `apikey` | API key header (`x-api-key`) required for write operations | API key |
| `oidc` | Azure Entra ID login for write operations | OIDC |

---

## Running Locally

### 1. Prerequisites
- JDK 21  
- Maven 3.9+  
- Docker (for local Redis)

### 2. Environment Variables
Copy `.env.example` → `.env` and set values:
```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
API_KEY=your-api-key
AZURE_OIDC_ISSUER=https://login.microsoftonline.com/{tenant-id}/v2.0
AZURE_OIDC_CLIENT_ID=your-client-id
```

### 3. Start Redis and Application

```
docker compose up -d redis
mvn spring-boot:run -Dspring-boot.run.profiles=dev

```

Application will be available at:

```
http://localhost:8080
```
## Example Endpoints

| Method | Endpoint | Description | Auth |
|--------|-----------|--------------|------|
| GET    | `/lists` | Retrieve all lists | Public |
| GET    | `/lists/{listId}` | Retrieve a list by ID | Public |
| POST   | `/lists` | Create a new list | Auth required |
| PATCH  | `/lists/{listId}` | Update list metadata | Auth required |
| DELETE | `/lists/{listId}` | Delete a list | Auth required |
| GET    | `/lists/{listId}/chemicals` | Get all chemicals in list | Public |
| POST   | `/lists/{listId}/chemicals` | Add one or more chemicals | Auth required |
| DELETE | `/lists/{listId}/chemicals/{ciriId}` | Remove a chemical | Auth required |

See `openapi.yaml` and `api-examples.http` for full request/response examples.

# Deployment to Azure App Service

1. Build and Package

```bash
mvn clean package -DskipTests

```
2. Deploy JAR or container image.
3. Configure environment variables in Azure Portal.
4. Connect Azure Cache for Redis with AOF enabled.

Testing

Use Testcontainers for integration testing:
```
mvn test
```

See testing.md for setup details.