package com.nexusmarket.domain.model.order;

/**
 * Value Object representing order lifecycle status state machine in NexusMarket.
 */
public enum OrderStatus {
    CART,
    PENDING_PAYMENT,
    PAID,
    DISPATCHED,
    DELIVERED_FINALIZED,
    CANCELLED
}
