package application.domain.services;

import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying orders belonging to a specific buyer.
 */
public class GetOrdersByBuyerService {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrdersByBuyerService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public List<Order> getOrdersByBuyer(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        return orderRepositoryPort.findOrdersByBuyerId(buyerId.trim());
    }
}
