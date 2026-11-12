# Banking API

A RESTful API for bank account management and financial operations, built with **Java 21** and **Spring Boot**. The project follows **Hexagonal Architecture (Ports and Adapters)** to keep business logic independent from infrastructure concerns.

It includes account management, deposits, withdrawals, money transfers, authentication, authorization, transactional operations, and concurrency control using PostgreSQL.

## Technologies

- **Java 21**
- **Spring Boot 4.1.2**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Bean Validation
- **PostgreSQL 16**
- **Maven**
- **Testing:** JUnit 5, Mockito, Spring Security Test, Testcontainers
- **Code coverage:** JaCoCo
- **Containerization:** Docker and Docker Compose

## Architecture

The application follows **Hexagonal Architecture**, separating the domain and application logic from external technologies.

```text
banking_api/
├── application/
│   ├── account/
│   │   ├── dto/
│   │   ├── port/
│   │   └── use cases
│   ├── transfer/
│   │   └── dto/
│   └── user/
│       └── port/
├── domain/
│   └── account/
└── infrastructure/
    ├── persistence/
    │   ├── account/
    │   └── user/
    ├── security/
    └── web/
        ├── account/
        ├── exception/
        └── transfer/
```

### Main layers

- **Domain:** Contains the `Account` model, business rules, and domain exceptions. It does not depend on persistence or web frameworks.
- **Application:** Implements use cases and defines the ports required to interact with external systems.
- **Infrastructure:** Provides the adapters for PostgreSQL persistence, Spring Security, and REST controllers.

The application layer depends on abstractions such as `AccountRepository` and `UserIdentityRepository`, while infrastructure implements those contracts.

## Features

### Account management

- Create accounts associated with existing users.
- Retrieve account information.
- Update account information.
- Deposit and withdraw money.
- Validate account ownership before allowing account operations.

### Money transfers

- Transfer money between two accounts.
- Allow transfers to accounts owned by other users.
- Validate source account ownership.
- Ensure both accounts use the same currency.
- Prevent transfers between the same account.
- Update both balances within a single database transaction.

### Security

- HTTP Basic authentication using Spring Security.
- Password hashing with BCrypt.
- Role-based authorization for account creation.
- Account ownership validation for account operations.
- Authenticated access to API endpoints.

### Transaction management and concurrency

- Transactional money transfers to ensure both account updates succeed or are rolled back together.
- Pessimistic write locking to protect account balances during concurrent operations.
- Accounts are locked in ascending ID order during transfers to reduce the risk of deadlocks.

### Validation and error handling

- Request validation using Jakarta Bean Validation.
- Domain-level business rule validation.
- Centralized exception handling with consistent API error responses.

## API Endpoints

All endpoints require HTTP Basic authentication.

| Method | Endpoint                  | Description                     | Authorization           |
| ------ | ------------------------- | ------------------------------- | ----------------------- |
| `POST` | `/accounts`               | Create an account               | ADMIN                   |
| `GET`  | `/accounts/{id}`          | Retrieve an account             | Account owner           |
| `PUT`  | `/accounts/{id}`          | Update account information      | Account owner           |
| `POST` | `/accounts/{id}/deposit`  | Deposit money                   | Account owner           |
| `POST` | `/accounts/{id}/withdraw` | Withdraw money                  | Account owner           |
| `POST` | `/transfers`              | Transfer money between accounts | Owner of source account |

**Transfer authorization:** Users must own the source account, but the destination account can belong to another user.

## Example: Money Transfer

### Request

`POST /transfers`

```json
{
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "amount": 150.0
}
```

### Response

```json
{
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "amount": 150.0,
  "sourceBalance": 350.0,
  "targetBalance": 650.0
}
```

The example illustrates a successful transfer. Actual balances depend on the accounts' initial values.

## Setup and Installation

### Prerequisites

- Java 21
- PostgreSQL 16 (for local execution)
- Docker Desktop (for containerized execution and integration tests)
- Git

### Clone the repository

```bash
git clone https://github.com/LauvB/banking-api.git
cd banking-api
```

### Option 1: Run locally

1. Create a PostgreSQL database named `banking`.
2. Configure the database credentials using environment variables.

On Windows PowerShell:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
```

3. Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```

The API will be available at:

`http://localhost:8080`

### Option 2: Run with Docker Compose

1. Copy the example environment file:

```powershell
Copy-Item .env.example .env
```

2. Edit `.env` and set a local database password:

```env
POSTGRES_DB=banking
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
```

3. Build and start the services:

```bash
docker compose up --build
```

Docker Compose starts the PostgreSQL database and the API. The API waits for the database health check before starting.

The services are exposed locally at:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5433`

To stop the services:

```bash
docker compose down
```

To stop the services and remove the database volume:

```bash
docker compose down -v
```

**Note:** Removing the volume deletes the persisted database data.

## Testing

Run the complete Maven verification lifecycle:

```powershell
.\mvnw.cmd clean verify
```

The test suite includes:

- Unit tests for domain rules and application use cases.
- Repository adapter tests using Mockito.
- Security and authorization tests.
- Integration tests using PostgreSQL and Testcontainers.
- Transaction rollback tests.
- Concurrent operation tests.

**Docker must be running** for tests that use Testcontainers.

### Code coverage

JaCoCo generates an HTML coverage report during the Maven `verify` phase.

After running the tests, open:

```text
target/site/jacoco/index.html
```

## Manual API Testing

For step-by-step instructions and examples of real API requests using Docker Compose, PostgreSQL, PowerShell, and curl, see the [Manual API Testing Guide](./docs/manual-testing.md).

## Configuration

Database credentials are supplied through environment variables:

| Variable            | Description                                |
| ------------------- | ------------------------------------------ |
| `DB_USERNAME`       | PostgreSQL username                        |
| `DB_PASSWORD`       | PostgreSQL password                        |
| `POSTGRES_DB`       | Database name used by Docker Compose       |
| `POSTGRES_USER`     | PostgreSQL user used by Docker Compose     |
| `POSTGRES_PASSWORD` | PostgreSQL password used by Docker Compose |

The `.env` file is excluded from version control. Use `.env.example` as a template.

## 👩‍💻 Author

**Laura Beltrán**\
Systems Engineer

[GitHub](https://github.com/LauvB)
