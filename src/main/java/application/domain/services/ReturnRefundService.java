package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.enums.ReturnReason;
import application.domain.enums.ReturnStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.valueobjects.Money;

public class ReturnRefundService {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;

    public ReturnRefundService(ReturnRepositoryPort returnRepositoryPort,
                               OrderRepositoryPort orderRepositoryPort,
                               InventoryRepositoryPort inventoryRepositoryPort,
                               PaymentGatewayPort paymentGatewayPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (paymentGatewayPort == null) {
            throw new IllegalArgumentException("PaymentGatewayPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.paymentGatewayPort = paymentGatewayPort;
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

    public ReturnRequest getReturnRequest(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        return returnRepositoryPort.findReturnById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return request not found with ID: " + returnId));
    }

    public void approveReturn(String returnId) {
        ReturnRequest request = getReturnRequest(returnId);
        request.approve();
        returnRepositoryPort.saveReturn(request);
    }

    public void rejectReturn(String returnId) {
        ReturnRequest request = getReturnRequest(returnId);
        request.reject();
        returnRepositoryPort.saveReturn(request);
    }

    public Refund processReturnedItemAndRefund(String refundId, String returnId, String warehouseId, boolean isReusable, Money refundAmount) {
        ReturnRequest request = getReturnRequest(returnId);
        if (request.getStatus() != ReturnStatus.APPROVED) {
            throw new InvalidDomainStateException("Return request '" + returnId + "' is not APPROVED. Current status: " + request.getStatus());
        }

        request.confirmItemReceived();

        inventoryRepositoryPort.findByProductAndWarehouse(request.getProductId(), warehouseId).ifPresent(inventory -> {
            if (isReusable) {
                inventory.addStock(1);
            } else {
                inventory.markAsDamaged(1);
            }
            inventoryRepositoryPort.save(inventory);
        });

        Refund refund = new Refund(refundId, returnId, request.getBuyerId(), refundAmount);
        boolean refundSuccess = paymentGatewayPort.processRefund(refundId, refundAmount);

        if (refundSuccess) {
            refund.process();
        } else {
            refund.fail();
        }

        returnRepositoryPort.saveReturn(request);
        return returnRepositoryPort.saveRefund(refund);
    }
}
