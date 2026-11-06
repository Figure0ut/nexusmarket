package application.domain.services;

import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for listing all orders (operational follow-up).
 */
public class GetAllOrdersService {

    private final OrderRepositoryPort repositoryPort;

    public GetAllOrdersService(OrderRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public List<Order> getAllOrders() {
        return repositoryPort.findAllOrders();
    }
}
