package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.enums.ShipmentStatus;
import application.domain.enums.WarehouseType;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Shipment;
import application.domain.models.Warehouse;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
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

class LogisticsDispatchServiceTest {

    private LogisticsDispatchService logisticsService;
    private MockShipmentRepository shipmentRepository;
    private MockOrderRepository orderRepository;
    private MockWarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        shipmentRepository = new MockShipmentRepository();
        orderRepository = new MockOrderRepository();
        warehouseRepository = new MockWarehouseRepository();
        logisticsService = new LogisticsDispatchService(shipmentRepository, orderRepository, warehouseRepository);
    }

    @Test
    @DisplayName("Should prepare shipment, dispatch with tracking, and confirm delivery")
    void shouldHandleCompleteShipmentLifecycle() {
        Order order = new Order("ORD-1", "BUY-1", List.of(new OrderItem("P-1", "Prod", new Money(100.00), 1)), new Address("Dest St", "City", "ST", "123", "USA"));
        order.markAsPaid();
        orderRepository.saveOrder(order);

        Warehouse warehouse = new Warehouse("WH-1", "Main Warehouse", new Address("WH St", "City", "ST", "123", "USA"), WarehouseType.MARKETPLACE, "ADM-1", true);
        warehouseRepository.save(warehouse);

        Shipment shipment = logisticsService.prepareShipment("SHIP-1", "ORD-1", "WH-1", new Address("Dest St", "City", "ST", "123", "USA"));
        assertEquals(ShipmentStatus.PREPARING, shipment.getStatus());

        logisticsService.dispatchShipment("SHIP-1", "DHL Express", "DHL-987654");
        assertEquals(ShipmentStatus.IN_TRANSIT, shipmentRepository.findById("SHIP-1").get().getStatus());
        assertEquals(OrderStatus.DISPATCHED, orderRepository.findOrderById("ORD-1").get().getStatus());

        logisticsService.confirmDelivery("SHIP-1");
        assertEquals(ShipmentStatus.DELIVERED, shipmentRepository.findById("SHIP-1").get().getStatus());
        assertEquals(OrderStatus.DELIVERED_FINALIZED, orderRepository.findOrderById("ORD-1").get().getStatus());
    }

    @Test
    @DisplayName("Should reject shipment preparation from inactive warehouse")
    void shouldRejectInactiveWarehouse() {
        Order order = new Order("ORD-1", "BUY-1", List.of(new OrderItem("P-1", "Prod", new Money(100.00), 1)), new Address("Dest St", "City", "ST", "123", "USA"));
        order.markAsPaid();
        orderRepository.saveOrder(order);

        Warehouse warehouse = new Warehouse("WH-1", "Main Warehouse", new Address("WH St", "City", "ST", "123", "USA"), WarehouseType.MARKETPLACE, "ADM-1", false);
        warehouseRepository.save(warehouse);

        assertThrows(InvalidDomainStateException.class, () ->
                logisticsService.prepareShipment("SHIP-1", "ORD-1", "WH-1", new Address("Dest St", "City", "ST", "123", "USA"))
        );
    }

    private static class MockShipmentRepository implements ShipmentRepositoryPort {
        private final Map<String, Shipment> storage = new HashMap<>();

        @Override public Optional<Shipment> findById(String shipmentId) { return Optional.ofNullable(storage.get(shipmentId)); }
        @Override public Optional<Shipment> findByOrderId(String orderId) { return storage.values().stream().filter(s -> s.getOrderId().equals(orderId)).findFirst(); }
        @Override public Shipment save(Shipment shipment) { storage.put(shipment.getShipmentId(), shipment); return shipment; }
    }

    private static class MockOrderRepository implements OrderRepositoryPort {
        private final Map<String, Order> orders = new HashMap<>();
        @Override public Optional<application.domain.models.Cart> findCartByBuyerId(String buyerId) { return Optional.empty(); }
        @Override public application.domain.models.Cart saveCart(application.domain.models.Cart cart) { return cart; }
        @Override public Optional<Order> findOrderById(String orderId) { return Optional.ofNullable(orders.get(orderId)); }
        @Override public List<Order> findOrdersByBuyerId(String buyerId) { return List.of(); }
        @Override public Order saveOrder(Order order) { orders.put(order.getOrderId(), order); return order; }
    }

    private static class MockWarehouseRepository implements WarehouseRepositoryPort {
        private final Map<String, Warehouse> storage = new HashMap<>();
        @Override public Optional<Warehouse> findById(String warehouseId) { return Optional.ofNullable(storage.get(warehouseId)); }
        @Override public List<Warehouse> findByOwnerId(String ownerId) { return List.of(); }
        @Override public Warehouse save(Warehouse warehouse) { storage.put(warehouse.getWarehouseId(), warehouse); return warehouse; }
    }
}
