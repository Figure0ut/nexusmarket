package application.domain.services;

import application.domain.enums.ReturnStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.valueobjects.Money;

/**
 * Atomic domain service responsible exclusively for receiving a returned item,
 * updating stock accordingly (restocking vs damage mark), and executing the monetary refund.
 */
public class ProcessReturnAndRefundService {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;

    public ProcessReturnAndRefundService(ReturnRepositoryPort returnRepositoryPort,
                                         InventoryRepositoryPort inventoryRepositoryPort,
                                         PaymentGatewayPort paymentGatewayPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (paymentGatewayPort == null) {
            throw new IllegalArgumentException("PaymentGatewayPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.paymentGatewayPort = paymentGatewayPort;
    }

    public Refund processReturnedItemAndRefund(String refundId, String returnId, String warehouseId, boolean isReusable, Money refundAmount) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        if (refundId == null || refundId.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund ID cannot be null or empty.");
        }
        if (refundAmount == null) {
            throw new IllegalArgumentException("Refund amount cannot be null.");
        }

        ReturnRequest request = returnRepositoryPort.findReturnById(returnId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Return request not found with ID: " + returnId));

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
