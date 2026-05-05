# Backend Architecture

## Tech Stack
- **Framework**: Spring Boot 4.x (Java 21)
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT
- **Caching/PubSub**: Redis
- **Utilities**: Lombok, Spotless (formatting)

## Key Dependencies
- `jjwt` for JWT handling
- `zxing` for QR code generation
- `pdfbox` for PDF manipulation
- `thumbnailator` & `twelvemonkeys` for image processing
- `spring-boot-starter-websocket` for WebSockets

## Guidelines
- Follow Spring Boot standard architectural patterns (Controller, Service, Repository)
- Ensure all endpoints are secured appropriately
- Use Lombok to reduce boilerplate
- Follow formatting guidelines enforced by Spotless

