package application.domain.models;

import application.domain.enums.ReturnReason;
import application.domain.enums.ReturnStatus;

import java.util.Objects;

public class ReturnRequest {

    private String returnId;
    private String orderId;
    private String buyerId;
    private String productId;
    private ReturnReason reason;
    private ReturnStatus status;

    public ReturnRequest() {
        this.status = ReturnStatus.REQUESTED;
    }

    public ReturnRequest(String returnId, String orderId, String buyerId, String productId,
                         ReturnReason reason, ReturnStatus status) {
        setReturnId(returnId);
        setOrderId(orderId);
        setBuyerId(buyerId);
        setProductId(productId);
        setReason(reason);
        setStatus(status);
    }

    public ReturnRequest(String returnId, String orderId, String buyerId, String productId, ReturnReason reason) {
        this(returnId, orderId, buyerId, productId, reason, ReturnStatus.REQUESTED);
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        this.returnId = returnId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        this.orderId = orderId.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        this.buyerId = buyerId.trim();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        this.productId = productId.trim();
    }

    public ReturnReason getReason() {
        return reason;
    }

    public void setReason(ReturnReason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("Return reason cannot be null.");
        }
        this.reason = reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Return status cannot be null.");
        }
        this.status = status;
    }

    public void approve() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED returns can be approved.");
        }
        setStatus(ReturnStatus.APPROVED);
    }

    public void reject() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED returns can be rejected.");
        }
        setStatus(ReturnStatus.REJECTED);
    }

    public void confirmItemReceived() {
        if (status != ReturnStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED returns can be marked as ITEM_RECEIVED.");
        }
        setStatus(ReturnStatus.ITEM_RECEIVED);
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
