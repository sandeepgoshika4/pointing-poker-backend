# Pointing Poker - Backend

Spring Boot backend for the Pointing Poker application.

## Overview

This repository contains the backend API implemented with Spring Boot and Maven. It provides REST endpoints for creating sessions, voting, and retrieving results.

## Prerequisites

- Java 11 or later (Java 17 recommended)
- Maven 3.6+
- (Optional) Docker if you want to build/run a container

## Build

From the project root:

- Download dependencies and build:
  mvn clean install

- Build without running tests:
  mvn clean package -DskipTests

## Run

Run directly with Maven:

- mvn spring-boot:run

Run the packaged jar:

- java -jar target/<artifactId>-<version>.jar
  (replace `<artifactId>-<version>.jar` with the actual jar name found in `target/`)

To run with a specific Spring profile:

- mvn spring-boot:run -Dspring-boot.run.profiles=dev
- or: java -jar target/...jar --spring.profiles.active=prod

## Tests

Run unit and integration tests:

- mvn test

## Docker (optional)

To build a Docker image (if Dockerfile exists in the repo):

- docker build -t pointing-poker-backend:latest .
- docker run -e SPRING_DATASOURCE_URL=... -p 8080:8080 pointing-poker-backend:latest

## API

Document endpoints here or link to API docs (Swagger/OpenAPI) if available, e.g.: 

- GET /api/session
- POST /api/session
- POST /api/session/{id}/vote
- GET /api/session/{id}/results

(Add more details or examples as appropriate for your API.)

## Frontend

If there is a separate frontend repository, manage frontend build there (npm/yarn). This backend README should not instruct running `npm install` — that belongs to the frontend repo.

## Contributing

- Create a branch for your change: git checkout -b feat/your-feature
- Open a pull request with a clear description of the change

## License

Specify your project license here (e.g., MIT).
