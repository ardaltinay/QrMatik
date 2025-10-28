QrMatik - Server

This is a minimal Spring Boot backend skeleton for the QrMatik client demo.
It exposes a simple REST API for orders and uses Spring Data JPA with MySQL.

How to run

1. Install Java 17 (or later) and Maven.
2. Configure MySQL and update `src/main/resources/application.properties` with your DB credentials.
3. From the `server/` directory run:

    mvn spring-boot:run

Endpoints

- GET /api/orders - list orders
- GET /api/orders/{id} - get order
- POST /api/orders - create order
- PUT /api/orders/{id}/status - update status (body: {"status":"PREPARING"})

Notes

- This skeleton uses Lombok; your IDE should have Lombok support enabled.
- For production you should harden CORS, security, and consider connection pooling and migrations (Flyway/Liquibase).
