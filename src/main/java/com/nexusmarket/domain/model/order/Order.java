package com.nexusmarket.domain.model.order;

import com.nexusmarket.domain.model.common.Address;
import com.nexusmarket.domain.model.common.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a commercial Order in NexusMarket.
 * Enforces order state machine transition rules and finalized order immutability.
 */
public class Order {

    private final String orderId;
    private final String buyerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Address shippingAddress;
    private final Money totalAmount;

    public Order(String orderId, String buyerId, List<OrderItem> items, OrderStatus status, Address shippingAddress) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items list cannot be null or empty.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null.");
        }

        this.orderId = orderId.trim();
        this.buyerId = buyerId.trim();
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.status = status;
        this.shippingAddress = shippingAddress;

        Money calculatedTotal = Money.zero();
        for (OrderItem item : this.items) {
            calculatedTotal = calculatedTotal.add(item.calculateSubtotal());
        }
        this.totalAmount = calculatedTotal;
    }

    public Order(String orderId, String buyerId, List<OrderItem> items, Address shippingAddress) {
        this(orderId, buyerId, items, OrderStatus.PENDING_PAYMENT, shippingAddress);
    }

    public void markAsPaid() {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to PAID from status '" + status + "'.");
        }
        this.status = OrderStatus.PAID;
    }

    public void markAsDispatched() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to DISPATCHED from status '" + status + "'.");
        }
        this.status = OrderStatus.DISPATCHED;
    }

    public void finalizeDelivery() {
        if (status != OrderStatus.DISPATCHED) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to DELIVERED_FINALIZED from status '" + status + "'.");
        }
        this.status = OrderStatus.DELIVERED_FINALIZED;
    }

    public void cancel() {
        if (status == OrderStatus.DELIVERED_FINALIZED) {
            throw new IllegalStateException("Finalized Order Failure: Order '" + orderId + "' is finalized/delivered and cannot be cancelled (Validaciones Críticas 11).");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
