package application.domain.models;

import application.domain.enums.OrderStatus;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {

    private String orderId;
    private String buyerId;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private Address shippingAddress;
    private Money totalAmount;

    public Order() {
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public Order(String orderId, String buyerId, List<OrderItem> items, OrderStatus status, Address shippingAddress) {
        setOrderId(orderId);
        setBuyerId(buyerId);
        setItems(items);
        setStatus(status);
        setShippingAddress(shippingAddress);
    }

    public Order(String orderId, String buyerId, List<OrderItem> items, Address shippingAddress) {
        this(orderId, buyerId, items, OrderStatus.PENDING_PAYMENT, shippingAddress);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        this.orderId = orderId.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        this.buyerId = buyerId.trim();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items list cannot be null or empty.");
        }
        this.items = new ArrayList<>(items);

        Money calculatedTotal = Money.zero();
        for (OrderItem item : this.items) {
            calculatedTotal = calculatedTotal.add(item.calculateSubtotal());
        }
        this.totalAmount = calculatedTotal;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        this.status = status;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null.");
        }
        this.shippingAddress = shippingAddress;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void markAsPaid() {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to PAID from status '" + status + "'.");
        }
        setStatus(OrderStatus.PAID);
    }

    public void markAsDispatched() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to DISPATCHED from status '" + status + "'.");
        }
        setStatus(OrderStatus.DISPATCHED);
    }

    public void finalizeDelivery() {
        if (status != OrderStatus.DISPATCHED) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to DELIVERED_FINALIZED from status '" + status + "'.");
        }
        setStatus(OrderStatus.DELIVERED_FINALIZED);
    }

    public void cancel() {
        if (status == OrderStatus.DELIVERED_FINALIZED) {
            throw new IllegalStateException("Finalized Order Failure: Order '" + orderId + "' is finalized/delivered and cannot be cancelled.");
        }
        setStatus(OrderStatus.CANCELLED);
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
