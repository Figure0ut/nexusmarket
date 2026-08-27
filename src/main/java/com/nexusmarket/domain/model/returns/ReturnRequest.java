package com.nexusmarket.domain.model.returns;

import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a product Return Request.
 */
public class ReturnRequest {

    private final String returnId;
    private final String orderId;
    private final String buyerId;
    private final String productId;
    private final ReturnReason reason;
    private ReturnStatus status;

    public ReturnRequest(String returnId, String orderId, String buyerId, String productId,
                         ReturnReason reason, ReturnStatus status) {
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
        if (status == null) {
            throw new IllegalArgumentException("Return status cannot be null.");
        }

        this.returnId = returnId.trim();
        this.orderId = orderId.trim();
        this.buyerId = buyerId.trim();
        this.productId = productId.trim();
        this.reason = reason;
        this.status = status;
    }

    public ReturnRequest(String returnId, String orderId, String buyerId, String productId, ReturnReason reason) {
        this(returnId, orderId, buyerId, productId, reason, ReturnStatus.REQUESTED);
    }

    public void approve() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED returns can be approved.");
        }
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED returns can be rejected.");
        }
        this.status = ReturnStatus.REJECTED;
    }

    public void confirmItemReceived() {
        if (status != ReturnStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED returns can be marked as ITEM_RECEIVED.");
        }
        this.status = ReturnStatus.ITEM_RECEIVED;
    }

    public String getReturnId() {
        return returnId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getProductId() {
        return productId;
    }

    public ReturnReason getReason() {
        return reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnRequest that = (ReturnRequest) o;
        return Objects.equals(returnId, that.returnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }
}
