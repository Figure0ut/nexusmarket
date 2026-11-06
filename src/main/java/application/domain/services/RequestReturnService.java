package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.models.ReturnRequest;
import application.domain.enums.ReturnReason;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;

/**
 * Atomic domain service responsible exclusively for requesting the return of a product of a delivered order
 * (OBJ-11). The order itself is NOT modified: a finalized order can never be modified (section 11);
 * the return is managed as an independent aggregate.
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
        if (returnRepositoryPort.findReturnById(returnId.trim()).isPresent()) {
            throw new DomainException("Return request with ID '" + returnId + "' already exists.");
        }
        Order order = orderRepositoryPort.findOrderById(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        if (!order.getBuyerId().equals(buyerId.trim())) {
            throw new InvalidDomainStateException("Order '" + orderId + "' does not belong to buyer '" + buyerId + "'.");
        }
        if (order.getStatus() != OrderStatus.DELIVERED_FINALIZED) {
            throw new InvalidDomainStateException("Cannot request return for order '" + orderId + "' with status: " + order.getStatus() + ". Order must be DELIVERED_FINALIZED.");
        }
        if (!order.containsProduct(productId.trim())) {
            throw new InvalidDomainStateException("Product '" + productId + "' is not part of order '" + orderId + "'.");
        }
        boolean alreadyRequested = returnRepositoryPort.findAllReturns().stream()
                .anyMatch(r -> r.getOrderId().equals(order.getOrderId()) && r.getProductId().equals(productId.trim())
                        && r.getStatus() != application.domain.enums.ReturnStatus.REJECTED);
        if (alreadyRequested) {
            throw new DomainException("A return for product '" + productId + "' of order '" + orderId + "' already exists.");
        }
        ReturnRequest returnRequest = new ReturnRequest(returnId.trim(), order.getOrderId(), order.getBuyerId(), productId.trim(), reason);
        return returnRepositoryPort.saveReturn(returnRequest);
    }
}
