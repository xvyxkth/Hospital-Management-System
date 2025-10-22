# Hospital Management System

A microservices-based Hospital Management System built with Java Spring Boot, React.js, PostgreSQL, Redis, and Docker.

## Architecture

This system follows a microservices architecture with the following services:

- **patient-service** (Port 8081): Manages patient records, demographics, and medical history
- **doctor-service** (Port 8082): Manages doctor profiles, specializations, and schedules
- **appointment-service** (Port 8083): Handles appointment scheduling and management
- **billing-service** (Port 8084): Manages invoices, payments, and insurance claims
- **api-gateway** (Port 8080): Routes requests and handles authentication
- **frontend** (Port 3000): React.js web application

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.1.5
- **Frontend**: React.js
- **Database**: PostgreSQL 15 (separate database per service)
- **Cache**: Redis 7
- **Containerization**: Docker & Docker Compose
- **Authentication**: JWT
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+ (for frontend)

## Project Structure

```
hospital-management-system/
├── backend/
│   ├── common-lib/              # Shared DTOs, utilities, exceptions
│   ├── patient-service/         # Patient management microservice
│   ├── doctor-service/          # Doctor management microservice
│   ├── appointment-service/     # Appointment scheduling microservice
│   ├── billing-service/         # Billing and payments microservice
│   └── api-gateway/             # API Gateway with JWT auth
├── frontend/
│   └── hospital-ui/             # React web application
├── docker-compose.yml           # Docker orchestration
└── README.md
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd hospital-management-system
```

### 2. Build Common Library

```bash
cd backend/common-lib
mvn clean install
cd ../..
```

### 3. Start Services with Docker Compose

```bash
docker-compose up -d --build
```

This will start:
- PostgreSQL databases for each service
- Redis cache
- All microservices
- (Frontend when implemented)

### 4. Verify Services are Running

```bash
docker-compose ps
```

### 5. Check Service Health

```bash
# Patient Service
curl http://localhost:8081/api/v1/patients

# Doctor Service (when implemented)
curl http://localhost:8082/api/v1/doctors

# Appointment Service (when implemented)
curl http://localhost:8083/api/v1/appointments
```

## Running Services Locally (Without Docker)

### Start PostgreSQL

```bash
# Install PostgreSQL
brew install postgresql@15

# Start PostgreSQL
brew services start postgresql@15

# Create databases
createdb patient_db
createdb doctor_db
createdb appointment_db
createdb billing_db
```

### Run a Service

```bash
cd backend/patient-service
mvn spring-boot:run
```

## API Documentation

### Patient Service API

Base URL: `http://localhost:8081/api/v1/patients`

#### Endpoints

- `POST /` - Create a new patient
- `GET /` - Get all patients
- `GET /{id}` - Get patient by ID
- `PUT /{id}` - Update patient
- `DELETE /{id}` - Delete patient (soft delete)
- `GET /search?query={query}` - Search patients

#### Example: Create Patient

```bash
curl -X POST http://localhost:8081/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    "address": "123 Main St, New York, NY",
    "bloodGroup": "O+",
    "emergencyContact": "9876543210",
    "medicalHistory": "No major illnesses",
    "allergies": "None"
  }'
```

## Testing

### Run Unit Tests

```bash
cd backend/patient-service
mvn test
```

### Run Integration Tests

```bash
# Use the provided test script
./test-patient-service.sh
```

## Development Workflow

### Adding a New Microservice

1. Create service directory in `backend/`
2. Add `pom.xml` with common-lib dependency
3. Create Spring Boot application with standard structure:
   - `controller/` - REST endpoints
   - `service/` - Business logic
   - `repository/` - JPA repositories
   - `model/` - JPA entities
   - `dto/` - Request/Response DTOs
   - `config/` - Configuration classes
   - `exception/` - Exception handlers
4. Add service to `docker-compose.yml`
5. Create Dockerfile for the service

### Database Migrations

Use Flyway or Liquibase for version-controlled schema migrations.

```
src/main/resources/db/migration/
├── V1__create_patients_table.sql
├── V2__add_indexes.sql
└── V3__add_columns.sql
```

## Monitoring & Logging

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f patient-service

# Last 100 lines
docker-compose logs --tail=100 patient-service
```

### Database Access

```bash
# Connect to patient database
docker exec -it postgres-patient psql -U postgres -d patient_db
```

## Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v
```

## Troubleshooting

### Port Already in Use

```bash
# Check what's using the port
lsof -i :8081

# Kill the process
kill -9 <PID>
```

### Database Connection Issues

1. Ensure PostgreSQL container is running
2. Check connection string in `application.yml`
3. Verify database exists

### Build Failures

```bash
# Clean and rebuild
mvn clean install -U

# Skip tests
mvn clean install -DskipTests
```
