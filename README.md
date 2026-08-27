# NexusMarket - Centralized Digital Marketplace

NexusMarket is an enterprise-grade digital marketplace platform built using Java, Spring Boot, Hexagonal Architecture (Ports and Adapters), and Spec-Driven Development (SDD) following strict Domain-Driven Design (DDD) principles.

## 🏛️ Architecture Overview

The system is structured using **Hexagonal Architecture** to cleanly decouple core domain logic from external frameworks, databases, and UI adapters:

```
                  ┌───────────────────────────────────────────┐
                  │               Infrastructure              │
                  │   (REST Controllers, Database Adapters)  │
                  └─────────────────────┬─────────────────────┘
                                        │
                                        ▼
                  ┌───────────────────────────────────────────┐
                  │                Application                │
                  │        (Use Cases, Application Ports)     │
                  └─────────────────────┬─────────────────────┘
                                        │
                                        ▼
                  ┌───────────────────────────────────────────┐
                  │                  Domain                   │
                  │  (Pure Java Aggregates, Entities, VOs)    │
                  └───────────────────────────────────────────┘
```

- **Domain Layer (`domain`)**: Pure Java layer containing business models, entities, value objects, and domain logic. Fully isolated without any framework annotations (e.g., Spring, JPA).
- **Application Layer (`application`)**: Handles application use cases, orchestration, and interface ports.
- **Infrastructure Layer (`infrastructure`)**: Contains web adapters (REST Controllers), database persistence adapters (JPA/MongoDB), and Spring configuration.

## 📁 Repository Structure

```
nexusmarket/
├── SDD/                                   # Spec-Driven Development documentation
│   ├── Software_Architecture/             # Architectural specifications & decision records
│   └── Domain/                            # Domain specifications and bounded contexts
│       └── Users_Buyers_Sellers_Domain.md # Users, Buyers & Sellers domain spec
├── src/main/java/com/nexusmarket/
│   ├── NexusMarketApplication.java        # Spring Boot entry point
│   ├── domain/                            # Pure Java Domain model (Users, Buyers, Sellers)
│   ├── application/                       # Application services and use cases
│   └── infrastructure/                    # Adapters, persistence, controllers & config
└── pom.xml                                # Maven dependencies and build setup
```

## 🚀 Getting Started

### Prerequisites
- **Java**: 17 or higher
- **Maven**: 3.8+ (or bundled `./mvnw`)

### Build and Run

```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Launch application
./mvnw spring-boot:run
```

## 📖 SDD Documentation
Domain and architectural specifications can be found under the [/SDD](./SDD) directory.
