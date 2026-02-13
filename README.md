# User Management Spring Boot

![Java CI with Maven](https://github.com/bankah-junior/User-Management-Spring-Boot/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)

## Task Status
- **US-001**: ✅ Create User Endpoint
- **US-002**: ✅ Get All Users Endpoint
- **US-003**: ✅ Get User by ID Endpoint
- **US-006**: ✅ Input Validation
- **US-008**: ✅ CI/CD Pipeline Setup

## Overview
User Management application built with Spring Boot providing REST API endpoints for CRUD operations on user entities with MongoDB integration.

## Features
- ✅ **RESTful API** - Complete CRUD operations
- ✅ **User Management** - Create, Read, Update, Delete users
- ✅ **Input Validation** - Bean Validation with custom error messages
- ✅ **MongoDB Integration** - NoSQL database with MongoTemplate
- ✅ **Exception Handling** - Global exception handler with structured error responses
- ✅ **API Documentation** - Swagger/OpenAPI ready
- ✅ **Comprehensive Testing** - Unit, integration, and validation tests
- ✅ **CI/CD Pipeline** - Automated build and test with GitHub Actions

## API Endpoints

### Create User
```http
POST /api/v1/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "age": 25
}
```

### Get All Users
```http
GET /api/v1/users
```

### Get User by ID
```http
GET /api/v1/users/{id}
```

## Validation Rules
- **Name**: Required, cannot be blank
- **Email**: Required, must be a valid email format
- **Age**: Required, must be between 18 and 100

## Technology Stack
- **Java 21** - Latest LTS version
- **Spring Boot 4.0.2** - Application framework
- **Spring Data MongoDB** - Database integration
- **Spring Boot Validation** - Input validation
- **MongoDB** - NoSQL database
- **Maven** - Build tool and dependency management
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing with MongoDB

## Prerequisites
- Java 21 or higher
- Maven 3.6+
- MongoDB 7.0+ (or Docker for MongoDB)

## Build and Test

### Build the project
```bash
mvn clean install
```

### Run tests
```bash
mvn test
```

### Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment:

- **Automated Builds** - Triggers on push to main/master branch
- **Automated Testing** - Runs all unit and integration tests
- **Test Reports** - Generates and uploads test results
- **Maven Caching** - Optimizes build time
- **Build Status** - See badge at the top of README

### Pipeline Features
- ✅ JDK 21 setup with Temurin distribution
- ✅ Maven dependency caching
- ✅ Automated test execution
- ✅ Test report generation
- ✅ Artifact upload for test results
- ✅ Build summary in GitHub Actions

## Project Structure
```
src/
├── main/
│   ├── java/com/amalitech/
│   │   ├── controller/      # REST controllers
│   │   ├── model/           # Domain models
│   │   ├── service/         # Business logic
│   │   ├── exception/       # Custom exceptions and handlers
│   │   └── repository/      # Data access layer
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/amalitech/
        ├── controller/      # Controller tests
        ├── service/         # Service tests
        └── validation/      # Validation tests
```

## Development

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceImplTest

# Run with coverage report
mvn clean test jacoco:report
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License.

## Author
Anthony Bekoe Bankah

## Acknowledgments
- Spring Boot Documentation
- MongoDB Documentation
- GitHub Actions Documentation

