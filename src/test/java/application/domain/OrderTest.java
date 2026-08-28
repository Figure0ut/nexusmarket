package application.domain;

import application.domain.enums.OrderStatus;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Should create order, compute total, and transition through order lifecycle")
    void shouldProcessOrderLifecycle() {
        OrderItem item1 = new OrderItem("PROD-1", "Laptop", new Money(1000.00), 1);
        OrderItem item2 = new OrderItem("PROD-2", "Mouse", new Money(50.00), 2);
        Address address = new Address("123 Market St", "Metropolis", "NY", "10001", "USA");

        Order order = new Order("ORD-001", "BUY-101", List.of(item1, item2), address);

        assertEquals(new Money(1100.00), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());

        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());

        order.markAsDispatched();
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());

        order.finalizeDelivery();
        assertEquals(OrderStatus.DELIVERED_FINALIZED, order.getStatus());
    }

    @Test
    @DisplayName("Should prevent cancellation of finalized/delivered orders")
    void shouldRejectCancellationOfFinalizedOrder() {
        OrderItem item = new OrderItem("PROD-1", "Laptop", new Money(1000.00), 1);
        Address address = new Address("123 Market St", "Metropolis", "NY", "10001", "USA");

        Order order = new Order("ORD-001", "BUY-101", List.of(item), address);
        order.markAsPaid();
        order.markAsDispatched();
        order.finalizeDelivery();

        IllegalStateException ex = assertThrows(IllegalStateException.class, order::cancel);
        assertTrue(ex.getMessage().contains("is finalized/delivered and cannot be cancelled"));
    }
}
