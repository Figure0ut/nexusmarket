# Software Architecture Specification: NexusMarket

## 1. Architectural Vision & Fundamentals

**NexusMarket** is an enterprise-grade digital marketplace platform designed following **Hexagonal Architecture (Ports and Adapters)** and **Domain-Driven Design (DDD)** principles.

The architectural objective is to establish an uncompromised separation between core business logic and external infrastructure, frameworks, databases, and user interfaces.

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    INFRASTRUCTURE LAYER                                         │
│   (Web REST Controllers, Persistence JPA/Mongo Adapters, Payment Adapters, Spring Framework)    │
└───────────────────────────────────────────────┬─────────────────────────────────────────────────┘
                                                │ (DIP Dependency Inversion)
                                                ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                     APPLICATION LAYER                                           │
│   (Use Case Interactors, Application Services, Application DTOs, Inbound/Outbound Ports)        │
└───────────────────────────────────────────────┬─────────────────────────────────────────────────┘
                                                │
                                                ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                       DOMAIN LAYER                                              │
│   (Pure Java Aggregate Roots, Entities, Value Objects, Domain Services, Invariants, Enums)      │
└─────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Layer Isolation Rules & Dependencies

### A. Pure Java Domain Layer (`com.nexusmarket.domain.*`)
- **Zero Framework Annotations**: Contains **100% pure Java**. Absolutely NO Spring (`@Service`, `@Component`, `@Autowired`), JPA (`@Entity`, `@Table`, `@Id`), Jackson, or Lombok annotations are permitted inside the domain layer.
- **Self-Contained Invariants**: Business invariants are enforced upon object creation and state transition.
- **Dependency Rule**: The domain layer has zero outward dependencies. It depends only on standard Java standard library (`java.lang`, `java.util`, `java.math`).

### B. Application Layer (`com.nexusmarket.application.*`)
- Contains use case orchestrators, application services, and port interface contracts.
- Coordinates domain entities and domain services to fulfill business transactions.

### C. Infrastructure Layer (`com.nexusmarket.infrastructure.*`)
- Implements outbound ports (e.g. database repositories, payment gateway integrations) and provides inbound entry adapters (REST controllers, event listeners).
- Framework dependencies (Spring Boot, Hibernate, Jackson) are strictly isolated within this layer.

---

## 3. Package Organization & Bounded Contexts

The codebase is structured under `com.nexusmarket.domain.model` into 8 explicit **Bounded Contexts**:

```
com.nexusmarket.domain.model
├── common         <-- Shared Immutable Value Objects (Money, Address, Email, SKU, TaxIdentifier)
├── user           <-- Users, Buyers, Sellers, UserRole, UserStatus
├── warehouse      <-- Warehouses, WarehouseType
├── catalog        <-- Products, ProductVariants, ProductType, ProductStatus
├── inventory      <-- Distributed Inventory, StockQuantity, Movement Types
├── order          <-- Shopping Carts, Orders, Line Items, OrderStatus State Machine
├── billing        <-- Invoices, InvoiceStatus, Tax Details
├── logistics      <-- Shipments, Carriers, Tracking, ShipmentStatus
└── returns        <-- Return Requests, Return Reasons, Inspection, Refunds
```

---

## 4. Invariant Enforcement & Constructor Design Strategy

### A. Rich Constructor Rule
- **No Default (Empty) Constructors**: Entities and Value Objects **never** expose zero-argument empty constructors.
- **Fail-Fast Validation**: All constructor parameters are validated immediately. If an invariant is violated (e.g. null/empty string, negative money, invalid email, zero quantity), constructor execution fails fast by throwing `IllegalArgumentException`.

### B. Business-Intention Methods Rule
- **No Generic Setters**: Standard `setFoo()` mutators are forbidden on domain entities.
- **Explicit Domain Intent**: State transitions occur exclusively through domain methods named after business actions (e.g., `user.changeStatus()`, `seller.incorporate(adminUser)`, `inventory.reserveStock(qty)`, `order.markAsPaid()`, `shipment.dispatch(carrier, tracking)`).

### C. Value Object Immutability Rule
- Value Objects (`Money`, `Address`, `Email`, `SKU`, `TaxIdentifier`, `StockQuantity`, `ProductVariant`) are declared `final` with `private final` fields.
- Value Objects provide structural equality via `equals()` and `hashCode()`.
- Operations on Value Objects (e.g. `money.add()`, `stock.subtract()`) return a *new instance* without mutating existing state.

---

## 5. Domain Exception & Business Error Strategy

Business validation failures in the domain layer throw standard Java runtime exceptions with clear contextual error messages:
- **`IllegalArgumentException`**: Thrown when invalid data is supplied to a constructor or method (e.g. null reference, negative stock quantity, malformed email).
- **`IllegalStateException`**: Thrown when an operation is executed in an invalid domain state (e.g. buyer attempting administrative action, non-admin incorporating seller, modifying a finalized order, reserving stock exceeding available inventory).
