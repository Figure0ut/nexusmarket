# Software Architecture Specification: NexusMarket

## 1. Architectural Vision & Fundamentals

**NexusMarket** is an enterprise-grade digital marketplace platform designed following **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)** principles.

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    INFRASTRUCTURE LAYER                                         │
│   (Web REST Controllers, Persistence JPA/Mongo Adapters, Payment Adapters, Spring Framework)    │
└───────────────────────────────────────────────┬─────────────────────────────────────────────────┘
                                                │
                                                ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                     APPLICATION LAYER                                           │
│   (Use Case Interactors, Application Services, Application DTOs, Inbound/Outbound Ports)        │
└───────────────────────────────────────────────┬─────────────────────────────────────────────────┘
                                                │
                                                ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                       DOMAIN LAYER                                              │
│   (Pure Java Models, Value Objects, Enums, Exceptions, Outbound Ports, Domain Services)         │
└─────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Package Organization & Domain Structure

All domain components reside under package `application.domain` structured strictly into the following 6 directories:

```
src/main/java/application/domain/
├── enums/          <-- Domain Enums (UserRole, UserStatus, WarehouseType, ProductType, etc.)
├── exceptions/     <-- Domain Exceptions (DomainException, EntityNotFoundException, etc.)
├── models/         <-- Domain Entities & Aggregates with Getters & Setters (User, Buyer, Seller, Product, Order, etc.)
├── ports/out/      <-- Outbound Port Contracts (UserRepositoryPort, ProductRepositoryPort, etc.)
├── services/       <-- Domain Services (UserAuthenticationService, SellerIncorporationService, etc.)
└── valueobjects/   <-- Non-Enum Value Objects with Getters & Setters (Money, Address, Email, SKU, TaxIdentifier, StockQuantity, ProductVariant)
```

---

## 3. Layer Isolation Rules & Dependencies

- **Pure Java Domain**: The `application.domain` package contains **100% pure Java**. Absolutely NO Spring (`@Service`, `@Component`), JPA (`@Entity`, `@Table`), Jackson, or Lombok annotations are permitted inside the domain layer.
- **Getters & Setters**: Models and Value Objects encapsulate properties with standard Java `getFoo()` and `setFoo()` mutators while enforcing domain invariant validations inside setters.
- **Dependency Rule**: Outer layers depend inward on `application.domain`. The domain layer has zero outward framework dependencies.
