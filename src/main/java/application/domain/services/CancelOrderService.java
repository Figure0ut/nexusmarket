package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;

/**
 * Atomic domain service responsible exclusively for cancelling an order.
 */
public class CancelOrderService {

    private final OrderRepositoryPort orderRepositoryPort;

    public CancelOrderService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public void cancelOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }

        Order order = orderRepositoryPort.findOrderById(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        order.cancel();
        orderRepositoryPort.saveOrder(order);
    }
}
