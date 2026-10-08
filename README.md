# Banking API

REST API for managing bank accounts and performing financial operations. Built with Java and Spring Boot, the project applies Hexagonal Architecture to separate business logic from infrastructure, improving maintainability and testability.

## Technologies

- **Java 21**
- **Spring Boot:** Spring Web, Spring Data JPA, Bean Validation
- **PostgreSQL**
- **Maven**
- **Testing:** JUnit 5, Mockito

## Architecture

The project follows **Hexagonal Architecture (Ports and Adapters)** to separate business logic from infrastructure concerns.

- **Domain:** Contains the `Account` model and core business rules.
- **Application:** Implements use cases for account management and money transfers.
- **Ports:** Define the contracts required by the application, such as `AccountRepository`.
- **Infrastructure:** Implements persistence with PostgreSQL and Spring Data JPA, and exposes REST endpoints through controllers.

## Features

- Account lifecycle management: creation, retrieval, and updates.
- Financial operations: deposits, withdrawals, and account-to-account transfers.
- Request validation and centralized exception handling.
- PostgreSQL persistence.
- Unit tests for business rules and use cases.

## API Endpoints

| Method | Endpoint                  | Description                     |
| ------ | ------------------------- | ------------------------------- |
| `POST` | `/accounts`               | Create an account               |
| `GET`  | `/accounts/{id}`          | Retrieve an account             |
| `PUT`  | `/accounts/{id}`          | Update account information      |
| `POST` | `/accounts/{id}/deposit`  | Deposit money                   |
| `POST` | `/accounts/{id}/withdraw` | Withdraw money                  |
| `POST` | `/transfers`              | Transfer money between accounts |

## Setup and Installation

### Prerequisites

- Java 21
- PostgreSQL

### Database Configuration

Create a PostgreSQL database named `banking`.

Configure the database connection in `src/main/resources/application.properties` using environment variables for credentials.

Set the following environment variables before running the application:

- `DB_USERNAME`
- `DB_PASSWORD`

### Run the Application

On Windows PowerShell:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
.\mvnw.cmd spring-boot:run
```

### Run Tests

```powershell
.\mvnw.cmd clean test
```

## Status

🚧 In development
