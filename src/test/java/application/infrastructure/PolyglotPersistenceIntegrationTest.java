package application.infrastructure;

import application.domain.enums.OrderStatus;
import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PolyglotPersistenceIntegrationTest {

    @Autowired
    private ProductRepositoryPort productRepository;

    @Autowired
    private AuditLogRepositoryPort auditLogRepository;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private WarehouseRepositoryPort warehouseRepository;

    @Autowired
    private OrderRepositoryPort orderRepository;

    @Test
    @DisplayName("MongoDB: Save and retrieve product document with variants")
    void testMongoProductPersistence() {
        Product product = new Product("PROD-TEST-1", "SKU-TEST-1", "SEL-01", "Polyglot Gaming Laptop", 1500.00, ProductType.PHYSICAL, ProductStatus.PUBLISHED);
        product.addVariant(new ProductVariant("RAM", "32GB"));
        product.addVariant(new ProductVariant("Storage", "1TB SSD"));

        productRepository.save(product);

        Optional<Product> found = productRepository.findById("PROD-TEST-1");
        assertTrue(found.isPresent());
        assertEquals("Polyglot Gaming Laptop", found.get().getName());
        assertEquals(2, found.get().getVariants().size());
        assertEquals("RAM", found.get().getVariants().get(0).getName());
    }

    @Test
    @DisplayName("MongoDB: Save and retrieve system audit log document")
    void testMongoAuditLogPersistence() {
        AuditEntry entry = new AuditEntry("LOG-TEST-1", "USER-42", UserRole.BUYER, "USER_REGISTER", "USER-42", "Registered new buyer");
        auditLogRepository.save(entry);

        List<AuditEntry> logs = auditLogRepository.findByTargetAggregateId("USER-42");
        assertFalse(logs.isEmpty());
        assertEquals("USER_REGISTER", logs.get(0).getAction());
        assertEquals("USER-42", logs.get(0).getActorId());
    }

    @Test
    @DisplayName("SQL JPA: Save and retrieve user, warehouse, and order relational entities")
    void testSqlJpaPersistence() {
        // User (SQL)
        Buyer user = new Buyer("USR-SQL-1", "CC-SQL-1", "John Doe", new Email("john.polyglot@example.com"), UserStatus.BLOCKED,
                new Address("123 Maple St"), List.of(new Address("9 Office Rd")), "ENABLED");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById("USR-SQL-1");
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getFullName());
        assertEquals("CC-SQL-1", foundUser.get().getIdentityDocument());
        // Status and additional addresses of buyers are persisted (regression test)
        assertEquals(UserStatus.BLOCKED, foundUser.get().getStatus());
        assertEquals(1, ((Buyer) foundUser.get()).getAdditionalAddresses().size());
        assertTrue(userRepository.existsByIdentityDocument("CC-SQL-1"));

        // Warehouse (SQL)
        Warehouse warehouse = new Warehouse("WH-SQL-1", "Central Depot", "456 Industrial Way", WarehouseType.MARKETPLACE, null);
        warehouseRepository.save(warehouse);

        Optional<Warehouse> foundWh = warehouseRepository.findById("WH-SQL-1");
        assertTrue(foundWh.isPresent());
        assertEquals("Central Depot", foundWh.get().getName());

        // Order with Line Items (SQL)
        OrderItem item = new OrderItem("PROD-TEST-1", "Polyglot Gaming Laptop", ProductType.PHYSICAL, "WH-SQL-1", new Money(1500.00), 2);
        Order order = new Order("ORD-SQL-1", "USR-SQL-1", List.of(item), new Address("123 Maple St"));
        orderRepository.saveOrder(order);

        Optional<Order> foundOrder = orderRepository.findOrderById("ORD-SQL-1");
        assertTrue(foundOrder.isPresent());
        assertEquals(OrderStatus.PENDING_PAYMENT, foundOrder.get().getStatus());
        assertEquals(1, foundOrder.get().getItems().size());
        assertEquals("WH-SQL-1", foundOrder.get().getItems().get(0).getWarehouseId());
        assertEquals(new BigDecimal("3000.00"), foundOrder.get().getTotalAmount().getAmount());
    }
}
