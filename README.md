# IronBus Train Seat Booking System

A segment-aware reserved-seat booking system for the Colombo Fort–Badulla railway line.

## Core idea

A physical seat may be reused by different passengers on non-overlapping journey segments.

Example:

- Passenger A: Colombo Fort → Kandy
- Passenger B: Kandy → Badulla

Both passengers may use the same seat because their occupied segments do not overlap.

## Technology stack

- Java 21
- Spring Boot
- PostgreSQL
- Flyway
- React
- TypeScript
- Docker Compose

## Running the application

1. Copy the environment template:

    ```bash
    cp .env.example .env
    ```

2. Start all services:

    ```bash
    docker compose up --build
    ```

3. Open:

    - Backend health: http://localhost:8080/actuator/health
    - Backend status: http://localhost:8080/api/v1/system/status 
