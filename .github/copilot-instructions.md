# Hospital Management System - AI Agent Instructions

## Project Overview
A microservices-based Hospital Management System for managing patient records, appointments, medical staff, and healthcare workflows. Built as a personal project to demonstrate modern distributed architecture patterns.

## Tech Stack
- **Backend**: Java Spring Boot (microservices architecture)
- **Frontend**: React.js
- **Database**: PostgreSQL (one per service for data isolation)
- **Cache**: Redis (shared cache layer)
- **Containerization**: Docker & Docker Compose
- **Authentication**: JWT (simpler for microservices communication)
- **API Gateway**: Spring Cloud Gateway (recommended for routing)

## Architecture & Structure

### Microservices Design
Each service should be independently deployable with its own PostgreSQL database:
- **patient-service**: Patient registration, demographics, medical history
- **appointment-service**: Scheduling, availability management, calendar
- **doctor-service**: Doctor profiles, specializations, schedules
- **billing-service**: Invoices, payments, insurance claims
- **notification-service**: Email/SMS alerts for appointments (optional)
- **api-gateway**: Routes requests, handles JWT validation, CORS
- **service-registry**: Eureka for service discovery (optional for personal project)

### Project Structure
```
/hospital-management-system
├── backend/
│   ├── api-gateway/
│   ├── patient-service/
│   ├── appointment-service/
│   ├── doctor-service/
│   ├── billing-service/
│   └── common-lib/          # Shared DTOs, utilities, security config
├── frontend/
│   └── hospital-ui/         # React app
├── docker-compose.yml       # Orchestrates all services, DBs, Redis
└── README.md
```

### Inter-Service Communication
- **Synchronous**: Use RestTemplate or WebClient for direct HTTP calls (e.g., appointment-service → doctor-service to check availability)
- **Asynchronous**: Consider RabbitMQ or Kafka if you add event-driven features later
- **Service Discovery**: Either hardcode service URLs in docker-compose networking or use Eureka
- **Resilience**: Implement circuit breakers with Resilience4j for fault tolerance

## Spring Boot Conventions

### Project Structure (per service)
```
patient-service/
├── src/main/java/com/hospital/patient/
│   ├── controller/      # REST endpoints (@RestController)
│   ├── service/         # Business logic (@Service)
│   ├── repository/      # JPA repositories (@Repository)
│   ├── model/           # JPA entities (@Entity)
│   ├── dto/             # Request/Response objects
│   ├── config/          # Security, Redis, DB configs
│   └── exception/       # Custom exceptions, global handlers
├── src/main/resources/
│   ├── application.yml  # Service-specific config
│   └── db/migration/    # Flyway/Liquibase migrations
└── Dockerfile
```

### Code Conventions
- **Entities**: Use JPA annotations (`@Entity`, `@Table`, `@Column`); follow camelCase for fields, snake_case for DB columns
- **DTOs**: Separate request/response DTOs from entities; use `PatientRequestDto`, `PatientResponseDto`
- **Naming**: `PatientController`, `PatientService`, `PatientRepository` - consistent suffixes
- **Validation**: Use `@Valid` with `@NotNull`, `@Email`, `@Pattern` on DTOs
- **Global Exception Handler**: Create `@ControllerAdvice` class to return consistent error responses
- **API Versioning**: Use `/api/v1/patients` for all endpoints

### JWT Authentication Pattern
- **api-gateway**: Validates JWT on incoming requests, forwards with user context
- **Individual services**: Optionally verify JWT or trust gateway (use shared secret)
- **Security Config**: Extend `WebSecurityConfigurerAdapter`, configure `JwtAuthenticationFilter`
- **Token Structure**: Include `userId`, `role` (PATIENT, DOCTOR, ADMIN) in claims

### Testing Requirements
- Write unit tests for all business logic, especially calculations (billing, medication dosages)
- Integration tests for critical workflows: patient admission, appointment scheduling, prescription management
- Test edge cases: concurrent appointment bookings, duplicate patient registrations

### Key Workflows
- **Patient Registration**: Validate demographics, check for duplicates, assign unique patient ID
- **Appointment Scheduling**: Check doctor availability, prevent double-booking, send confirmations as notifications
- **Medical Records**: Version control for medical history, maintain audit logs
- **Billing**: Calculate costs, apply insurance, generate invoices

## Database & Caching

### PostgreSQL Setup
- Each microservice has its own PostgreSQL database (e.g., `patient_db`, `appointment_db`)
- Use Flyway or Liquibase for version-controlled schema migrations
- **Indexing**: Create indexes on `patient_id`, `doctor_id`, `appointment_date`, `created_at`
- **Transactions**: Use `@Transactional` for operations spanning multiple entities
- **Soft Deletes**: Add `deleted_at` timestamp column; use `@Where(clause = "deleted_at IS NULL")`

### Redis Caching Strategy
- Cache doctor schedules, department lists, frequently accessed patient summaries
- Use `@Cacheable`, `@CacheEvict` on service methods
- Configure Redis connection in `application.yml`: `spring.redis.host`, `spring.redis.port`
- **TTL**: Set appropriate expiration (e.g., 30 minutes for schedules, 5 minutes for real-time data)

### Docker Compose Configuration
```yaml
services:
  postgres-patient:
    image: postgres:15
    environment:
      POSTGRES_DB: patient_db
  redis:
    image: redis:7-alpine
  patient-service:
    build: ./backend/patient-service
    depends_on:
      - postgres-patient
      - redis
```

## React Frontend

### Project Structure
```
hospital-ui/
├── src/
│   ├── components/      # Reusable UI components
│   ├── pages/           # Page-level components (PatientDashboard, Appointments)
│   ├── services/        # API client functions (axios calls to api-gateway)
│   ├── context/         # React Context for auth, global state
│   ├── hooks/           # Custom hooks (useAuth, usePatients)
│   └── utils/           # Helper functions, constants
├── public/
└── package.json
```

### API Integration
- All requests go through `api-gateway` at `http://localhost:8080/api/v1`
- Store JWT in localStorage or httpOnly cookie
- Use axios interceptors to attach JWT to all requests
- Handle 401 (unauthorized) globally to redirect to login

### UI Patterns
- Use React Router for navigation between patient list, appointment booking, billing
- Implement loading states and error boundaries
- Form validation using Formik or React Hook Form
- Date/time pickers for appointment scheduling (react-datepicker or similar)

## Common Commands

### Backend (Spring Boot Services)
```bash
# Build all services
mvn clean install

# Run a specific service
cd backend/patient-service && mvn spring-boot:run

# Run tests
mvn test

# Build Docker image
docker build -t patient-service:latest .
```

### Frontend (React)
```bash
# Install dependencies
cd frontend/hospital-ui && npm install

# Run development server
npm start

# Build for production
npm run build
```

### Docker Compose
```bash
# Start all services (databases, Redis, backend services, frontend)
docker-compose up -d

# View logs
docker-compose logs -f patient-service

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up -d --build
```

## Development Workflow

1. **Starting a new service**: Copy structure from existing service, update `application.yml` with new database config
2. **Adding API endpoint**: Controller → Service → Repository pattern; always return DTOs, not entities
3. **Inter-service call**: Use `WebClient` or `RestTemplate` with service URL from config
4. **Database migration**: Create Flyway migration file in `src/main/resources/db/migration/V{version}__description.sql`
5. **Caching**: Add `@Cacheable("cacheName")` on service method, configure TTL in Redis config
6. **Testing**: Mock repositories in service tests, use `@WebMvcTest` for controller tests
