# Mint — Personal Budgeting App

A modular budgeting application built with **Spring Boot**, designed as a microservices-ready architecture. The project is split into 5 independent modules, each owned by a different team member.

---

## Modules

| Module | Status | Package |
|---|---|---|
| Transaction | Done | `com.mint.transaction` |
| Bank Connection & Sync | Done | `com.mint.service.bank` |
| Auth / Login | In progress | `com.mint.auth` |
| Budget Management | In progress | `com.mint.budget` |
| Dashboard | In progress | `com.mint.dashboard` |

---

## Tech Stack

- **Java 17**
- **Spring Boot 2.7.14** (Web, Data JPA, Security, Validation)
- **PostgreSQL 16** (via Docker)
- **Hibernate** (JPA provider)
- **Maven**

---

## Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven (or use the included `./mvnw` wrapper)

### 1. Start the database

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 container at `localhost:5432`.

| Setting | Value |
|---|---|
| Database | `mint_db` |
| Username | `mint_user` |
| Password | `mint_password` |

### 2. Run the application

```bash
./mvnw spring-boot:run
```

### 3. Test the API with Bruno

Import the Bruno collection from:

```text
/Users/zicctor/IdeaProjects/mint/bruno/mint-api
```

Use the local environment file at `bruno/mint-api/environments/local.bru` and set:

- `baseUrl` to your running app, usually `http://localhost:8080`
- `token` from the `/auth/login` response
- `userId`, `budgetId`, and `transactionId` from your seed data or earlier create/list responses

Note: `/auth/login` now returns structured JSON: { "token": "<jwt>", "requiresMfa": true|false } so Bruno (and other tools) can extract the token automatically.


## REST API

### Transactions — `/api/transactions`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/transactions` | List all transactions |
| `GET` | `/api/transactions/{id}` | Get transaction by ID |
| `POST` | `/api/transactions` | Create a transaction |
| `PUT` | `/api/transactions/{id}` | Update a transaction |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction |
| `GET` | `/api/transactions/type/{type}` | Filter by type (`INCOME`/`EXPENSE`) |
| `GET` | `/api/transactions/category/{category}` | Filter by category |

---

## Project Structure

```
src/main/java/com/mint/
├── MintApplication.java
├── auth/               # Module: Login & Auth (in progress)
├── bank/               # Module: Bank Connection & Sync
│   └── api/
├── budget/             # Module: Budget Management (in progress)
├── config/
├── dto/                # Shared DTOs (BankAccountDto, BankTransactionDto)
├── entity/             # JPA Entities (Bank, BankConnection, User)
├── exception/          # Custom exceptions
├── repository/         # Spring Data repositories
├── service/bank/       # Bank sync & connection services
└── transaction/        # Module: Transactions
```

---

## Design Patterns

- **Builder Pattern** — all entities and DTOs use an inner static `Builder` class for construction
- **Repository Pattern** — Spring Data JPA repositories for data access
- **Service Layer** — business logic decoupled from controllers
- **Interface Abstraction** — `BankApiClient` interface with `MockBankApiClient` implementation

---

## Environment Variables

See `.env` for database credentials used by Docker Compose.
See `POSTGRES_SETUP.md` for manual database setup instructions.
