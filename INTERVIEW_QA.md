# 🎯 Employee Management System — Interview Q&A

---

## 🏗️ Architecture & Design

**Q1: Why did you use a layered (Controller → Service → Repository) architecture?**

**A:** Separation of concerns. Each layer has one responsibility:
- **Controller**: HTTP request/response, input validation delegation, status codes
- **Service**: Business logic, transactions, cross-cutting orchestration
- **Repository**: Data access, query logic, DB interaction only

This follows SRP (Single Responsibility Principle) from SOLID. Benefits: easier unit testing (mock each layer independently), team scalability (frontend devs own controllers, backend own services), and changeability (swap MySQL for MongoDB by only touching the repository layer).

---

**Q2: What is the DTO pattern and why is it critical here?**

**A:** DTOs (Data Transfer Objects) decouple the API contract from the database schema. Without DTOs:
- Circular JSON serialization errors occur: `Employee → Department → List<Employee> → ...`
- Internal fields (like `isDeleted`, `deletedAt`) are exposed to API consumers
- Any DB schema change breaks the public API

With DTOs: `EmployeeResponseDto` flattens `department.departmentName` directly, avoiding nested object cycles. MapStruct auto-generates the mapping code at compile time — zero reflection overhead, unlike ModelMapper.

---

**Q3: How does MapStruct differ from ModelMapper? Why did you choose it?**

**A:**
| Feature       | MapStruct                        | ModelMapper             |
|---------------|----------------------------------|-------------------------|
| Approach      | Compile-time code generation     | Runtime reflection      |
| Performance   | ~same as hand-written code       | Slower (reflection)     |
| Error detection| Compile-time                    | Runtime                 |
| Debuggability | Generated code is readable       | Hard to debug           |
| Spring Boot 3  | Full Jakarta EE support         | Needs config            |

MapStruct generates actual Java code in `target/generated-sources` — you can read and debug it. Any mapping issue (e.g., missing field) is a **compile error**, not a runtime surprise.

---

**Q4: Explain the soft delete implementation.**

**A:** Instead of `DELETE FROM employees WHERE id=?`, we set `is_deleted = true` and `deleted_at = NOW()`. The Hibernate 6 `@SQLRestriction("is_deleted = false")` annotation appends `AND is_deleted = false` to **every** query on that entity automatically — no manual filtering needed.

Benefits: audit trail, data recovery, referential integrity (foreign keys still valid), compliance (GDPR deletion requests handled separately).

Tradeoff: table grows indefinitely without a purge job. In production, add a scheduled task to archive/purge records older than N years.

---

**Q5: What are the JPA fetch strategies and when to use each?**

**A:**
- **LAZY** (default for `@OneToMany`, `@ManyToOne`): Proxy object returned; SQL fires only when you access the collection. Best for most cases — prevents loading huge graphs.
- **EAGER**: Joins and loads immediately. Can cause N+1 problem and cartesian product explosions.

In this project: `Employee.department` and `Employee.role` are LAZY. The `findByIdWithDetails()` JPQL uses `LEFT JOIN FETCH` to eagerly load them in **one SQL query** when needed — this is the recommended pattern: lazy by default, fetch-join when required.

---

## 🔐 Spring Security & JWT

**Q6: Walk through the JWT authentication flow.**

**A:**
```
1. Client → POST /api/v1/auth/login {username, password}
2. AuthController → AuthenticationManager.authenticate()
3. Spring Security → UserDetailsService.loadUserByUsername()
4. BCrypt password comparison
5. On success → JwtTokenProvider.generateToken()
6. Token returned to client

--- subsequent requests ---
7. Client sends: Authorization: Bearer <token>
8. JwtAuthenticationFilter.doFilterInternal() intercepts
9. Extracts token from header
10. JwtTokenProvider.validateToken() — checks signature + expiry
11. Sets SecurityContextHolder with authenticated user
12. Request proceeds to controller
```

---

**Q7: Why is CSRF disabled for REST APIs?**

**A:** CSRF (Cross-Site Request Forgery) protects against browser-based session hijacking — it's only relevant when the server issues cookies. REST APIs use stateless JWT tokens sent in the `Authorization` header, not cookies. Browsers don't auto-send headers to cross-origin requests, so CSRF attacks are impossible. Enabling CSRF on a JWT API just adds complexity with zero security benefit.

---

**Q8: What is `SessionCreationPolicy.STATELESS` and why use it?**

**A:** Tells Spring Security never to create or use an `HttpSession`. Each request is authenticated fresh via the JWT token. Benefits:
- **Horizontal scalability**: Any server instance handles any request (no sticky sessions)
- **No memory leaks**: Sessions don't accumulate on the server
- **Microservice-ready**: Services can validate tokens independently

---

**Q9: How would you implement token blacklisting for logout?**

**A:** JWT is stateless — you can't "invalidate" a token at the server without state. Solutions:
1. **Redis blacklist**: On logout, store `{jti: tokenId, expiry: tokenExpiry}` in Redis. JwtAuthenticationFilter checks Redis before allowing the request. O(1) lookup.
2. **Short-lived tokens + refresh tokens**: Access token expires in 15 min; refresh token is stored in DB and can be revoked.
3. **Version field in user table**: Embed `tokenVersion` in JWT claim; increment on logout. Filter compares claim vs DB version.

For this system, I've noted in the `AuthController.logout()` that the current implementation is client-side; Redis blacklist is the production recommendation.

---

## 🗄️ Spring Data JPA & Hibernate

**Q10: What is the N+1 problem and how did you solve it?**

**A:** N+1 occurs when fetching a list of N entities triggers N additional queries for each entity's lazy association.

Example without fix:
```sql
SELECT * FROM employees;                    -- 1 query
SELECT * FROM departments WHERE id = 1;     -- for employee 1
SELECT * FROM departments WHERE id = 2;     -- for employee 2
-- N more queries for N employees = N+1 total
```

Solution in this project: `findByIdWithDetails()` uses JPQL `LEFT JOIN FETCH` to load `department` and `role` in a single query:
```sql
SELECT e, d, r FROM Employee e
LEFT JOIN FETCH e.department d
LEFT JOIN FETCH e.role r
WHERE e.id = :id
```
For list endpoints, the DTOs flatten only `department.id` and `department.name` — avoiding the need to load the full graph.

---

**Q11: Explain `@Transactional(readOnly = true)` at class level with method-level overrides.**

**A:** Setting `readOnly = true` at class level is a best practice for service classes that primarily read data. Benefits:
- Hibernate skips "dirty checking" (comparing entity state before/after transaction) — faster
- Database driver can optimize for read-only (e.g., skip write locks)
- Documents intent clearly

Methods that **write** data override this: `@Transactional` (readOnly defaults to false) on `createEmployee()`, `updateEmployee()`, `deleteEmployee()`.

---

**Q12: What is `@EnableJpaAuditing` and how does it work?**

**A:** Enables Spring Data JPA's auditing infrastructure. Combined with `@EntityListeners(AuditingEntityListener.class)` on `BaseEntity`:
- `@CreatedDate` → auto-populated with `LocalDateTime.now()` on `INSERT`
- `@LastModifiedDate` → auto-updated on every `UPDATE`

No manual `setCreatedAt()` calls needed anywhere. You can also add `@CreatedBy` / `@LastModifiedBy` with an `AuditorAware<String>` bean that reads from `SecurityContextHolder`.

---

**Q13: What cascade types did you use and why?**

**A:** `Department.employees` uses `CascadeType.ALL` — meaning save/delete operations cascade from Department to its Employees. This makes sense because employees are owned by a department.

`orphanRemoval = false` is deliberate: deleting a department should NOT auto-delete its employees (they might be reassigned). In production, a business rule check in the service layer prevents deleting a department with active employees.

---

## 🧪 Testing

**Q14: Explain the difference between `@SpringBootTest`, `@WebMvcTest`, and `@DataJpaTest`.**

**A:**
| Annotation        | What it loads               | Best for               | Speed |
|-------------------|-----------------------------|------------------------|-------|
| `@SpringBootTest` | Full application context    | Integration tests      | Slow  |
| `@WebMvcTest`     | Web layer only (controllers, filters, security) | Controller tests | Fast |
| `@DataJpaTest`    | JPA layer only (repositories, entities, H2) | Repository tests | Fast |

Using slice tests means: EmployeeServiceTest (pure Mockito — no Spring context at all = fastest), EmployeeControllerTest (`@WebMvcTest` — mocks service), EmployeeRepositoryTest (`@DataJpaTest` — real H2 queries).

---

**Q15: What is `@InjectMocks` vs `@Mock` in Mockito?**

**A:**
- `@Mock`: Creates a Mockito proxy of the class. All methods return default values (null, 0, false) unless stubbed with `when().thenReturn()`.
- `@InjectMocks`: Creates a real instance of the class under test and **injects** `@Mock` fields into it via constructor injection (preferred), setter, or field injection.

`@ExtendWith(MockitoExtension.class)` initialises the mocks automatically — no need for `MockitoAnnotations.openMocks(this)`.

---

## 🌐 REST API Design

**Q16: Why did you use PATCH for status update instead of PUT?**

**A:** HTTP semantics matter:
- **PUT**: Replace the **entire** resource. Requires sending all fields.
- **PATCH**: Partial update. Only send the fields you want to change.

`PATCH /api/v1/employees/{id}/status` with `{"status": "ON_LEAVE"}` is semantically correct — you're updating one field, not replacing the whole employee. Using PUT here would require sending the full employee payload just to change the status.

---

**Q17: How did you implement pagination and sorting?**

**A:** Spring Data JPA's `Pageable` interface handles this:

```java
Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
Page<Employee> result = employeeRepository.findAll(pageable);
```

`Page<T>` contains: `content` (the data), `totalElements`, `totalPages`, `number`, `size`, `first`, `last`. I wrap this in a `PagedResponse<T>` DTO for consistent API output. The controller accepts `page`, `size`, `sortBy`, `sortDir` as query parameters with sensible defaults.

---

**Q18: How does the Global Exception Handler work?**

**A:** `@RestControllerAdvice` is a specialised `@ControllerAdvice` that applies to all `@RestController` classes. `@ExceptionHandler` methods match specific exception types:

```
ResourceNotFoundException  → 404 Not Found
DuplicateResourceException → 409 Conflict
ValidationException        → 400 Bad Request
MethodArgumentNotValidException → 400 (Bean Validation failures)
AccessDeniedException      → 403 Forbidden
BadCredentialsException    → 401 Unauthorized
Exception (catch-all)      → 500 Internal Server Error
```

All responses follow the same `ErrorResponse` structure — API consumers always know where to look for error info.

---

## 🐳 DevOps

**Q19: Explain the multi-stage Docker build.**

**A:** Two `FROM` instructions in the Dockerfile:
1. **Stage 1 (builder)**: `maven:3.9.6-eclipse-temurin-17-alpine` — installs Maven, downloads dependencies (cached layer), builds the JAR
2. **Stage 2 (runtime)**: `eclipse-temurin:17-jre-alpine` — only the JRE, no Maven/JDK

Final image is ~200 MB instead of ~500 MB. The `COPY --from=builder` only copies the JAR — source code and Maven cache are discarded. Non-root user (`appuser`) follows the principle of least privilege.

---

**Q20: How would you scale this application to handle 100,000 concurrent users?**

**A:** Multiple layers:
1. **Database**: Read replicas for GET requests; connection pooling (HikariCP already configured); query indexing on `email`, `department_id`, `role_id`, `status`
2. **Caching**: Redis cache for `getAllDepartments()`, `getAllRoles()` (rarely change); `@Cacheable` annotation
3. **Application tier**: Stateless design (JWT) means multiple instances behind a load balancer with no session affinity needed
4. **Rate limiting**: Spring Cloud Gateway or API Gateway with rate limiting per token
5. **Async operations**: `@Async` for non-critical work (email notifications, audit logging)
6. **Database pagination**: Always use `Pageable` — never `findAll()` on large tables
7. **Observability**: Micrometer + Prometheus + Grafana for metrics; distributed tracing with Zipkin

