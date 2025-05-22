# Platonus API Tracing Project

This project provides a full-stack solution for interacting with and extending the capabilities of the Platonus system (platonus.iitu). It features a robust back-end written in Kotlin using Spring Boot and Gradle, which wraps the original Platonus API via web scraping, and a modern front-end built with Vue.js.

## Features

- **User & Group Management:** Manage users and groups, including querying, rating, and filtering users.
- **Document Handling:** Generate, retrieve, and manage documents for users.
- **Authentication & Security:** JWT-based authentication and Spring Security integration.
- **API Gateway:** Microservice gateway management using Spring Cloud Gateway.
- **Microservices Architecture:** Separate services for authentication, document handling, chat, and API gateway.
- **Front-End:** A Vue.js client for interacting with the API.

## Project Structure

- `auth-api/` – Back-end authentication and user/group management service (Kotlin, Spring Boot)
- `docs-api/` – Document handling service (Kotlin, Spring Boot)
- `chat-api/` – Real-time chat microservice (Kotlin, Spring Boot WebSocket)
- `api-gateway/` – API gateway for routing and aggregation (Kotlin, Spring Cloud Gateway)
- `front-end/` – Vue.js front-end application

## Getting Started

### Prerequisites

- Java 8
- Node.js & npm (for the front-end)
- PostgreSQL (for persistence)
- Gradle (or use the included Gradle wrapper)
- Docker (optional, for containerization)

### Back-End Setup

Each service (`auth-api`, `docs-api`, `chat-api`, `api-gateway`) is a standalone Spring Boot application.
1. Navigate into a service directory (e.g., `auth-api/`).
2. Install dependencies and build:
   ```bash
   ./gradlew build
   ```
3. Run the service:
   ```bash
   ./gradlew bootRun
   ```

Configure each service using `application.properties` or environment variables as needed (database, ports, etc.).

### Front-End Setup

```bash
cd front-end
npm install
npm run serve
```

See [front-end/README.md](front-end/README.md) for more details.

## API Usage

- The back-end exposes REST APIs for authentication, user/group management, and document operations.
- The API gateway provides a single entry point for all microservices.

## Technologies Used

- **Back-End:** Kotlin, Spring Boot, Spring Data JPA, Spring Security, JWT, PostgreSQL, Eureka, OpenFeign, Swagger
- **Front-End:** Vue.js, Vue Router, Vuex

This project is for educational purposes.

---
