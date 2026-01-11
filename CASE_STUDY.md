## Overview

This case study documents the approach, design decisions, and implementation details for the `Java Code Assignment` based on the `Quarkus framework`.

The goal of the assignment was to:

- Implement missing and incomplete business logic

- Apply domain rules and validations

- Improve code quality and reliability

- Add comprehensive unit and integration tests

- Achieve code coverage of 80% or higher

- Follow software engineering best practices

- Document challenges and solution strategies

- Optionally enhance the solution with CI/CD and health checks

## Assignment & Codebase Context
### About the Assignment

The tasks for this assignment are defined in:

- CODE_ASSIGNMENT.md

- BRIEFING.md

- QUESTIONS.md

The assignment required implementing missing methods, completing incomplete logic, and answering design-related questions while respecting the domain constraints and validations.

### About the Codebase

- The codebase is based on Quarkus Quickstarts

  - Reference: https://github.com/quarkusio/quarkus-quickstarts

- Architecture follows a layered approach:

	- Resource (API)

	- Service / Use Case

	- Gateway

	- Persistence

### Technical Requirements

To compile and run the application locally, the following are required:

- JDK 17+

- PostgreSQL (or Docker to run PostgreSQL)

### JDK Configuration

- JAVA_HOME must point to a JDK 17+ installation

- java command must be available on the system path

### Build & Run Instructions

#### Build the application

```sh
./mvnw package
```

#### Run in development mode (Live Coding)

```sh
./mvnw quarkus:dev
```

- Supports hot reload

- Changes to code and JPA entities are applied automatically

- Database schema updates dynamically

#### Run in JVM mode

1. Build the application:

```sh
./mvnw package
```

2. Start PostgreSQL using Docker:

```sh
docker run -it --rm=true --name quarkus_test \
  -e POSTGRES_USER=quarkus_test \
  -e POSTGRES_PASSWORD=quarkus_test \
  -e POSTGRES_DB=quarkus_test \
  -p 15432:5432 postgres:13.3
```

3 Run the application:

```sh
java -jar ./target/quarkus-app/quarkus-run.jar
```

## Understanding the Domain & Constraints

Before implementing the tasks, BRIEFING.md was thoroughly reviewed to understand:

- Core domain entities (Location, Store, Warehouse)

- Business rules and constraints

- Integration with a legacy system

- Capacity, stock, and replacement rules

This ensured that implementations aligned with real business expectations and avoided purely technical solutions.

## Implemented Tasks
### Location – Identifier Resolution

#### Task:
Implement `resolveByIdentifier` in `LocationGateway`.

#### Implementation Approach:

- Resolved locations based on provided identifiers

- Handled invalid or non-existent identifiers gracefully

-  Replaced `UnsupportedOperationException` with meaningful domain behavior

#### Outcome:

- This method now acts as a prerequisite validation step for warehouse operations

- Improved stability and clarity of location-related logic

### Store – Transactional Consistency with Legacy System

#### Task:
Ensure that calls to `LegacyStoreManagerGateway` occur only after database commits.

#### Problem Identified:

- Legacy system updates could occur even if the database transaction failed

- Risk of data inconsistency between systems

#### Solution Strategy:

- Ensured store changes are persisted first

- Triggered legacy synchronization only after successful commit

- Maintained transactional integrity and system reliability

#### Outcome:

- Guaranteed consistency between internal system and legacy system

- Prevented propagation of uncommitted or rolled-back data

### Warehouse – Creation, Replacement & Archival
#### Warehouse Creation Validations Implemented:

- Business unit code uniqueness

- Location existence and validity

- Maximum warehouse count per location

- Warehouse capacity constraints

- Stock vs capacity validation

#### Design Approach:

- Validation logic separated from persistence

- Domain-specific exceptions used for clarity

#### Warehouse Replacement Additional Validations:

- New warehouse capacity must accommodate existing stock

- Stock between old and new warehouses must match

- Old warehouse is archived instead of deleted

#### Outcome:

- Safe and consistent replacement process

- Preserved historical traceability

## Testing Strategy
### Unit Testing

Framework: `Mocktio framework`

#### Coverage Includes:

- Positive scenarios (valid creation, replacement)

- Negative scenarios (invalid location, capacity overflow)

- Error scenarios (duplicate business unit codes, stock mismatch)

#### Mocks were used for:

- Gateways

- External dependencies

- Legacy integrations

### Integration Testing

- Verified transactional behavior

- Ensured database state correctness

- Validated post-commit behavior for legacy synchronization

## Code Coverage

Tool Used: `JaCoCo`

#### Results:

- Line coverage ≥ 82%

- Focused on business logic rather than trivial code

Coverage reports are generated as part of the Maven build.

## Code Quality & Best Practices

The following best practices were consistently applied:

- Clean and readable code

- Meaningful naming conventions

- Layered architecture

- Clear separation of concerns

- Avoidance of hardcoded values

- Maintainable and extensible design

## Exception Handling & Logging
### Exception Handling

- Domain-specific exceptions for validation failures

- Clear, actionable error messages

- Avoidance of generic runtime exceptions

## CI/CD & Health Checks (Good-to-Have)

###CI/CD Pipeline

- Implemented using GitHub Actions

- Automatically runs:

	- Build

	- Tests

	- Code coverage checks

##  Challenges & Solutions

#### Challenge 1: Transactional Consistency with Legacy Systems

Solution: Deferred legacy calls until after successful database commits.

#### Challenge 2: Complex Warehouse Validation Rules

Solution: Structured validations step-by-step with explicit failure reasons.

#### Challenge 3: Meaningful Test Coverage

Solution: Focused tests on business behavior rather than trivial methods.

## Conclusion

- This case study demonstrates:

- Strong understanding of the problem domain

- Correct application of business rules and validations

- High code quality and test coverage

- Practical use of CI/CD and observability features

- Balanced and intentional use of AI assistance

The solution is production-ready, maintainable, and aligned with modern Java backend development standards.