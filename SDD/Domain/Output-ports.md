# Comprehensive Output Ports Specification

Output Ports define outbound contract interfaces in Hexagonal Architecture, located in package `application.domain.ports.out`.

---

## Package: `application.domain.ports.out`

```
[ Domain Services / Use Cases ] ──> ( Output Port Interface ) ──> [ Infrastructure Adapter Implementation ]
```

### 1. `UserRepositoryPort`
- **Purpose**: Persistence interface for `User`, `Buyer`, and `Seller` models.
- **Contract Methods**:
  - `Optional<User> findById(String identifier)`
  - `Optional<User> findByEmail(Email email)`
  - `User save(User user)`
  - `boolean existsByEmail(Email email)`

### 2. `WarehouseRepositoryPort`
- **Purpose**: Persistence interface for `Warehouse` models.
- **Contract Methods**:
  - `Optional<Warehouse> findById(String warehouseId)`
  - `List<Warehouse> findByOwnerId(String ownerId)`
  - `Warehouse save(Warehouse warehouse)`

### 3. `ProductRepositoryPort`
- **Purpose**: Persistence interface for `Product` catalog models.
- **Contract Methods**:
  - `Optional<Product> findById(String productId)`
  - `Optional<Product> findBySku(SKU sku)`
  - `List<Product> findBySellerId(String sellerId)`
  - `Product save(Product product)`

### 4. `InventoryRepositoryPort`
- **Purpose**: Persistence interface for distributed `Inventory` models.
- **Contract Methods**:
  - `Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId)`
  - `List<Inventory> findByProductId(String productId)`
  - `Inventory save(Inventory inventory)`

### 5. `OrderRepositoryPort`
- **Purpose**: Persistence interface for `Cart` and `Order` models.
- **Contract Methods**:
  - `Optional<Cart> findCartByBuyerId(String buyerId)`
  - `Cart saveCart(Cart cart)`
  - `Optional<Order> findOrderById(String orderId)`
  - `List<Order> findOrdersByBuyerId(String buyerId)`
  - `Order saveOrder(Order order)`

### 6. `InvoiceRepositoryPort`
- **Purpose**: Persistence interface for `Invoice` models.
- **Contract Methods**:
  - `Optional<Invoice> findById(String invoiceId)`
  - `Optional<Invoice> findByOrderId(String orderId)`
  - `Invoice save(Invoice invoice)`

### 7. `ShipmentRepositoryPort`
- **Purpose**: Persistence interface for logistics `Shipment` models.
- **Contract Methods**:
  - `Optional<Shipment> findById(String shipmentId)`
  - `Optional<Shipment> findByOrderId(String orderId)`
  - `Shipment save(Shipment shipment)`

### 8. `ReturnRepositoryPort`
- **Purpose**: Persistence interface for `ReturnRequest` and `Refund` models.
- **Contract Methods**:
  - `Optional<ReturnRequest> findReturnById(String returnId)`
  - `ReturnRequest saveReturn(ReturnRequest returnRequest)`
  - `Optional<Refund> findRefundById(String refundId)`
  - `Refund saveRefund(Refund refund)`

### 9. `AuditLogRepositoryPort`
- **Purpose**: Persistence interface for immutable administrative and operational `AuditEntry` records.
- **Contract Methods**:
  - `void save(AuditEntry entry)`
  - `List<AuditEntry> findAll()`
  - `List<AuditEntry> findByTargetAggregateId(String aggregateId)`

### 10. `PaymentGatewayPort`
- **Purpose**: External payment processor integration interface (Stripe, PayPal, banking rails).
- **Contract Methods**:
  - `boolean processPayment(String orderId, Money amount, String paymentToken)`
  - `boolean processRefund(String refundId, Money amount)`

### 11. `NotificationPort`
- **Purpose**: External communication gateway interface (Email, SMS, Push).
- **Contract Methods**:
  - `void sendEmailNotification(Email recipient, String subject, String body)`
