package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying an order by its identifier.
 */
public class GetOrderService {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrderService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Order getOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        return orderRepositoryPort.findOrderById(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
    }
}
