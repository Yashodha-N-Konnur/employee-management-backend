# 🏢 Employee Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen?style=for-the-badge&logo=spring"/>
  <img src="https://img.shields.io/badge/MySQL-8.x-blue?style=for-the-badge&logo=mysql"/>
  <img src="https://img.shields.io/badge/JWT-Auth-purple?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger"/>
</p>

A **production-ready RESTful backend** for managing Employees, Departments, and Roles — built with Spring Boot 3, JPA/Hibernate, MySQL, JWT Security, MapStruct, and full Swagger documentation.

---

## 📐 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      HTTP Clients                           │
│              (Postman / Swagger UI / Frontend)              │
└───────────────────────┬─────────────────────────────────────┘
                        │ REST (JSON)
┌───────────────────────▼─────────────────────────────────────┐
│              Spring Security (JWT Filter)                   │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│               Controller Layer (@RestController)            │
│    EmployeeController / DepartmentController / RoleController│
│    AuthController                                           │
└───────────────────────┬─────────────────────────────────────┘
                        │ DTO
┌───────────────────────▼─────────────────────────────────────┐
│                Service Layer (@Service)                     │
│    EmployeeService / DepartmentService / RoleService        │
│    (Business logic, validation, transactions)               │
└───────────────────────┬─────────────────────────────────────┘
                        │ Entity
┌───────────────────────▼─────────────────────────────────────┐
│              Repository Layer (Spring Data JPA)             │
│    EmployeeRepository / DepartmentRepository / RoleRepository│
└───────────────────────┬─────────────────────────────────────┘
                        │ JDBC (Hibernate ORM)
┌───────────────────────▼─────────────────────────────────────┐
│                   MySQL 8 Database                          │
│           employees / departments / roles                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗂️ Project Structure

```
employee-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/employeemanagement/
│   │   │   ├── EmployeeManagementApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java        # Spring Security + JWT
│   │   │   │   └── OpenApiConfig.java         # Swagger / OpenAPI 3
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java        # POST /auth/login
│   │   │   │   ├── EmployeeController.java    # CRUD + search + filter
│   │   │   │   ├── DepartmentController.java
│   │   │   │   └── RoleController.java
│   │   │   ├── service/
│   │   │   │   ├── EmployeeService.java       # Interface
│   │   │   │   ├── DepartmentService.java
│   │   │   │   ├── RoleService.java
│   │   │   │   └── impl/
│   │   │   │       ├── EmployeeServiceImpl.java
│   │   │   │       ├── DepartmentServiceImpl.java
│   │   │   │       └── RoleServiceImpl.java
│   │   │   ├── repository/
│   │   │   │   ├── EmployeeRepository.java    # Spring Data JPA
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   └── RoleRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── BaseEntity.java            # createdAt / updatedAt
│   │   │   │   ├── Employee.java              # @SQLRestriction soft delete
│   │   │   │   ├── Department.java
│   │   │   │   ├── Role.java
│   │   │   │   └── EmployeeStatus.java        # Enum
│   │   │   ├── dto/
│   │   │   │   ├── request/                   # Inbound DTOs + validation
│   │   │   │   └── response/                  # Outbound DTOs + wrappers
│   │   │   ├── mapper/                        # MapStruct interfaces
│   │   │   ├── exception/                     # Global handler + custom exceptions
│   │   │   ├── security/                      # JWT Provider, Filter, EntryPoint
│   │   │   └── util/AppConstants.java
│   │   └── resources/
│   │       ├── application.properties         # MySQL config
│   │       ├── application-dev.properties     # H2 for local dev
│   │       ├── application-prod.properties    # Env-var driven
│   │       └── data.sql                       # Sample seed data
│   └── test/
│       └── java/com/example/employeemanagement/
│           ├── service/EmployeeServiceTest.java     # Mockito unit tests
│           ├── service/DepartmentServiceTest.java
│           ├── controller/EmployeeControllerTest.java # MockMvc tests
│           └── repository/EmployeeRepositoryTest.java # DataJpaTest
├── postman/
│   └── EmployeeManagementSystem.postman_collection.json
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 🗃️ Database Schema

```sql
departments
  id              BIGINT PK AUTO_INCREMENT
  department_name VARCHAR(100) NOT NULL
  department_code VARCHAR(20)  NOT NULL UNIQUE
  created_at      DATETIME
  updated_at      DATETIME

roles
  id               BIGINT PK AUTO_INCREMENT
  role_name        VARCHAR(50)  NOT NULL UNIQUE
  role_description VARCHAR(255)
  created_at       DATETIME
  updated_at       DATETIME

employees
  id             BIGINT PK AUTO_INCREMENT
  first_name     VARCHAR(50)  NOT NULL
  last_name      VARCHAR(50)  NOT NULL
  email          VARCHAR(100) NOT NULL UNIQUE
  phone          VARCHAR(15)
  salary         DECIMAL(12,2)
  joining_date   DATE
  status         ENUM('ACTIVE','INACTIVE','ON_LEAVE','TERMINATED')
  department_id  BIGINT FK → departments.id
  role_id        BIGINT FK → roles.id
  is_deleted     BOOLEAN DEFAULT FALSE      ← Soft delete flag
  deleted_at     DATETIME                   ← Soft delete timestamp
  created_at     DATETIME
  updated_at     DATETIME
```

**Relationships:**
- `departments` 1 ──< `employees` (OneToMany / ManyToOne)
- `roles`       1 ──< `employees` (OneToMany / ManyToOne)

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8.x (or Docker)

### Option A — Local (MySQL installed)

```bash
# 1. Clone
git clone https://github.com/your-username/employee-management-system.git
cd employee-management-system

# 2. Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS employee_db;"

# 3. Configure credentials
#    Edit src/main/resources/application.properties:
#    spring.datasource.username=YOUR_USER
#    spring.datasource.password=YOUR_PASS

# 4. Run
mvn spring-boot:run

# App starts at http://localhost:8080
```

### Option B — Docker Compose (recommended)

```bash
# 1. Clone
git clone https://github.com/your-username/employee-management-system.git
cd employee-management-system

# 2. Set environment variables
cp .env.example .env
# Edit .env with your passwords

# 3. Build and start
docker compose up -d --build

# 4. Check health
docker compose ps
curl http://localhost:8080/actuator/health

# 5. View logs
docker compose logs -f app
```

### Option C — Dev Profile (H2, no MySQL needed)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# H2 console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:empdb
```

---

## 🔐 Authentication

All API endpoints (except `/api/v1/auth/**` and Swagger) require a valid JWT Bearer token.

### Step 1 – Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "username": "admin",
    "role": "ROLE_ADMIN"
  }
}
```

### Step 2 – Use the Token

```bash
curl http://localhost:8080/api/v1/employees \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

| Role       | GET | POST / PUT / DELETE / PATCH |
|------------|-----|-----------------------------|
| ROLE_ADMIN | ✅  | ✅                          |
| ROLE_USER  | ✅  | ❌ (403 Forbidden)          |

**Default credentials:**
| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | admin123  | ROLE_ADMIN |
| user     | user123   | ROLE_USER  |

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint               | Description        | Auth |
|--------|------------------------|--------------------|------|
| POST   | /api/v1/auth/login     | Get JWT token      | No   |
| POST   | /api/v1/auth/logout    | Logout (stateless) | Yes  |

### Departments
| Method | Endpoint                          | Description          | Role  |
|--------|-----------------------------------|----------------------|-------|
| POST   | /api/v1/departments               | Create department    | ADMIN |
| GET    | /api/v1/departments               | List all             | ANY   |
| GET    | /api/v1/departments/{id}          | Get by ID            | ANY   |
| PUT    | /api/v1/departments/{id}          | Update               | ADMIN |
| DELETE | /api/v1/departments/{id}          | Delete               | ADMIN |
| GET    | /api/v1/departments/search?name=  | Search by name       | ANY   |

### Roles
| Method | Endpoint               | Description | Role  |
|--------|------------------------|-------------|-------|
| POST   | /api/v1/roles          | Create      | ADMIN |
| GET    | /api/v1/roles          | List all    | ANY   |
| GET    | /api/v1/roles/{id}     | Get by ID   | ANY   |
| PUT    | /api/v1/roles/{id}     | Update      | ADMIN |
| DELETE | /api/v1/roles/{id}     | Delete      | ADMIN |
| GET    | /api/v1/roles/search?name= | Search | ANY   |

### Employees
| Method | Endpoint                                    | Description                | Role  |
|--------|---------------------------------------------|----------------------------|-------|
| POST   | /api/v1/employees                           | Create employee            | ADMIN |
| GET    | /api/v1/employees?page=0&size=10            | Paginated list             | ANY   |
| GET    | /api/v1/employees/{id}                      | Get by ID                  | ANY   |
| PUT    | /api/v1/employees/{id}                      | Full update                | ADMIN |
| DELETE | /api/v1/employees/{id}                      | Soft delete                | ADMIN |
| PATCH  | /api/v1/employees/{id}/status               | Update status only         | ADMIN |
| GET    | /api/v1/employees/search?name=              | Search by name             | ANY   |
| GET    | /api/v1/employees/department/{deptId}       | Filter by department       | ANY   |
| GET    | /api/v1/employees/role/{roleId}             | Filter by role             | ANY   |
| GET    | /api/v1/employees/status/{status}           | Filter by status           | ANY   |

### Standard Response Format

```json
{
  "success": true,
  "message": "Employee created successfully",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Error Response Format

```json
{
  "statusCode": 404,
  "error": "Not Found",
  "message": "Employee not found with id: '99'",
  "path": "/api/v1/employees/99",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 📖 Swagger UI

After starting the app, open:
```
http://localhost:8080/swagger-ui.html
```
1. Click **Authorize** → enter `Bearer <your_token>`
2. All endpoints are now testable directly from the browser

---

## 🧪 Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=EmployeeServiceTest
mvn test -Dtest=EmployeeControllerTest
mvn test -Dtest=EmployeeRepositoryTest

# With coverage report
mvn test jacoco:report
# Report at: target/site/jacoco/index.html
```

---

## 🐳 Docker Commands

```bash
# Build image only
docker build -t ems-app:1.0 .

# Start all services
docker compose up -d

# Stop all services
docker compose down

# Stop and remove volumes (fresh start)
docker compose down -v

# View logs
docker compose logs -f app
docker compose logs -f mysql

# Access MySQL container
docker exec -it ems-mysql mysql -u root -p employee_db
```

---

## 📦 Maven Commands

```bash
mvn clean compile          # Compile only
mvn clean package          # Build JAR (target/*.jar)
mvn clean package -DskipTests  # Build without tests
mvn spring-boot:run        # Run directly
mvn dependency:tree        # View dependency tree
mvn versions:display-dependency-updates  # Check for updates
```

---

## 🔧 Configuration

| Property                          | Default       | Description              |
|-----------------------------------|---------------|--------------------------|
| `server.port`                     | 8080          | HTTP port                |
| `spring.datasource.url`           | localhost/employee_db | MySQL URL      |
| `spring.jpa.hibernate.ddl-auto`   | update        | Schema management        |
| `app.jwt.secret`                  | (base64 key)  | JWT signing secret       |
| `app.jwt.expiration`              | 86400000      | Token TTL (ms) = 24h     |
| `app.security.admin.username`     | admin         | Admin credential         |
| `app.security.user.username`      | user          | Read-only credential     |

---

## 🏗️ Tech Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| Language       | Java 17 (LTS)                       |
| Framework      | Spring Boot 3.2.3                   |
| ORM            | Spring Data JPA + Hibernate 6       |
| Database       | MySQL 8.x                           |
| Security       | Spring Security 6 + JWT (JJWT)      |
| Mapping        | MapStruct 1.5.5                     |
| Validation     | Jakarta Bean Validation             |
| Documentation  | SpringDoc OpenAPI 2.3 (Swagger UI)  |
| Testing        | JUnit 5 + Mockito + MockMvc         |
| Build          | Maven 3.9                           |
| Container      | Docker + Docker Compose             |
| Logging        | SLF4J + Logback                     |
| Boilerplate    | Lombok                              |

---

## 💼 Resume Bullet Points

> *Employee Management System — Java | Spring Boot 3 | JPA/Hibernate | MySQL | JWT | Docker*

- Designed and developed a production-ready RESTful backend to manage employee records with full CRUD for Employees, Departments, and Roles, following layered MVC architecture (Controller → Service → Repository)
- Implemented JWT-based authentication and RBAC (Admin/User roles) using Spring Security 6 with stateless session management and custom exception handling returning standardised JSON error responses
- Modelled JPA entity relationships (`@OneToMany`, `@ManyToOne`) with LAZY fetch, proper cascade types, and a soft-delete pattern using `@SQLRestriction`, preserving records for audit compliance
- Integrated MapStruct for zero-boilerplate DTO mapping, pagination/sorting via Spring Data Pageable, and full Swagger/OpenAPI 3 documentation for all 20+ endpoints
- Achieved high test coverage with JUnit 5 + Mockito (service unit tests), `@DataJpaTest` slice tests (repository), and MockMvc tests (controller); containerised with Docker multi-stage build and Docker Compose

---

## 📄 License

MIT License — free to use, modify, and distribute.
