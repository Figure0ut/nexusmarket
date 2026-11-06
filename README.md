# NexusMarket - Centralized Digital Marketplace

REST API for the NexusMarket marketplace (Software Construction 2), built with **Java 17+, Spring Boot 3.2, Hexagonal Architecture (Ports & Adapters)** and **polyglot persistence (SQL + MongoDB)**.
The functional specification is implemented literally; see [SDD/Requirements-Traceability.md](SDD/Requirements-Traceability.md).

## Architecture

```
            ┌────────────────────────── INFRASTRUCTURE ──────────────────────────┐
  HTTP ───► │ Inbound adapters: REST controllers, DTOs, auth resolver, audit     │
            │        │                                                           │
            │        ▼                      ┌──────────── DOMAIN (pure Java) ──┐ │
            │  DomainServicesConfig ──────► │ models · value objects · enums   │ │
            │  (wires beans)                │ atomic domain services           │ │
            │                               │ output ports (interfaces)        │ │
            │                               └──────────────┬───────────────────┘ │
            │        ┌───────────── implements ────────────┘                     │
            │        ▼                                                           │
            │ Outbound adapters: JPA/H2 (SQL) · MongoDB · payment · notification │
            └────────────────────────────────────────────────────────────────────┘
```

- `application.domain` — business logic with **no framework dependencies** (no Spring/JPA annotations).
- `application.infrastructure` — Spring Boot: REST controllers (inbound adapters), SQL and MongoDB repositories (outbound adapters) and configuration.
- Dependencies always point inward: infrastructure depends on the domain, never the opposite.

| Storage | Data |
| :--- | :--- |
| SQL (H2 in-memory, JPA) | users, warehouses, inventory, carts, orders, invoices, shipments, returns, refunds |
| MongoDB (embedded server) | product catalog (flexible variants), audit trail |

## Run

```bash
./mvnw spring-boot:run          # API on http://localhost:8080
./mvnw test                     # 70 unit + integration tests
./test-api.sh                   # 42-step end-to-end demo (server must be running)
```

No external database is needed: H2 and an embedded MongoDB start inside the JVM (H2 console: `/h2-console`, JDBC URL `jdbc:h2:mem:nexusmarket`, user `sa`).
To use a real MongoDB set `spring.data.mongodb.uri` in `application.properties`.

## Using the API (Postman)

1. Import [`postman/NexusMarket.postman_collection.json`](postman/NexusMarket.postman_collection.json).
2. Run the folders in order on a freshly started server.
3. Every request sends `X-User-Email: <email of an ACTIVE user>` (RG-01). The initial Administrator is `admin@nexusmarket.com`.

| Role | How it is created |
| :--- | :--- |
| ADMIN | initial account at startup; more via `POST /api/users` |
| OPERATOR_LOGISTIC, SUPERVISOR | `POST /api/users` by an ADMIN |
| SELLER | `POST /api/sellers` by an ADMIN, together with its first warehouse |
| BUYER | self-registration `POST /api/users/register` |

## Documentation (`/SDD`)

- [Software Architecture](SDD/SoftwareArchitecture/SoftwareArchitecture.md)
- [Requirements Traceability](SDD/Requirements-Traceability.md)
- [Domain Model](SDD/Domain/Domain%20Model.md) · [Value Objects & Enums](SDD/Domain/Domain%20Value%20Objects.md) · [Domain Services](SDD/Domain/Domain%20Services.md) · [Output Ports](SDD/Domain/Output-ports.md)
- [REST API](SDD/Infrastructure/RestApi.md) · [Persistence](SDD/Infrastructure/Persistence.md)
