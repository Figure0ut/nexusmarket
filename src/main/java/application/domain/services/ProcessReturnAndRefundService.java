package application.domain.services;

import application.domain.enums.ReturnStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.valueobjects.Money;

/**
 * Atomic domain service responsible exclusively for receiving an approved return and executing the refund (OBJ-11).
 * <ul>
 *   <li>Physical products: the units come back to the warehouse they were sold from (RETURN movement);
 *       reusable units go back to available stock, non-reusable units are stored as damaged stock.</li>
 *   <li>Digital products: no inventory movement.</li>
 *   <li>The refunded amount is the amount paid for that line of the order (never typed by the client).</li>
 * </ul>
 */
public class ProcessReturnAndRefundService {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final OrderRepositoryPort orderRepositoryPort;

    public ProcessReturnAndRefundService(ReturnRepositoryPort returnRepositoryPort,
                                         InventoryRepositoryPort inventoryRepositoryPort,
                                         PaymentGatewayPort paymentGatewayPort,
                                         OrderRepositoryPort orderRepositoryPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (paymentGatewayPort == null) {
            throw new IllegalArgumentException("PaymentGatewayPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.paymentGatewayPort = paymentGatewayPort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Refund processReturnedItemAndRefund(String refundId, String returnId, boolean isReusable) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        if (refundId == null || refundId.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund ID cannot be null or empty.");
        }
        if (returnRepositoryPort.findRefundById(refundId.trim()).isPresent()) {
            throw new DomainException("Refund with ID '" + refundId + "' already exists.");
        }
        ReturnRequest request = returnRepositoryPort.findReturnById(returnId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Return request not found with ID: " + returnId));
        if (request.getStatus() != ReturnStatus.APPROVED) {
            throw new InvalidDomainStateException("Return request '" + returnId + "' is not APPROVED. Current status: " + request.getStatus());
        }
        Order order = orderRepositoryPort.findOrderById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + request.getOrderId()));
        OrderItem item = order.findItem(request.getProductId())
                .orElseThrow(() -> new InvalidDomainStateException("Product '" + request.getProductId() + "' is not part of order '" + order.getOrderId() + "'."));

        request.confirmItemReceived();

        if (item.isPhysical()) {
            Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(item.getProductId(), item.getWarehouseId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + item.getProductId()
                            + "' in warehouse '" + item.getWarehouseId() + "'."));
            inventory.registerReturn(item.getQuantity(), isReusable);
            inventoryRepositoryPort.save(inventory);
        }

        Money refundAmount = item.calculateSubtotal();
        Refund refund = new Refund(refundId.trim(), request.getReturnId(), request.getBuyerId(), refundAmount);
        if (paymentGatewayPort.processRefund(refund.getRefundId(), refundAmount)) {
            refund.process();
        } else {
            refund.fail();
        }
        returnRepositoryPort.saveReturn(request);
        return returnRepositoryPort.saveRefund(refund);
    }
}
