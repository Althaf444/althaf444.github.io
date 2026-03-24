# 🐘 Mint Project - PostgreSQL Setup (Docker + IntelliJ)

This project uses **PostgreSQL inside Docker** for local development.

Each teammate runs their own local database container to ensure
consistency and avoid conflicts.

------------------------------------------------------------------------

## ✅ 1. Requirements

Make sure you have installed:

-   Docker Desktop
-   IntelliJ IDEA
-   Java (correct version for the project)

Check Docker installation:

    docker --version

------------------------------------------------------------------------

## ✅ 2. Start PostgreSQL

From the project root (same folder as `pom.xml` and
`docker-compose.yml`):

    docker compose up -d

Check if it's running:

    docker ps

You should see a container like:

    mint-postgres

------------------------------------------------------------------------

## ✅ 3. Database Configuration

PostgreSQL runs with the following configuration:

Property   Value
  ---------- ---------------
Host       localhost
Port       5432
Database   mint_db
Username   mint_user
Password   mint_password

------------------------------------------------------------------------

## ✅ 4. Connect in IntelliJ

1.  Open IntelliJ\
2.  Go to **View → Tool Windows → Database**\
3.  Click **+ → Data Source → PostgreSQL**\
4.  Enter the following:

```
    Host: localhost
    Port: 5432
    Database: mint_db
    User: mint_user
    Password: mint_password
```
5.  Click **Download missing drivers** (if prompted)\
6.  Click **Test Connection**\
7.  Click **OK**

------------------------------------------------------------------------
## 🚀 First-Time Setup Summary

After cloning the project:

    git pull
    docker compose up -d

Then run the Spring Boot application normally in IntelliJ.

------------------------------------------------------------------------

You're ready to start developing 🎉
