# Mint - PostgreSQL Setup Guide

## Prerequisites
- Docker and Docker Compose installed
- Java 17+
- Maven 3.8+

## Quick Start

### 1. Start PostgreSQL with Docker Compose

```bash
docker-compose up -d
```

This will:
- Start a PostgreSQL container named `mint_postgres`
- Create a database named `mintdb`
- Set credentials: username `postgres`, password `postgres`
- Listen on `localhost:5432`

### 2. Build the Application

```bash
mvn clean package
```

### 3. Run the Application

```bash
java -jar target/mint-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### 4. Access the Application

- Application: `http://localhost:8080`
- PostgreSQL connection: `localhost:5432`

## Database Configuration

Database credentials can be modified in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mintdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Useful Docker Commands

```bash
# Check PostgreSQL status
docker-compose ps

# View PostgreSQL logs
docker-compose logs postgres

# Stop PostgreSQL
docker-compose down

# Stop and remove data (reset database)
docker-compose down -v

# Connect to PostgreSQL directly
docker exec -it mint_postgres psql -U postgres -d mintdb
```

## Troubleshooting

**Cannot connect to PostgreSQL:**
- Ensure Docker daemon is running
- Check if port 5432 is not already in use
- Run `docker-compose ps` to verify container is running

**Database reset needed:**
```bash
docker-compose down -v  # Remove volume to reset all data
docker-compose up -d    # Start fresh
```

