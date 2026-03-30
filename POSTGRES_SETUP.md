# PostgreSQL Setup — Mint Project

This project uses PostgreSQL inside Docker for local development.
Each teammate runs their own local container to ensure consistency.

---

## Requirements

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Java 17+
- IntelliJ IDEA (or any IDE)

Verify Docker is installed:

```bash
docker --version
```

---

## 1. Start the database

From the project root (same folder as `docker-compose.yml`):

```bash
docker compose up -d
```

Verify the container is running:

```bash
docker ps
```

You should see a container named `mint-postgres`.

---

## 2. Connection details

| Property | Value |
|---|---|
| Host | `localhost` |
| Port | `5432` |
| Database | `mint_db` |
| Username | `mint_user` |
| Password | `mint_password` |
| Test database | `mint_test` |

---

## 3. Connect in IntelliJ

1. Go to **View → Tool Windows → Database**
2. Click **+ → Data Source → PostgreSQL**
3. Enter the connection details from the table above
4. Click **Download missing drivers** if prompted
5. Click **Test Connection**, then **OK**

---

## 4. Run the application

```bash
./mvnw spring-boot:run
```

The app starts at `http://localhost:8080`.

---

## 5. Run tests

Tests connect to a separate `mint_test` database (configured in `src/test/resources/application-test.properties`). Make sure the Docker container is running before executing tests.

```bash
./mvnw test
```

---

## Useful Docker commands

```bash
# Check container status
docker compose ps

# View logs
docker compose logs postgres

# Stop the container
docker compose down

# Stop and wipe all data (full reset)
docker compose down -v
docker compose up -d

# Open a PostgreSQL shell
docker exec -it mint-postgres psql -U mint_user -d mint_db
```

---

## Troubleshooting

**Cannot connect to PostgreSQL**
- Ensure Docker Desktop is running
- Check port 5432 is not already in use: `lsof -i :5432`
- Run `docker compose ps` to verify the container status

**Application fails to start**
- Confirm credentials in `src/main/resources/application.properties` match the table above
- Try a full reset: `docker compose down -v && docker compose up -d`
