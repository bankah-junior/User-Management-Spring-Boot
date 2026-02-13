# Contributing to User Management Spring Boot

Thank you for your interest in contributing! This document provides guidelines for contributing to this project.

## Development Workflow

### 1. Fork and Clone
```bash
git clone https://github.com/your-username/User-Management-Spring-Boot.git
cd User-Management-Spring-Boot
```

### 2. Create a Branch
```bash
git checkout -b feature/your-feature-name
```

### 3. Make Changes
- Write clean, maintainable code
- Follow Java coding standards
- Add appropriate comments
- Update tests as needed

### 4. Run Tests
```bash
# Run all tests
mvn clean test

# Ensure all tests pass before committing
```

### 5. Commit Changes
```bash
git add .
git commit -m "feat: add your feature description"
```

### Commit Message Convention
Follow conventional commits format:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `test:` - Test additions or modifications
- `refactor:` - Code refactoring
- `style:` - Code style changes (formatting, etc.)
- `chore:` - Build process or auxiliary tool changes

### 6. Push and Create Pull Request
```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub.

## Code Style Guidelines

### Java Code
- Use 4 spaces for indentation
- Follow standard Java naming conventions
- Keep methods focused and small
- Write self-documenting code
- Add JavaDoc for public methods

### Testing
- Write tests for all new features
- Maintain at least 80% code coverage
- Follow AAA pattern (Arrange, Act, Assert)
- Use descriptive test method names

## Pull Request Process

1. **Update Documentation** - Update README.md if needed
2. **Add Tests** - Ensure new code has test coverage
3. **Run CI Checks** - All GitHub Actions must pass
4. **Code Review** - Wait for review and address feedback
5. **Merge** - Once approved, your PR will be merged

## Definition of Done

A feature is considered "Done" when:
- ✅ Code is written and follows coding standards
- ✅ Unit tests are written with 80%+ coverage
- ✅ All tests pass in CI/CD pipeline
- ✅ Code is committed with meaningful messages
- ✅ Documentation is updated
- ✅ No critical bugs or security vulnerabilities

## Running the Application Locally

### Prerequisites
- Java 21
- Maven 3.6+
- MongoDB (running locally or via Docker)

### Start MongoDB with Docker
```bash
docker run -d -p 27017:27017 --name mongodb mongo:7.0
```

### Run the Application
```bash
mvn spring-boot:run
```

### Access the API
- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (when available)

## Questions?

If you have questions, please:
1. Check existing issues
2. Create a new issue with your question
3. Tag it with the "question" label

## Thank You!

Your contributions make this project better. Thank you for taking the time to contribute! 🎉
