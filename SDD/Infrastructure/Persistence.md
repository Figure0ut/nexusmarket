# Infrastructure Specification: Polyglot Persistence Architecture

## 1. Architectural Strategy & Design Motivation

NexusMarket implements a **Polyglot Persistence** pattern following the **Hexagonal Architecture (Ports and Adapters)** paradigm. 

Instead of forcing all data into a single database paradigm, the storage engine is chosen based on the natural requirements of each domain aggregate:

| Storage Engine | Technology Stack | Aggregate / Responsibility | Rationale |
| :--- | :--- | :--- | :--- |
| **MongoDB** | Spring Data MongoDB / BSON | **Catalog Management** (`Product`, `ProductVariant`) | Flexible document schema capable of housing dynamic product variants, attributes, and categories without schema migrations. |
| **MongoDB** | Spring Data MongoDB / BSON | **Operation Audit Trail** (`AuditEntry`) | High-throughput, write-intensive, append-only operational log documents. |
| **SQL (Relational)** | Spring Data JPA / Hibernate / H2 | **Users & Actors** (`User`, `Buyer`, `Seller`) | Relational integrity, email uniqueness constraints, foreign key relationships. |
| **SQL (Relational)** | Spring Data JPA / Hibernate / H2 | **Warehousing & Inventory** (`Warehouse`, `Inventory`) | ACID transaction guarantees for stock reservations, preventing race conditions or overselling. |
| **SQL (Relational)** | Spring Data JPA / Hibernate / H2 | **Checkout & Invoicing** (`Order`, `OrderItem`, `Cart`, `Invoice`) | Relational tabular representation for financial transactions, billing records, and line items. |
| **SQL (Relational)** | Spring Data JPA / Hibernate / H2 | **Logistics & Returns** (`Shipment`, `ReturnRequest`, `Refund`) | Strong relational consistency tracking order fulfillments, deliveries, and return lifecycles. |

---

## 2. Hexagonal Boundary & Port Independence

The Domain layer (`application.domain`) remains **100% pure Java**. It defines domain repository port interfaces without any framework imports:

- `ProductRepositoryPort`
- `AuditLogRepositoryPort`
- `UserRepositoryPort`
- `WarehouseRepositoryPort`
- `InventoryRepositoryPort`
- `OrderRepositoryPort`
- `InvoiceRepositoryPort`
- `ShipmentRepositoryPort`
- `ReturnRepositoryPort`

The database-specific entities, documents, and repositories live exclusively in the infrastructure layer:

```
src/main/java/application/infrastructure/adapters/out/
├── mongo/
│   ├── config/
│   │   └── MongoDatabaseConfig.java               <-- Embedded in-memory MongoDB wire server + MongoClient
│   ├── document/
│   │   ├── ProductDocument.java                   <-- MongoDB @Document("products")
│   │   └── AuditLogDocument.java                  <-- MongoDB @Document("audit_logs")
│   ├── repository/
│   │   ├── SpringDataMongoProductRepository.java  <-- MongoRepository<ProductDocument, String>
│   │   └── SpringDataMongoAuditLogRepository.java <-- MongoRepository<AuditLogDocument, String>
│   ├── MongoProductRepositoryAdapter.java         <-- Implements ProductRepositoryPort (@Primary)
│   └── MongoAuditLogRepositoryAdapter.java        <-- Implements AuditLogRepositoryPort (@Primary)
│
└── sql/
    ├── config/
    │   └── JpaDatabaseConfig.java                 <-- @EnableJpaRepositories + @EntityScan
    ├── entity/
    │   ├── UserEntity.java                        <-- JPA @Entity @Table(name = "users")
    │   ├── WarehouseEntity.java                   <-- JPA @Entity @Table(name = "warehouses")
    │   ├── InventoryEntity.java                   <-- JPA @Entity @Table(name = "inventories")
    │   ├── OrderEntity.java                       <-- JPA @Entity @Table(name = "orders")
    │   ├── OrderItemEntity.java                   <-- JPA @Entity @Table(name = "order_items")
    │   ├── CartEntity.java                        <-- JPA @Entity @Table(name = "carts")
    │   ├── CartItemEntity.java                    <-- JPA @Entity @Table(name = "cart_items")
    │   ├── InvoiceEntity.java                     <-- JPA @Entity @Table(name = "invoices")
    │   ├── ShipmentEntity.java                    <-- JPA @Entity @Table(name = "shipments")
    │   ├── ReturnRequestEntity.java               <-- JPA @Entity @Table(name = "return_requests")
    │   └── RefundEntity.java                      <-- JPA @Entity @Table(name = "refunds")
    ├── repository/
    │   ├── SpringDataJpaUserRepository.java
    │   ├── SpringDataJpaWarehouseRepository.java
    │   ├── SpringDataJpaInventoryRepository.java
    │   ├── SpringDataJpaOrderRepository.java
    │   ├── SpringDataJpaCartRepository.java
    │   ├── SpringDataJpaInvoiceRepository.java
    │   ├── SpringDataJpaShipmentRepository.java
    │   ├── SpringDataJpaReturnRepository.java
    │   └── SpringDataJpaRefundRepository.java
    └── [Jpa*RepositoryAdapter.java]               <-- Implement Port interfaces (@Primary)
```

---

## 3. Zero-Configuration Embedded Environment

To allow the application to compile, execute, and be evaluated on any workstation without requiring external system dependencies, daemons, or Docker containers:

1. **Embedded MongoDB Wire Server (`de.bwaldvogel:mongo-java-server`)**:
   - Automatically spins up an in-memory BSON wire protocol server inside the JVM on a dynamically assigned free TCP port.
   - Spring Boot connects transparently via the standard `MongoClient` driver without external dependencies.
   - Can optionally connect to an external MongoDB instance simply by specifying `spring.data.mongodb.uri` in `application.properties`.

2. **Embedded SQL Database (H2)**:
   - Configured with `jdbc:h2:mem:nexusmarket;DB_CLOSE_DELAY=-1`.
   - Schema generation handled automatically via Hibernate DDL auto-generation.
   - H2 Console available at `/h2-console` for inspection.
