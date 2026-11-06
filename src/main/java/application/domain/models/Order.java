package application.domain.models;

import application.domain.enums.OrderStatus;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order aggregate (Domain 7). Represents the formal commercial commitment of a buyer.
 * Lifecycle: CART (Cart aggregate) -> PENDING_PAYMENT -> PAID -> DISPATCHED -> DELIVERED_FINALIZED.
 */
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
        if (status == OrderStatus.CART) {
            throw new IllegalArgumentException("An order is created at checkout; the CART stage is represented by the Cart aggregate.");
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

    /**
     * Payment validated (PENDING_PAYMENT -> PAID).
     * Digital products are delivered immediately after payment (Domain 5), therefore an order
     * that only contains digital items is finalized right away.
     */
    public void markAsPaid() {
        ensureNotFinalized();
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to PAID from status '" + status + "'.");
        }
        setStatus(OrderStatus.PAID);
        if (!containsPhysicalItems()) {
            setStatus(OrderStatus.DELIVERED_FINALIZED);
        }
    }

    /**
     * Physical exit from the warehouse (PAID -> DISPATCHED).
     */
    public void markAsDispatched() {
        ensureNotFinalized();
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to DISPATCHED from status '" + status + "'.");
        }
        if (!containsPhysicalItems()) {
            throw new IllegalStateException("Order '" + orderId + "' only contains digital products and does not require dispatch.");
        }
        setStatus(OrderStatus.DISPATCHED);
    }

    /**
     * Delivery confirmed (DISPATCHED -> DELIVERED_FINALIZED).
     */
    public void finalizeDelivery() {
        ensureNotFinalized();
        if (status != OrderStatus.DISPATCHED) {
            throw new IllegalStateException("Order '" + orderId + "' cannot transition to DELIVERED_FINALIZED from status '" + status + "'.");
        }
        setStatus(OrderStatus.DELIVERED_FINALIZED);
    }

    public boolean isFinalized() {
        return status == OrderStatus.DELIVERED_FINALIZED;
    }

    public boolean containsPhysicalItems() {
        return items.stream().anyMatch(OrderItem::isPhysical);
    }

    public boolean containsProduct(String productId) {
        return items.stream().anyMatch(item -> item.getProductId().equals(productId));
    }

    public java.util.Optional<OrderItem> findItem(String productId) {
        return items.stream().filter(item -> item.getProductId().equals(productId)).findFirst();
    }

    /**
     * A finalized order can never be modified under any circumstance (section 11).
     */
    private void ensureNotFinalized() {
        if (isFinalized()) {
            throw new IllegalStateException("Finalized Order Failure: Order '" + orderId +
                    "' is DELIVERED_FINALIZED and cannot be modified under any circumstance.");
        }
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
