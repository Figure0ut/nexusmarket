# Comprehensive Output Ports Specification

Output Ports define outbound contract interfaces in Hexagonal Architecture through which domain services and application use cases interact with external infrastructure adapters (databases, payment gateways, email providers).

---

## Architecture Pattern

```
[ Domain Layer / Application Use Cases ] ──> ( Output Port Interface ) ──> [ Infrastructure Adapter Implementation ]
```

---

## Detailed Output Port Contracts

### 1. `UserRepositoryPort`
- **Purpose**: Persistence interface for `User`, `Buyer`, and `Seller` aggregate roots.
- **Contract Methods**:
  - `Optional<User> findById(String identifier)`
  - `Optional<User> findByEmail(Email email)`
  - `User save(User user)`
  - `boolean existsByEmail(Email email)`

### 2. `WarehouseRepositoryPort`
- **Purpose**: Persistence interface for `Warehouse` aggregates.
- **Contract Methods**:
  - `Optional<Warehouse> findById(String warehouseId)`
  - `List<Warehouse> findByOwnerId(String ownerId)`
  - `Warehouse save(Warehouse warehouse)`

### 3. `ProductRepositoryPort`
- **Purpose**: Persistence interface for `Product` catalog aggregates.
- **Contract Methods**:
  - `Optional<Product> findById(String productId)`
  - `Optional<Product> findBySku(SKU sku)`
  - `List<Product> findBySellerId(String sellerId)`
  - `Product save(Product product)`

### 4. `InventoryRepositoryPort`
- **Purpose**: Persistence interface for distributed `Inventory` aggregates.
- **Contract Methods**:
  - `Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId)`
  - `List<Inventory> findByProductId(String productId)`
  - `Inventory save(Inventory inventory)`

### 5. `OrderRepositoryPort`
- **Purpose**: Persistence interface for `Cart` and `Order` aggregates.
- **Contract Methods**:
  - `Optional<Cart> findCartByBuyerId(String buyerId)`
  - `Cart saveCart(Cart cart)`
  - `Optional<Order> findOrderById(String orderId)`
  - `List<Order> findOrdersByBuyerId(String buyerId)`
  - `Order saveOrder(Order order)`

### 6. `InvoiceRepositoryPort`
- **Purpose**: Persistence interface for `Invoice` aggregates.
- **Contract Methods**:
  - `Optional<Invoice> findById(String invoiceId)`
  - `Optional<Invoice> findByOrderId(String orderId)`
  - `Invoice save(Invoice invoice)`

### 7. `ShipmentRepositoryPort`
- **Purpose**: Persistence interface for logistics `Shipment` aggregates.
- **Contract Methods**:
  - `Optional<Shipment> findById(String shipmentId)`
  - `Optional<Shipment> findByOrderId(String orderId)`
  - `Shipment save(Shipment shipment)`

### 8. `ReturnRepositoryPort`
- **Purpose**: Persistence interface for `ReturnRequest` and `Refund` aggregates.
- **Contract Methods**:
  - `Optional<ReturnRequest> findReturnById(String returnId)`
  - `ReturnRequest saveReturn(ReturnRequest returnRequest)`
  - `Optional<Refund> findRefundById(String refundId)`
  - `Refund saveRefund(Refund refund)`

### 9. `PaymentGatewayPort`
- **Purpose**: External payment processor integration interface (e.g. Stripe, PayPal).
- **Contract Methods**:
  - `boolean processPayment(String orderId, Money amount, String paymentToken)`

### 10. `NotificationPort`
- **Purpose**: External communication gateway interface (Email, SMS, Push).
- **Contract Methods**:
  - `void sendEmailNotification(Email recipient, String subject, String body)`
