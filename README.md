# Task Manager API (v0.1)

A robust RESTful API for task management, built with pure Java 21 and Jakarta Servlets to demonstrate core backend principles and manual infrastructure management.

## 🚀 Core Features (v0.1)
- **User Management**: Registration, session-based authentication, and user profiles.
- **Task CRUD**: Full lifecycle management for tasks (Create, Read, Update, Soft-Delete).
- **Soft Delete**: Entities are marked as deleted instead of being physically removed (using `is_deleted` and `deleted_at`).
- **Advanced Pagination & Sorting**: Generic pagination support with custom sorting logic (e.g., status priority sorting).

## 🛠 Technical Stack
- **Java 21 / Jakarta Servlet API**
- **PostgreSQL** — Main relational database.
- **HikariCP** — High-performance connection pooling.
- **Jackson** — JSON serialization/deserialization.
- **Logback** — Structured logging.
- **Session-based Security**: Cookie-based authentication and secure session management.

## 🏗 Architectural Highlights
- **Manual Transaction Management**: Handled at the Service layer for ACID compliance.
- **Dependency Injection**: Implemented manually via `ServletContextListener` and constructor injection.
- **Centralized Error Handling**: Global `ExceptionHandlingFilter` to catch and process business and system exceptions.
- **Generic PageResponse**: A reusable wrapper for all paginated API responses.

---
*Roadmap for v0.2: Projects, Shared Workspaces, Dockerization & Kafka Notifications.*