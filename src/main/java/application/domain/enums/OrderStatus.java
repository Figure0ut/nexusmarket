package application.domain.enums;

/**
 * Represents the lifecycle states of an Order.
 * Note: Cart state is managed by the separate {@code Cart} aggregate;
 * Orders always begin in {@code PENDING_PAYMENT}.
 */
public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    DISPATCHED,
    DELIVERED_FINALIZED,
    CANCELLED
}
