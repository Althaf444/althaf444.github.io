# Changelog

All notable changes to this project are documented here, in reverse chronological order.

---

## [Unreleased]

---

## 2026-03-30 — Builder Pattern refactor (`8d7b505`)

**Commit:** `Refine bank sync models and mock API fixtures`

Applied the **Builder Pattern** across all entity and DTO classes to replace telescoping constructors and raw setter chains.

### Changed
- `entity/Bank.java` — Added inner static `Builder` class; added `protected` no-arg constructor for JPA
- `entity/BankConnection.java` — Added inner static `Builder` class with all 12 fields; added `protected` no-arg constructor for JPA
- `entity/User.java` — Added inner static `Builder` class; added `protected` no-arg constructor for JPA
- `dto/BankAccountDto.java` — Replaced all-args telescoping constructor with inner static `Builder`
- `dto/BankTransactionDto.java` — Replaced all-args telescoping constructor with inner static `Builder`
- `transaction/Transaction.java` — Added Lombok `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- `service/bank/BankConnectionService.java` — Updated `createConnection()` to use `BankConnection.builder()` instead of setter chain
- `service/bank/api/MockBankApiClient.java` — Updated `fetchAccount()` and `generateMockTransactions()` to use DTO builders
- `test/util/TestDataBuilder.java` — Updated all build methods to use entity builders

---

## 2026-03-30 — Cleanup (`2b57489`)

**Commit:** `Deleted MIGRATION_COMPLETE.md`

### Removed
- `MIGRATION_COMPLETE.md` — temporary migration notes file deleted after completion

---

## 2026-03-26 — Merge: Transaction module (`9d3eea4`)

**Commit:** `Merge branch 'feature/transaction'`

Merged the completed transaction feature branch into main.

---

## 2026-03-26 — Merge: Bank sync module (`0faac12`)

**Commit:** `Merged PR 1: Merge pull request #1 from feature/bank-sync`

Merged the completed bank sync feature branch into main via pull request.

---

## 2026-03-26 — Bank Connection & Sync module (`ee1fe34`)

**Commit:** `Implement bank connection sync flow with PostgreSQL-backed tests`

Full implementation of the bank connection and synchronization module.

### Added
- `dto/BankAccountDto.java` — DTO for account data returned by bank API
- `dto/BankTransactionDto.java` — DTO for transaction data returned by bank API
- `entity/ConnectionStatus.java` — Enum: `PENDING`, `LINKED`, `FAILED`, `EXPIRED`, `SYNCING`
- `entity/SyncStatus.java` — Enum: `IDLE`, `SYNCING`, `SUCCESS`, `FAILED`
- `exception/BankConnectionException.java` — thrown on invalid/missing connections
- `exception/BankSyncException.java` — thrown on sync failures
- `exception/ConnectionTimeoutException.java` — thrown on network timeouts
- `exception/RateLimitException.java` — thrown when bank API rate limit is exceeded
- `repository/BankConnectionRepository.java` — JPA repository with custom queries
- `repository/BankRepository.java` — JPA repository for Bank entity
- `service/bank/BankConnectionService.java` — CRUD and state management for bank connections
- `service/bank/BankSyncService.java` — orchestrates sync flow with retry/backoff logic
- `service/bank/api/BankApiClient.java` — interface abstracting the bank API
- `service/bank/api/MockBankApiClient.java` — mock implementation with simulated failures (timeouts, rate limits, connection errors)
- `test/integration/BankConnectionIntegrationTest.java` — integration tests against real PostgreSQL
- `test/service/bank/BankConnectionServiceTest.java` — unit tests for connection service
- `test/service/bank/BankSyncServiceTest.java` — unit tests for sync service
- `test/service/bank/api/MockBankApiClientTest.java` — tests for mock API client
- `test/util/TestDataBuilder.java` — utility class for building test fixtures
- `test/resources/application-test.properties` — test profile pointing to `mint_test` database

### Changed
- `entity/BankConnection.java` — added `syncStatus`, `syncErrorMessage`, `lastSyncAttempt`, `syncFailureCount` fields
- `entity/User.java` — added `bankConnections` one-to-many relationship

---

## 2026-03-25 — Transaction module (`9cd8c10`)

**Commit:** `Add transaction module tests and PostgreSQL Docker setup`

Full implementation of the transaction module with REST API and tests.

### Added
- `transaction/Transaction.java` — JPA entity: `amount`, `date`, `category`, `type`
- `transaction/TransactionType.java` — Enum: `INCOME`, `EXPENSE`
- `transaction/TransactionRepository.java` — JPA repository with `findByType` and `findByCategory`
- `transaction/TransactionService.java` — CRUD operations and filtering logic
- `transaction/TransactionController.java` — REST endpoints at `/api/transactions`
- `transaction/SecurityConfig.java` — Spring Security config (all requests permitted for now)
- `POSTGRES_SETUP.md` — manual PostgreSQL setup guide
- `docker-compose.yml` — PostgreSQL 16 container definition
- `test/transaction/TransactionControllerTest.java`
- `test/transaction/TransactionRepositoryTest.java`
- `test/transaction/TransactionServiceTest.java`

### Changed
- `pom.xml` — added PostgreSQL driver, H2 test dependency, Lombok
- `resources/application.properties` — configured PostgreSQL datasource
- `test/MintApplicationTests.java` — updated for Spring context load test

---

## 2026-03-25 — Bank sync entities (`7f9caca`)

**Commit:** `Add bank sync entities`

### Added
- `entity/Bank.java` — Bank entity: `id`, `name`, `apiEndpoint`, `connections`
- `entity/BankConnection.java` — BankConnection entity with user/bank relationships and connection status
- `entity/User.java` — User entity: `id`, `username`, `email`, `password`
- `.vscode/settings.json` — VS Code project settings

---

## 2026-03-24 — Docker setup (`5b02c86`, `bafb9bc`)

**Commits:** `Add Docker compose file and Docker instruction`

### Added
- `PostgresSQL.md` — guide for running PostgreSQL via Docker with IntelliJ
- `docker-compose.yml` — initial Docker Compose file
- `.env` — environment variables for Docker Compose (`mint_user`, `mint_password`, `mint_db`)

### Changed
- `.gitignore` — updated ignore rules

---

## 2026-03-24 — Initial project setup (`658e490`)

**Commit:** `Initial Project Setup`

Spring Boot project scaffolded with Maven.

### Added
- `MintApplication.java` — application entry point
- `pom.xml` — Maven POM with Spring Boot parent and core dependencies
- `resources/application.properties` — default application configuration
- `HELP.md` — Spring Boot reference links
- `mvnw` / `mvnw.cmd` — Maven wrapper scripts
- `.gitignore`, `.gitattributes` — Git configuration
