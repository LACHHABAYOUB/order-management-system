# Order Management System – Full Stack Project

A production-ready **Order Management System** built with **Spring Boot 3 (Java 17)** and a modern **React + TypeScript** frontend.

This project demonstrates real-world backend engineering practices: clean architecture, security, database migrations, audit logging, idempotency, integration testing, and Dockerized deployment.


<img width="1024" height="1536" alt="Architecture" src="https://github.com/user-attachments/assets/52a2b29c-c7c7-4404-8cfe-788f2eb506ca" />

---

## 🧱 Architecture Overview

```
root
├── backend
│   └── order-service
│       ├── api        # REST controllers & DTOs
│       ├── service    # Business logic
│       ├── domain     # JPA entities & enums
│       ├── repo       # Spring Data repositories
│       ├── error      # Global error handling
│       └── config     # Security & configuration
├── frontend
│   └── React + TypeScript (Vite)
├── infra
│   └── docker-compose
└── docs
```

---

## 🚀 Tech Stack

### Backend
- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security (API Key)
- PostgreSQL
- Flyway
- Docker & Docker Compose
- OpenAPI / Swagger
- Testcontainers

### Frontend
- React
- TypeScript
- Vite
- Material UI

---

## ✨ Features

- Order lifecycle management
- Status transition validation
- Audit trail
- Pagination & filtering
- Idempotent creation
- API Key security
- Integration tests
- Dockerized setup

---

## 🔐 Security

All protected endpoints require:

```
X-API-KEY: secret-api-key
```

---

## 🐳 Run with Docker

```bash
docker compose up -d --build
```

Backend:
http://localhost:8081

Swagger:
http://localhost:8081/swagger-ui/index.html

---

## 🧪 Testing

```bash
cd backend/order-service
mvn test
```

---

## 📬 API Examples

### Create Order
```http
POST /api/v1/orders
X-API-KEY: secret-api-key
Content-Type: application/json

{
  "customerName": "Ayoub"
}
```

---

## 🧪 Postman Collection

A ready-to-use Postman collection is available in:

docs/postman/Order-Service.postman_collection.json

---

## 👨‍💻 Author

Ayoub Lachhab

Senior Software Engineer
