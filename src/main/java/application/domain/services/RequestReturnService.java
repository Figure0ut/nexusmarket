package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.enums.ReturnReason;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;

/**
 * Atomic domain service responsible exclusively for requesting a return on a delivered order.
 */
public class RequestReturnService {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;

    public RequestReturnService(ReturnRepositoryPort returnRepositoryPort, OrderRepositoryPort orderRepositoryPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public ReturnRequest requestReturn(String returnId, String orderId, String buyerId, String productId, ReturnReason reason) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (reason == null) {
            throw new IllegalArgumentException("Return reason cannot be null.");
        }

        Order order = orderRepositoryPort.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED_FINALIZED) {
            throw new InvalidDomainStateException("Cannot request return for order '" + orderId + "' with status: " + order.getStatus() + ". Order must be DELIVERED_FINALIZED.");
        }

        ReturnRequest returnRequest = new ReturnRequest(returnId, orderId, buyerId, productId, reason);
        return returnRepositoryPort.saveReturn(returnRequest);
    }
}
