package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.enums.RefundStatus;
import application.domain.enums.ReturnReason;
import application.domain.enums.ReturnStatus;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReturnRefundServiceTest {

    private ReturnRefundService returnService;
    private MockReturnRepository returnRepository;
    private MockOrderRepository orderRepository;
    private MockInventoryRepository inventoryRepository;
    private MockPaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        returnRepository = new MockReturnRepository();
        orderRepository = new MockOrderRepository();
        inventoryRepository = new MockInventoryRepository();
        paymentGateway = new MockPaymentGateway();
        returnService = new ReturnRefundService(returnRepository, orderRepository, inventoryRepository, paymentGateway);
    }

    @Test
    @DisplayName("Should request return, approve, inspect item, restock and process refund")
    void shouldHandleCompleteReturnAndRefund() {
        Order order = new Order("ORD-1", "BUY-1", List.of(new OrderItem("P-1", "Prod", new Money(100.00), 1)), new Address("Dest St", "City", "ST", "123", "USA"));
        order.markAsPaid();
        order.markAsDispatched();
        order.finalizeDelivery();
        orderRepository.saveOrder(order);

        Inventory inventory = new Inventory("INV-1", "P-1", "WH-1", 10);
        inventoryRepository.save(inventory);

        ReturnRequest req = returnService.requestReturn("RET-1", "ORD-1", "BUY-1", "P-1", ReturnReason.DEFECTIVE);
        assertEquals(ReturnStatus.REQUESTED, req.getStatus());

        returnService.approveReturn("RET-1");
        assertEquals(ReturnStatus.APPROVED, returnRepository.findReturnById("RET-1").get().getStatus());

        Refund refund = returnService.processReturnedItemAndRefund("REF-1", "RET-1", "WH-1", true, new Money(100.00));
        assertNotNull(refund);
        assertEquals(RefundStatus.PROCESSED, refund.getStatus());
        assertEquals(ReturnStatus.ITEM_RECEIVED, returnRepository.findReturnById("RET-1").get().getStatus());
        assertEquals(11, inventoryRepository.findByProductAndWarehouse("P-1", "WH-1").get().getAvailableStock().getValue());
    }

    private static class MockReturnRepository implements ReturnRepositoryPort {
        private final Map<String, ReturnRequest> returns = new HashMap<>();
        private final Map<String, Refund> refunds = new HashMap<>();

        @Override public Optional<ReturnRequest> findReturnById(String returnId) { return Optional.ofNullable(returns.get(returnId)); }
        @Override public ReturnRequest saveReturn(ReturnRequest returnRequest) { returns.put(returnRequest.getReturnId(), returnRequest); return returnRequest; }
        @Override public Optional<Refund> findRefundById(String refundId) { return Optional.ofNullable(refunds.get(refundId)); }
        @Override public Refund saveRefund(Refund refund) { refunds.put(refund.getRefundId(), refund); return refund; }
    }

    private static class MockOrderRepository implements OrderRepositoryPort {
        private final Map<String, Order> orders = new HashMap<>();
        @Override public Optional<application.domain.models.Cart> findCartByBuyerId(String buyerId) { return Optional.empty(); }
        @Override public application.domain.models.Cart saveCart(application.domain.models.Cart cart) { return cart; }
        @Override public Optional<Order> findOrderById(String orderId) { return Optional.ofNullable(orders.get(orderId)); }
        @Override public List<Order> findOrdersByBuyerId(String buyerId) { return List.of(); }
        @Override public Order saveOrder(Order order) { orders.put(order.getOrderId(), order); return order; }
    }

    private static class MockInventoryRepository implements InventoryRepositoryPort {
        private final Map<String, Inventory> storage = new HashMap<>();
        @Override public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) { return Optional.ofNullable(storage.get(productId + "_" + warehouseId)); }
        @Override public List<Inventory> findByProductId(String productId) { return List.of(); }
        @Override public Inventory save(Inventory inventory) { storage.put(inventory.getProductId() + "_" + inventory.getWarehouseId(), inventory); return inventory; }
    }

    private static class MockPaymentGateway implements PaymentGatewayPort {
        @Override public boolean processPayment(String orderId, Money amount, String paymentToken) { return true; }
        @Override public boolean processRefund(String refundId, Money amount) { return true; }
    }
}
