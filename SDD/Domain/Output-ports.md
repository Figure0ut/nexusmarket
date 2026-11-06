# Output Ports Specification

Output ports are the outbound contracts of the Hexagonal Architecture (`application.domain.ports.out`). The domain only knows these interfaces; the infrastructure provides the implementations.

| Port | Production adapter | Storage |
| :--- | :--- | :--- |
| `UserRepositoryPort` | `JpaUserRepositoryAdapter` | SQL |
| `WarehouseRepositoryPort` | `JpaWarehouseRepositoryAdapter` | SQL |
| `InventoryRepositoryPort` | `JpaInventoryRepositoryAdapter` | SQL |
| `OrderRepositoryPort` | `JpaOrderRepositoryAdapter` | SQL (orders + carts) |
| `InvoiceRepositoryPort` | `JpaInvoiceRepositoryAdapter` | SQL |
| `ShipmentRepositoryPort` | `JpaShipmentRepositoryAdapter` | SQL |
| `ReturnRepositoryPort` | `JpaReturnRepositoryAdapter` | SQL (returns + refunds) |
| `ProductRepositoryPort` | `MongoProductRepositoryAdapter` | MongoDB |
| `AuditLogRepositoryPort` | `MongoAuditLogRepositoryAdapter` | MongoDB |
| `PaymentGatewayPort` | `InMemoryPaymentGatewayAdapter` | Simulated external gateway |
| `NotificationPort` | `InMemoryNotificationAdapter` | Simulated e-mail (log) |

Unit tests use in-memory test doubles of every repository port (`src/test/java/.../adapters/out/memory`).

## Contract methods

### `AuditLogRepositoryPort`
- `void save(AuditEntry entry)`
- `List<AuditEntry> findAll()`
- `List<AuditEntry> findByTargetAggregateId(String aggregateId)`

### `InventoryRepositoryPort`
- `Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId)`
- `List<Inventory> findByProductId(String productId)`
- `List<Inventory> findByWarehouseId(String warehouseId)`
- `List<Inventory> findAll()`
- `Inventory save(Inventory inventory)`

### `InvoiceRepositoryPort`
- `Optional<Invoice> findById(String invoiceId)`
- `Optional<Invoice> findByOrderId(String orderId)`
- `List<Invoice> findAll()`
- `Invoice save(Invoice invoice)`

### `NotificationPort`
- `void sendEmailNotification(Email recipient, String subject, String body)`

### `OrderRepositoryPort`
- `Optional<Cart> findCartByBuyerId(String buyerId)`
- `Cart saveCart(Cart cart)`
- `Optional<Order> findOrderById(String orderId)`
- `List<Order> findOrdersByBuyerId(String buyerId)`
- `List<Order> findAllOrders()`
- `Order saveOrder(Order order)`

### `PaymentGatewayPort`
- `boolean processPayment(String orderId, Money amount, String paymentToken)`
- `boolean processRefund(String refundId, Money amount)`

### `ProductRepositoryPort`
- `Optional<Product> findById(String productId)`
- `Optional<Product> findBySku(SKU sku)`
- `List<Product> findBySellerId(String sellerId)`
- `List<Product> findAll()`
- `Product save(Product product)`

### `ReturnRepositoryPort`
- `Optional<ReturnRequest> findReturnById(String returnId)`
- `ReturnRequest saveReturn(ReturnRequest returnRequest)`
- `Optional<Refund> findRefundById(String refundId)`
- `Refund saveRefund(Refund refund)`
- `List<ReturnRequest> findAllReturns()`
- `List<Refund> findAllRefunds()`

### `ShipmentRepositoryPort`
- `Optional<Shipment> findById(String shipmentId)`
- `Optional<Shipment> findByOrderId(String orderId)`
- `List<Shipment> findAll()`
- `Shipment save(Shipment shipment)`

### `UserRepositoryPort`
- `Optional<User> findById(String identifier)`
- `Optional<User> findByEmail(Email email)`
- `User save(User user)`
- `boolean existsByEmail(Email email)`
- `boolean existsByIdentityDocument(String identityDocument)`
- `List<User> findAll()`

### `WarehouseRepositoryPort`
- `Optional<Warehouse> findById(String warehouseId)`
- `List<Warehouse> findByOwnerId(String ownerId)`
- `List<Warehouse> findAll()`
- `Warehouse save(Warehouse warehouse)`
