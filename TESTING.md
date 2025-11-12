# Testing Guide

## Overview
This document describes the testing approach and setup for the **Chemical List Management API**.  
The project follows **Test-Driven Development (TDD)** principles where possible and uses **JUnit 5**, **Spring Boot Test**, and **Testcontainers** for integration testing with a real Redis instance.

All tests can be executed locally or in CI (e.g., GitHub Actions). The Redis container automatically starts and stops before and after test execution.

---

## Testing Layers

### 1. Unit Tests
- **Scope:** Test business logic inside services and utilities.
- **Tools:** JUnit 5 + Mockito.
- **Annotations:**
  ```java
  @ExtendWith(MockitoExtension.class)
  ```

  ## Examples

- Validate creation of new chemical list metadata.
- Ensure duplicate CIRI-IDs are ignored (idempotent behavior).
- Verify Redis repository correctly handles serialization and key naming.

## 2. Integration Tests

**Scope:** End-to-end tests using Spring Boot Test and Testcontainers for Redis.

**Annotations:**

```
@SpringBootTest
@Testcontainers
```
Container Setup:

```
@Container
static GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine")
        .withExposedPorts(6379)
        .withEnv("REDIS_APPENDONLY", "yes");

```

Spring automatically wires the container’s host and port into  
`spring.data.redis.host` and `spring.data.redis.port`.

---

### 3. API (Web Layer) Tests

**Scope:** Validate controller endpoints and security behaviors.  

**Tools:** `@WebMvcTest`, `MockMvc`, `JSONPath`.

**Examples:**

- `GET /lists` returns public data with **200 OK**.  
- `POST /lists` with a valid API key returns **201 Created**.  
- Unauthorized `POST` returns **401** or **403** with **RFC 9457 Problem Details** body.  
- `POST /lists/{id}/chemicals` with JSON array behaves **idempotently**.

### 4. End-to-End (Optional)

If desired, Postman or HTTP client tests can run against a running local instance using api-examples.http or Postman collection.

### Example Test Class: Redis Integration

```
@SpringBootTest
@Testcontainers
class RedisIntegrationTests {

    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.4-alpine"));

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void shouldStoreAndRetrieveListMetadata() {
        redisTemplate.opsForHash().put("list:1", "name", "Example List");
        String name = (String) redisTemplate.opsForHash().get("list:1", "name");
        assertEquals("Example List", name);
    }
}
```

### Running Tests Locally
1. Using Maven
    ```
    mvn clean test
    ```

1. Running Specific Test Class
    ```
    mvn -Dtest=ChemicalListServiceTests test
    ```
1. Continuous Testing
    ```
    mvn test -T 1C -fn
    ```

### Testcontainers Configuration
#### Required Dependencies (pom.xml)
```
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>junit-jupiter</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>redis</artifactId>
  <scope>test</scope>
</dependency>
```

### Notes

* No manual Redis installation is required; the container runs automatically.
* Tests will pull the redis:7.4-alpine image if not cached.
* If Docker is not available, skip integration tests using:

```
mvn test -DskipITs
```

### CI Integration (GitHub Actions)

The workflow automatically runs unit and integration tests:
```
- name: Run tests
  run: mvn --batch-mode clean verify
```

Testcontainers works in GitHub Actions without special setup; Docker is available by default.

## Example Coverage Goals

| Layer            | Coverage Goal | Notes                                      |
|------------------|---------------|--------------------------------------------|
| Service Logic    | ≥ 90%         | Focus on data validation and idempotency   |
| Controller       | ≥ 85%         | All endpoints, including auth and errors   |
| Redis Repository | ≥ 80%         | Hash and Set operations                    |
| Integration      | ≥ 70%         | End-to-end paths                           |

### Test Data

Seed data files are located in src/test/resources/data/seed-data.json.
They contain:

* Example chemical lists
* Sample CIRI-IDs for batch addition tests

### Guidelines

* Prefer Arrange-Act-Assert test structure.
* Use assertj fluent assertions for readability.
* Each test class should run in isolation—no shared state.
* All tests must pass with mvn verify before commit.

### Example Command Summary

```
# Run all tests
mvn clean verify

# Run only integration tests
mvn failsafe:integration-test

# Skip integration tests (if Docker not available)
mvn test -DskipITs

```

## Troubleshooting

| Issue                       | Possible Fix                                 |
|------------------------------|----------------------------------------------|
| Cannot connect to Redis container | Ensure Docker Desktop is running             |
| Tests hang on startup        | Pull latest Redis image manually             |
| Port conflicts               | Use dynamic port mapping (default in Testcontainers) |
| Out of memory                | Increase Docker memory to ≥ 2 GB             |

## References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Testcontainers Java Docs](https://java.testcontainers.org/)
- [Spring Boot Testing Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Redis Docker Image](https://hub.docker.com/_/redis)
