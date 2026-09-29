# Fitness Web Backend

Spring Boot multi-module backend for the fitness application. The root `pom.xml` builds `common`, `user-service`, `activity-service`, and `server-eureka` together.

## Local setup

1. Use Java 21 and run `mvn test` from this directory.
2. Copy `user-service/src/main/resources/application.example.yaml` to `application.yaml` in the same directory and set `DB_PASSWORD` for PostgreSQL. For Docker Compose, set `POSTGRES_PASSWORD` to the same value before running `docker compose -f user-service/docker-compose.yml up -d`.
3. Copy `activity-service/src/main/resources/application.example.yaml` to `application.yaml` in the same directory and set `MONGODB_URI` for MongoDB Atlas.
4. Start `server-eureka` on port 8761, then `user-service` on 8081 and `activity-service` on 8082.

Local credentials and `application.yaml` files are ignored by Git.
