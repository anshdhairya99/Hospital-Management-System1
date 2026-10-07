# Hospital Management System

A REST backend built with **Java 17, Spring Boot 3, Spring Data JPA / Hibernate and Spring Security (JWT, role-based)**.

## Features
| Role | Capabilities |
|------|--------------|
| **ADMIN** | Create departments and doctors, view all patients and appointments, dashboard statistics |
| **DOCTOR** | View own appointments, confirm / complete / cancel them, view patients, write prescriptions |
| **PATIENT** | Self-register, search doctors, book / cancel appointments, view own prescriptions |

## Highlights (what to talk about in a viva / interview)
- **JPA relationships**: `OneToOne` (user profile), `ManyToOne` / `OneToMany` (doctor-department, appointment-doctor/patient), `OneToOne` (prescription-appointment)
- **Lazy loading + `@EntityGraph`** to avoid the N+1 query problem
- **Locking**: `@Version` (optimistic) on appointments, `PESSIMISTIC_WRITE` on the doctor row while booking to prevent double booking
- **Auditing**: `@CreatedDate` / `@LastModifiedDate` through `BaseEntity`
- **JPQL**: slot-conflict count and group-by statistics with an interface projection
- **Pagination and sorting** with `Pageable`
- **Security**: BCrypt, stateless JWT filter, `@PreAuthorize` role checks, ownership checks in the service layer
- **Global exception handling** with a uniform error body, **Bean Validation** on DTOs
- **Entities never leave the service layer**: records are used as DTOs
- **Swagger UI** and **JUnit 5 / Mockito** tests

## Architecture
`Controller -> Service -> Repository -> Database` with `dto`, `entity`, `security`, `config` and `exception` packages under `com.hospital`.

## Run
Requirements: JDK 17+, Maven 3.8+.

```bash
mvn spring-boot:run              # in-memory H2, no setup
mvn test                         # run unit tests
```

MySQL instead of H2:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql   # edit application-mysql.properties first
```

URLs
- Swagger UI: http://localhost:8080/swagger-ui.html (use **Authorize** and paste the JWT)
- H2 console: http://localhost:8080/h2-console  (JDBC URL `jdbc:h2:mem:hospitaldb`, user `sa`, empty password)

Seeded accounts
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| dr.smith | doctor123 | DOCTOR |

Patients register themselves via `POST /api/auth/register`. See `api-requests.http` for a ready-to-run walkthrough.

## Appointment lifecycle
`PENDING -> CONFIRMED -> COMPLETED`, and `CANCELLED` from `PENDING` or `CONFIRMED`.

## API summary
| Method | Endpoint | Role |
|--------|----------|------|
| POST | /api/auth/register, /api/auth/login | public |
| GET | /api/doctors?specialization= | any logged-in user |
| POST | /api/appointments | PATIENT |
| GET | /api/appointments/my | PATIENT, DOCTOR |
| GET | /api/appointments | ADMIN |
| PATCH | /api/appointments/{id}/status?status= | DOCTOR |
| PATCH | /api/appointments/{id}/cancel | PATIENT |
| POST | /api/prescriptions | DOCTOR |
| GET | /api/prescriptions/my | PATIENT |
| GET | /api/prescriptions/appointment/{id} | DOCTOR, PATIENT (owner only) |
| GET | /api/patients/me | PATIENT |
| GET | /api/patients, /api/patients/{id} | ADMIN, DOCTOR |
| POST/GET | /api/admin/departments | ADMIN |
| POST | /api/admin/doctors | ADMIN |
| GET | /api/admin/stats | ADMIN |

## Ideas to extend
Billing and invoices, email/SMS reminders, doctor availability calendar, refresh tokens, React/Angular front end, Docker Compose with MySQL, Flyway migrations.
