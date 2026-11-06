package application.domain;

import application.domain.enums.OrderStatus;
import application.domain.enums.ProductType;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private OrderItem physical() {
        return new OrderItem("P-1", "Laptop", ProductType.PHYSICAL, "W-1", new Money(1000), 2);
    }

    private OrderItem digital() {
        return new OrderItem("P-2", "E-book", ProductType.DIGITAL, null, new Money(10), 1);
    }

    @Test
    @DisplayName("The order lifecycle has exactly the five states of the specification")
    void lifecycleStates() {
        assertArrayEquals(new OrderStatus[]{OrderStatus.CART, OrderStatus.PENDING_PAYMENT, OrderStatus.PAID,
                OrderStatus.DISPATCHED, OrderStatus.DELIVERED_FINALIZED}, OrderStatus.values());
    }

    @Test
    @DisplayName("Physical order: PENDING_PAYMENT -> PAID -> DISPATCHED -> DELIVERED_FINALIZED")
    void physicalLifecycle() {
        Order order = new Order("ORD-1", "BUY-1", List.of(physical(), digital()), new Address("Street 1"));
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(new Money(2010), order.getTotalAmount());
        assertThrows(IllegalStateException.class, order::markAsDispatched);
        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());
        order.markAsDispatched();
        order.finalizeDelivery();
        assertTrue(order.isFinalized());
    }

    @Test
    @DisplayName("Digital-only order is delivered immediately after payment")
    void digitalOnlyIsDeliveredAfterPayment() {
        Order order = new Order("ORD-2", "BUY-1", List.of(digital()), new Address("Street 1"));
        order.markAsPaid();
        assertEquals(OrderStatus.DELIVERED_FINALIZED, order.getStatus());
    }

    @Test
    @DisplayName("A finalized order cannot be modified under any circumstance")
    void finalizedOrderIsImmutable() {
        Order order = new Order("ORD-3", "BUY-1", List.of(digital()), new Address("Street 1"));
        order.markAsPaid();
        assertThrows(IllegalStateException.class, order::markAsPaid);
        assertThrows(IllegalStateException.class, order::markAsDispatched);
        assertThrows(IllegalStateException.class, order::finalizeDelivery);
    }

    @Test
    @DisplayName("An order cannot be created with the CART status (the Cart aggregate represents it)")
    void orderCannotBeCart() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-4", "BUY-1", List.of(physical()), OrderStatus.CART, new Address("Street 1")));
    }
}
