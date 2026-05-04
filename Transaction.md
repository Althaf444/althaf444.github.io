# Transaction Module Summary

## Overview
The Transaction module is a core component of the Mint application, responsible for managing financial transactions including income and expenses. It provides a complete CRUD (Create, Read, Update, Delete) API for transaction management with filtering capabilities.

## Components

### Transaction Entity
The `Transaction` class represents a financial transaction in the system.

**Fields:**
- `id`: Unique identifier (Long, auto-generated)
- `amount`: Transaction amount (BigDecimal, required)
- `date`: Transaction date (LocalDate, required)
- `category`: Transaction category (String, required)
- `type`: Transaction type (TransactionType enum, required)

**Annotations:**
- `@Entity` - JPA entity
- `@Table(name = "transactions")` - Database table mapping
- Lombok annotations for getters, setters, constructors, and builders

### TransactionType Enum
Defines the type of transaction.

**Values:**
- `INCOME`: Represents income transactions
- `EXPENSE`: Represents expense transactions

### TransactionRepository
JPA repository interface for data access operations.

**Methods:**
- `findByType(TransactionType type)`: Find transactions by type
- `findByCategory(String category)`: Find transactions by category
- `findByDateBetween(LocalDate from, LocalDate to)`: Find transactions within a date range
- `findByTypeAndDateBetween(TransactionType type, LocalDate from, LocalDate to)`: Find transactions by type within a date range

**Inheritance:** Extends `JpaRepository<Transaction, Long>`

### TransactionService
Business logic layer for transaction operations.

**Methods:**
- `getAllTransactions()`: Retrieve all transactions
- `getTransactionById(Long id)`: Retrieve a specific transaction by ID (throws RuntimeException if not found)
- `createTransaction(Transaction transaction)`: Create a new transaction
- `updateTransaction(Long id, Transaction transaction)`: Update an existing transaction
- `deleteTransaction(Long id)`: Delete a transaction by ID
- `getByType(TransactionType type)`: Get transactions filtered by type
- `getByCategory(String category)`: Get transactions filtered by category

### TransactionController
REST API controller providing HTTP endpoints for transaction management.

**Base Path:** `/api/transactions`

**Endpoints:**
- `GET /api/transactions` - Get all transactions
- `GET /api/transactions/{id}` - Get transaction by ID
- `POST /api/transactions` - Create a new transaction
- `PUT /api/transactions/{id}` - Update an existing transaction
- `DELETE /api/transactions/{id}` - Delete a transaction
- `GET /api/transactions/type/{type}` - Get transactions by type
- `GET /api/transactions/category/{category}` - Get transactions by category

**Response Format:** JSON
**HTTP Status Codes:** 200 OK, 204 No Content, 404 Not Found (implied through exceptions)

## Data Flow
1. Client sends HTTP request to TransactionController
2. Controller delegates to TransactionService
3. Service performs business logic and calls TransactionRepository
4. Repository executes database operations
5. Data flows back through the layers to the client

## Dependencies
- Spring Boot Web
- Spring Data JPA
- Lombok
- PostgreSQL (production database)
- H2 (for testing, if applicable)

## Usage Example
```json
POST /api/transactions
{
  "amount": 100.00,
  "date": "2023-05-04",
  "category": "Food",
  "type": "EXPENSE"
}
```

This creates a new expense transaction for $100 in the Food category on May 4, 2023.
